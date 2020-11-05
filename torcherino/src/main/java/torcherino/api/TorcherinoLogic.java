package torcherino.api;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import torcherino.Torcherino;
import torcherino.api.blocks.entity.TorcherinoBlockEntity;
import torcherino.config.Config;

import java.util.Random;
import java.util.function.Consumer;

public class TorcherinoLogic
{
    public static void scheduledTick(final BlockState state, final ServerLevel level, final BlockPos pos, final Random random)
    {
        if (level.isClientSide) { return; }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        // todo: figure out scheduled tick
        //if (blockEntity instanceof TorcherinoBlockEntity) { ((TorcherinoBlockEntity) blockEntity).tick(); }
    }

    public static InteractionResult onUse(final BlockState state, final Level level, final BlockPos pos, final Player player,
                                          final InteractionHand hand, final BlockHitResult hit)
    {
        if (level.isClientSide || hand == InteractionHand.OFF_HAND) { return InteractionResult.SUCCESS; }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity)
        {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            ((TorcherinoBlockEntity) blockEntity).writeClientData(buffer);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new ResourceLocation(Torcherino.MOD_ID, "ots"), buffer);
        }
        return InteractionResult.SUCCESS;
    }

    public static void neighborUpdate(final BlockState state, final Level level, final BlockPos pos, final Block neighborBlock,
                                      final BlockPos neighborPos, final boolean boolean_1, final Consumer<TorcherinoBlockEntity> func)
    {
        if (level.isClientSide) { return; }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity) { func.accept((TorcherinoBlockEntity) blockEntity); }
    }

    public static void onPlaced(final Level level, final BlockPos pos, final BlockState state, final LivingEntity placer,
                                final ItemStack stack, final Block block)
    {
        if (level.isClientSide) { return; }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity)
        {
            TorcherinoBlockEntity be = (TorcherinoBlockEntity) blockEntity;
            if (stack.hasCustomHoverName()) { be.setCustomName(stack.getHoverName()); }
            if (!Config.INSTANCE.online_mode.equals("")) { be.setOwner(placer == null ? "" : placer.getStringUUID()); }
        }
        if (Config.INSTANCE.log_placement)
        {
            String prefix = placer == null ? "Something" : placer.getDisplayName().getString() + "(" + placer.getStringUUID() + ")";
            Torcherino.LOGGER.info("[Torcherino] {} placed a {} at {}, {}, {}.", prefix, Registry.BLOCK.getKey(block), pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
