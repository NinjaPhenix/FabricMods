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

    public static void scheduledTick(BlockState state, ServerLevel world, BlockPos pos, Random random)
    {
        if (world.isClientSide) { return; }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity) { ((TorcherinoBlockEntity) blockEntity).tick(); }
    }

    public static InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (world.isClientSide || hand == InteractionHand.OFF_HAND) { return InteractionResult.SUCCESS; }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity)
        {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            ((TorcherinoBlockEntity) blockEntity).writeClientData(buffer);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new ResourceLocation(Torcherino.MOD_ID, "ots"), buffer);
        }
        return InteractionResult.SUCCESS;
    }

    public static void neighborUpdate(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean boolean_1,
            Consumer<TorcherinoBlockEntity> func)
    {
        if (world.isClientSide) { return; }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity) { func.accept((TorcherinoBlockEntity) blockEntity); }
    }

    public static void onPlaced(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, Block block)
    {
        if (world.isClientSide) { return; }
        BlockEntity blockEntity = world.getBlockEntity(pos);
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
