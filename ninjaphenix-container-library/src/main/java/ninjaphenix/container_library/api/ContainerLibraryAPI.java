package ninjaphenix.container_library.api;

import java.util.Set;
import java.util.function.Function;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ninjaphenix.container_library.api.common.inventory.ServerScreenHandlerFactory;
import ninjaphenix.container_library.impl.common.NewContainerLibrary;

import static ninjaphenix.container_library.api.common.inventory.AbstractScreenHandler.ScreenMeta;

public interface ContainerLibraryAPI
{
    ContainerLibraryAPI INSTANCE = NewContainerLibrary.INSTANCE;

    /**
     * Declares a new container type that can be picked by clients.
     */
    void declareContainerType(final Identifier id, final Identifier texture, final Text name);

    void registerScreenMetaDeserializer(final Identifier id, final Function<PacketByteBuf, ScreenMeta> deserializer);

    Function<PacketByteBuf, ScreenMeta> getScreenMetaDeserializer(final Identifier deserializerId);

    Set<Identifier> getContainerTypes();

    ServerScreenHandlerFactory<?, ?> getServerContainerFactory(final Identifier preference);

    ExtendedScreenHandlerFactory createScreenHandlerFactory(final Identifier preference, final ScreenMeta meta,
                                                            final Inventory inventory, final Text containerName);
}
