package ninjaphenix.container_library.impl.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ninjaphenix.container_library.api.ContainerLibraryAPI;
import ninjaphenix.container_library.api.common.inventory.ServerScreenHandlerFactory;
import ninjaphenix.container_library.impl.client.NewContainerLibraryClient;
import ninjaphenix.container_library.api.common.inventory.AbstractScreenHandler.ScreenMeta;

public class NewContainerLibrary implements ContainerLibraryAPI
{
    public static final NewContainerLibrary INSTANCE = new NewContainerLibrary();
    private final Set<Identifier> containerTypes = new HashSet<>();
    private final Map<Identifier, Function<PacketByteBuf, ScreenMeta>> deserializers = new HashMap<>();
    private final Map<Identifier, ServerScreenHandlerFactory<?, ?>> containerFactories = new HashMap<>();

    @Override
    public void declareContainerType(final Identifier id, final Identifier texture, final Text name)
    {
        containerTypes.add(id);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
        {
            NewContainerLibraryClient.INSTANCE.declareContainerPickButton(id, texture, name);
        }
    }

    public void registerServerContainerFactory(final Identifier id, final ServerScreenHandlerFactory<?, ?> factory)
    {
        containerFactories.putIfAbsent(id, factory);
    }

    @Override
    public void registerScreenMetaDeserializer(final Identifier id, final Function<PacketByteBuf, ScreenMeta> deserializer)
    {
        deserializers.putIfAbsent(id, deserializer);
    }

    @Override
    public Function<PacketByteBuf, ScreenMeta> getScreenMetaDeserializer(final Identifier id) { return deserializers.get(id); }

    @Override
    public Set<Identifier> getContainerTypes() { return new HashSet<>(containerTypes); }

    @Override
    public ServerScreenHandlerFactory<?, ?> getServerContainerFactory(final Identifier id)
    {
        return containerFactories.get(id);
    }

    @Override
    public ExtendedScreenHandlerFactory createScreenHandlerFactory(final Identifier preference, final ScreenMeta meta,
                                                                   final Inventory inventory, final Text containerName)
    {
        return null;
    }
}