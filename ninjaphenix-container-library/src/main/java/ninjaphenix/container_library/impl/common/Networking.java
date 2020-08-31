package ninjaphenix.container_library.impl.common;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import ninjaphenix.container_library.api.common.inventory.AbstractScreenHandler.ScreenMeta;
import ninjaphenix.container_library.api.common.inventory.ExtendedScreenHandlerFactoryProvider;
import ninjaphenix.container_library.impl.client.NewContainerLibraryClient;
import java.util.HashSet;
import java.util.Set;

public class Networking
{
    public static final Networking INSTANCE = new Networking();

    private Networking() { }

    public void registerClientHandlers()
    {
        ClientSidePacketRegistry.INSTANCE.register(Const.RECEIVE_SERVER_CONTAINERS, this::receiveServerContainers);
    }

    private void receiveServerContainers(final PacketContext context, final PacketByteBuf buffer)
    {
        final HashSet<Identifier> serverContainers = new HashSet<>();
        final int amount = buffer.readInt();
        for (int i = 0; i < amount; i++)
        {
            serverContainers.add(buffer.readIdentifier());
        }
        NewContainerLibraryClient.INSTANCE.setServerSupportedContainers(serverContainers);
    }

    public void registerServerHandlers()
    {
        ServerSidePacketRegistry.INSTANCE.register(Const.OPEN_CONTAINER, this::handleOpenContainerRequest);
    }

    private void handleOpenContainerRequest(final PacketContext context, final PacketByteBuf buffer)
    {
        final ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
        final BlockPos pos = buffer.readBlockPos();
        final Identifier preference = buffer.readIdentifier();
        final Identifier screenMetaDeserializeId = buffer.readIdentifier();
        final ScreenMeta meta = NewContainerLibrary.INSTANCE.getScreenMetaDeserializer(screenMetaDeserializeId).apply(buffer);
        final ServerWorld world = player.getServerWorld();
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();
        if (block instanceof ExtendedScreenHandlerFactoryProvider)
        {
            final ExtendedScreenHandlerFactory factory = ((ExtendedScreenHandlerFactoryProvider) block)
                    .createFactory(preference, meta, state, world, pos);
            player.openHandledScreen(factory);
        }
    }

    public void requestContainerOpen(final BlockPos pos, final Identifier preference, final ScreenMeta meta)
    {
        final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeBlockPos(pos);
        buffer.writeIdentifier(preference);
        meta.serialize(buffer);
        ClientSidePacketRegistry.INSTANCE.sendToServer(Const.OPEN_CONTAINER, buffer);
    }

    public void sendServerContainersTo(final PlayerEntity player)
    {
        final Set<Identifier> containerTypes = NewContainerLibrary.INSTANCE.getContainerTypes();
        final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeInt(containerTypes.size());
        containerTypes.forEach(buffer::writeIdentifier);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Const.RECEIVE_SERVER_CONTAINERS, buffer);
    }
}
