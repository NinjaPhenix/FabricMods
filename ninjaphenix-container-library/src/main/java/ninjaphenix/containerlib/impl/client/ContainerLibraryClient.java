package ninjaphenix.containerlib.impl.client;

import blue.endless.jankson.JsonPrimitive;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import ninjaphenix.chainmail.api.config.JanksonConfigParser;
import ninjaphenix.containerlib.api.Constants;
import ninjaphenix.containerlib.impl.ContainerLibraryImpl;
import ninjaphenix.containerlib.impl.client.config.Config;
import ninjaphenix.containerlib.impl.client.screen.PagedScreen;
import ninjaphenix.containerlib.impl.client.screen.ScrollableScreen;
import ninjaphenix.containerlib.impl.client.screen.SelectContainerScreen;
import ninjaphenix.containerlib.impl.client.screen.SingleScreen;
import ninjaphenix.containerlib.impl.inventory.PagedContainer;
import ninjaphenix.containerlib.impl.inventory.ScrollableContainer;
import ninjaphenix.containerlib.impl.inventory.SingleContainer;
import org.apache.logging.log4j.MarkerManager;

import java.nio.file.Path;
import java.util.HashMap;

import static ninjaphenix.containerlib.api.Constants.LIBRARY_ID;
import static ninjaphenix.containerlib.api.Constants.SCREEN_SELECT;

public final class ContainerLibraryClient implements ClientModInitializer
{
    public static final ContainerLibraryClient INSTANCE = new ContainerLibraryClient();
    public static final Config CONFIG = getConfigParser().load(Config.class, getConfigPath(), new MarkerManager.Log4jMarker(LIBRARY_ID));

    private static JanksonConfigParser getConfigParser()
    {
        return new JanksonConfigParser.Builder()
                .deSerializer(JsonPrimitive.class, Identifier.class, (it, marshaller) -> new Identifier(it.asString()),
                        ((identifier, marshaller) -> marshaller.serialize(identifier.toString())))
                .build();
    }

    private static Path getConfigPath() { return FabricLoader.getInstance().getConfigDirectory().toPath().resolve("ninjaphenix-container-library.json"); }

    private ContainerLibraryClient() {}

    public static void sendPreferencesToServer()
    {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeIdentifier(CONFIG.preferred_container_type);
        ClientSidePacketRegistry.INSTANCE.sendToServer(Constants.SCREEN_SELECT, buffer);
    }

    public static void sendCallbackRemoveToServer() {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeIdentifier(Constants.id("auto"));
        ClientSidePacketRegistry.INSTANCE.sendToServer(Constants.SCREEN_SELECT, buffer);
    }

    public static void setPreference(Identifier container_type)
    {
        CONFIG.preferred_container_type = container_type;
        getConfigParser().save(CONFIG, getConfigPath(), new MarkerManager.Log4jMarker(LIBRARY_ID));
    }

    @Override
    public void onInitializeClient()
    {
        ScreenProviderRegistry.INSTANCE.registerFactory(Constants.SINGLE_CONTAINER, (ContainerScreenFactory<SingleContainer>) SingleScreen::new);
        ScreenProviderRegistry.INSTANCE.registerFactory(Constants.PAGED_CONTAINER, (ContainerScreenFactory<PagedContainer>) PagedScreen::new);
        ScreenProviderRegistry.INSTANCE.registerFactory(Constants.SCROLLABLE_CONTAINER, (ContainerScreenFactory<ScrollableContainer>) ScrollableScreen::new);

        ClientSidePacketRegistry.INSTANCE.register(SCREEN_SELECT, (context, buffer) ->
        {
            final int count = buffer.readInt();
            final HashMap<Identifier, ScreenMiscSettings> allowed = new HashMap<>();
            for (int i = 0; i < count; i++)
            {
                final Identifier containerFactoryId = buffer.readIdentifier();
                if (ContainerLibraryImpl.INSTANCE.isContainerTypeDeclared(containerFactoryId) /*&&
                        ContainerProviderRegistry.INSTANCE.factoryExists(containerFactoryId)*/)
                {
                    allowed.put(containerFactoryId, ContainerLibraryImpl.INSTANCE.getScreenSettings(containerFactoryId));
                }
            }
            MinecraftClient.getInstance().openScreen(new SelectContainerScreen(allowed));
        });
    }
}
