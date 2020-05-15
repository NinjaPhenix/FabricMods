package ninjaphenix.containerlib.client;

import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.DeserializationException;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Container;
import ninjaphenix.chainmail.api.config.JanksonConfigParser;
import ninjaphenix.containerlib.ContainerLibrary;
import ninjaphenix.containerlib.client.config.Config;
import ninjaphenix.containerlib.client.screen.FixedScreen;
import ninjaphenix.containerlib.client.screen.PagedScreen;
import ninjaphenix.containerlib.client.screen.ScreenType;
import ninjaphenix.containerlib.client.screen.ScrollingScreen;
import ninjaphenix.containerlib.inventory.CContainer;
import org.apache.logging.log4j.MarkerManager;

public final class ContainerLibraryClient implements ClientModInitializer
{
    public static final ContainerLibraryClient INSTANCE = new ContainerLibraryClient();
    private static final JanksonConfigParser PARSER = new JanksonConfigParser.Builder()
            .deSerializer(JsonPrimitive.class, ScreenType.class, (jsonPrimitive, marshaller) -> {
                switch (jsonPrimitive.asString())
                {
                    case "FIXED": return ScreenType.FIXED;
                    case "SCROLLING": return ScreenType.SCROLLING;
                    case "PAGED": return ScreenType.PAGED;
                    default: throw new DeserializationException("Invalid screen type, must be FIXED, SCROLLING or PAGED.");
                }
            }, (screenType, marshaller) -> new JsonPrimitive(screenType.name())).build();
    public static final Config CONFIG = PARSER.load(Config.class,
            FabricLoader.getInstance().getConfigDirectory().toPath().resolve("ninjaphenix-container-library.json"),
            new MarkerManager.Log4jMarker("ninjaphenix-container-library"));

    private ContainerLibraryClient() {}

    @Override
    public void onInitializeClient()
    {
        ScreenProviderRegistry.INSTANCE.registerFactory(ContainerLibrary.CONTAINER_ID, this::makeScreen);
    }

    private ContainerScreen<CContainer> makeScreen(Container container)
    {
        if (container instanceof CContainer)
        {
            final CContainer cContainer = (CContainer) container;
            switch (CONFIG.screen_type)
            {

                case FIXED: return new FixedScreen<>(cContainer);
                case SCROLLING: return new ScrollingScreen<>(cContainer);
                case PAGED: return new PagedScreen<>(cContainer);
            }
        }
        return null;
    }
}
