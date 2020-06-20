package torcherino.api;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import torcherino.Torcherino;
import torcherino.api.blocks.entity.TorcherinoBlockEntity;
import torcherino.config.Config;

import java.util.Random;
import java.util.function.Consumer;

public class TorcherinoLogic
{

    public static void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (world.isClient) { return; }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity) { ((TorcherinoBlockEntity) blockEntity).tick(); }
    }

    public static ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.isClient || hand == Hand.OFF_HAND) { return ActionResult.SUCCESS; }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity)
        {
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            ((TorcherinoBlockEntity) blockEntity).writeClientData(buffer);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new Identifier(Torcherino.MOD_ID, "ots"), buffer);
        }
        return ActionResult.SUCCESS;
    }

    public static void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean boolean_1,
            Consumer<TorcherinoBlockEntity> func)
    {
        if (world.isClient) { return; }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity) { func.accept((TorcherinoBlockEntity) blockEntity); }
    }

    public static void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, Block block)
    {
        if (world.isClient) { return; }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity)
        {
            TorcherinoBlockEntity be = (TorcherinoBlockEntity) blockEntity;
            if (stack.hasCustomName()) { be.setCustomName(stack.getName()); }
            if (!Config.INSTANCE.online_mode.equals("")) { be.setOwner(placer == null ? "" : placer.getUuidAsString()); }
        }
        if (Config.INSTANCE.log_placement)
        {
            String prefix = placer == null ? "Something" : placer.getDisplayName().getString() + "(" + placer.getUuidAsString() + ")";
            Torcherino.LOGGER.info("[Torcherino] {} placed a {} at {}, {}, {}.", prefix, Registry.BLOCK.getId(block), pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
