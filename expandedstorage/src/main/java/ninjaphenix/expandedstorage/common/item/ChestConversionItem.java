package ninjaphenix.expandedstorage.common.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import ninjaphenix.expandedstorage.common.Const;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.BarrelBlock;
import ninjaphenix.expandedstorage.common.block.CursedChestBlock;
import ninjaphenix.expandedstorage.common.block.StorageBlock;
import ninjaphenix.expandedstorage.common.block.entity.StorageBlockEntity;
import ninjaphenix.expandedstorage.common.misc.CursedChestType;

public final class ChestConversionItem extends ChestModifierItem
{
    private final Component TOOLTIP;
    private final ResourceLocation FROM, TO;
    private static final MutableComponent DOUBLE_REQUIRES_2 = new TranslatableComponent("tooltip.expandedstorage.conversion_kit_double_requires_2")
            .withStyle(ChatFormatting.GRAY);

    public ChestConversionItem(final Item.Properties settings, final Tuple<ResourceLocation, String> from, final Tuple<ResourceLocation, String> to)
    {
        super(settings);
        FROM = from.getA();
        TO = to.getA();
        TOOLTIP = new TranslatableComponent(String.format("tooltip.expandedstorage.conversion_kit_%s_%s", from.getB(), to.getB()),
                                       Const.leftShiftRightClick).withStyle(ChatFormatting.GRAY);
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    private void upgradeCursedChest(final Level world, final BlockPos pos, final BlockState state)
    {
        StorageBlockEntity blockEntity = (StorageBlockEntity) world.getBlockEntity(pos);
        final MappedRegistry<Registries.TierData> registry = ((StorageBlock) state.getBlock()).getDataRegistry();
        final NonNullList<ItemStack> inventoryData = NonNullList.withSize(registry.get(TO).getSlotCount(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(blockEntity.save(new CompoundTag()), inventoryData);
        world.removeBlockEntity(pos);
        BlockState newState = Registry.BLOCK.get(registry.get(TO).getBlockId()).defaultBlockState();
        if (newState.getBlock() instanceof SimpleWaterloggedBlock)
        {
            newState = newState.setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));
        }
        world.setBlockAndUpdate(pos, newState.setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING))
                .setValue(CursedChestBlock.TYPE, state.getValue(CursedChestBlock.TYPE)));
        blockEntity = (StorageBlockEntity) world.getBlockEntity(pos);
        blockEntity.load(world.getBlockState(pos), ContainerHelper.saveAllItems(blockEntity.save(new CompoundTag()), inventoryData));
    }

    @SuppressWarnings({"ConstantConditions"})
    private void upgradeChest(final Level world, final BlockPos pos, final BlockState state)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        final NonNullList<ItemStack> inventoryData = NonNullList.withSize(Registries.CHEST.get(TO).getSlotCount(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(blockEntity.save(new CompoundTag()), inventoryData);
        world.removeBlockEntity(pos);
        final BlockState newState = Registry.BLOCK.get(Registries.CHEST.get(TO).getBlockId()).defaultBlockState();
        world.setBlockAndUpdate(pos, newState.setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING))
                .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED))
                .setValue(CursedChestBlock.TYPE, CursedChestType.valueOf(state.getValue(BlockStateProperties.CHEST_TYPE))));
        blockEntity = world.getBlockEntity(pos);
        blockEntity.load(world.getBlockState(pos), ContainerHelper.saveAllItems(blockEntity.save(new CompoundTag()), inventoryData));
    }

    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    protected InteractionResult useModifierOnChestBlock(final UseOnContext context, final BlockState mainState, final BlockPos mainBlockPos,
                                                   final BlockState otherState, final BlockPos otherBlockPos)
    {
        final Level world = context.getLevel();
        final Player player = context.getPlayer();
        final StorageBlock chestBlock = (StorageBlock) mainState.getBlock();
        if (chestBlock.TIER_ID != FROM) { return InteractionResult.FAIL; }
        final ItemStack handStack = player.getItemInHand(context.getHand());
        if (otherBlockPos == null)
        {
            if (!world.isClientSide)
            {
                upgradeCursedChest(world, mainBlockPos, mainState);
                handStack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        else if (handStack.getCount() > 1 || player.isCreative())
        {
            if (!world.isClientSide)
            {
                upgradeCursedChest(world, otherBlockPos, world.getBlockState(otherBlockPos));
                upgradeCursedChest(world, mainBlockPos, mainState);
                handStack.shrink(2);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected InteractionResult useModifierOnBarrel(final UseOnContext context, final BlockState state, final BlockPos pos)
    {
        final BarrelBlock block = (BarrelBlock) state.getBlock();
        if (block.TIER_ID != FROM) { return InteractionResult.FAIL; }
        upgradeBarrel(context.getLevel(), pos, state);
        context.getPlayer().getItemInHand(context.getHand()).shrink(1);
        return InteractionResult.SUCCESS;
    }

    private void upgradeVanillaBarrel(final Level world, final BlockPos pos, final BlockState state)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        final NonNullList<ItemStack> inventoryData = NonNullList.withSize(Registries.BARREL.get(TO).getSlotCount(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(blockEntity.save(new CompoundTag()), inventoryData);
        world.removeBlockEntity(pos);
        final BlockState newState = Registry.BLOCK.get(Registries.BARREL.get(TO).getBlockId()).defaultBlockState();
        world.setBlockAndUpdate(pos, newState.setValue(BlockStateProperties.FACING, state.getValue(BlockStateProperties.FACING)));
        blockEntity = world.getBlockEntity(pos);
        blockEntity.load(world.getBlockState(pos), ContainerHelper.saveAllItems(blockEntity.save(new CompoundTag()), inventoryData));
    }

    private void upgradeBarrel(final Level world, final BlockPos pos, final BlockState state)
    {
        StorageBlockEntity blockEntity = (StorageBlockEntity) world.getBlockEntity(pos);
        final MappedRegistry<Registries.TierData> registry = ((StorageBlock) state.getBlock()).getDataRegistry();
        final NonNullList<ItemStack> inventoryData = NonNullList.withSize(registry.get(TO).getSlotCount(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(blockEntity.save(new CompoundTag()), inventoryData);
        world.removeBlockEntity(pos);
        BlockState newState = Registry.BLOCK.get(registry.get(TO).getBlockId()).defaultBlockState();
        world.setBlockAndUpdate(pos, newState.setValue(BlockStateProperties.FACING, state.getValue(BlockStateProperties.FACING)));
        blockEntity = (StorageBlockEntity) world.getBlockEntity(pos);
        blockEntity.load(world.getBlockState(pos), ContainerHelper.saveAllItems(blockEntity.save(new CompoundTag()), inventoryData));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected InteractionResult useModifierOnBlock(final UseOnContext context, final BlockState state)
    {
        final Block block = state.getBlock();
        if (block instanceof ChestBlock && block.is(Const.WOODEN_CHESTS) && FROM.equals(Const.id("wood")))
        {
            final Level world = context.getLevel();
            final BlockPos mainPos = context.getClickedPos();
            final Player player = context.getPlayer();
            final ItemStack handStack = player.getItemInHand(context.getHand());
            if (state.getValue(BlockStateProperties.CHEST_TYPE) == ChestType.SINGLE)
            {
                if (!world.isClientSide)
                {
                    upgradeChest(world, mainPos, state);
                    handStack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
            else if (handStack.getCount() > 1 || player.isCreative())
            {
                final BlockPos otherPos;
                if (state.getValue(BlockStateProperties.CHEST_TYPE) == ChestType.RIGHT)
                {
                    otherPos = mainPos.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise());
                }
                else if (state.getValue(BlockStateProperties.CHEST_TYPE) == ChestType.LEFT)
                {
                    otherPos = mainPos.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise());
                }
                else { return InteractionResult.FAIL; }
                if (!world.isClientSide)
                {
                    upgradeChest(world, otherPos, world.getBlockState(otherPos));
                    upgradeChest(world, mainPos, state);
                    handStack.shrink(2);
                }
                return InteractionResult.SUCCESS;
            }
        }
        else if(block instanceof net.minecraft.world.level.block.BarrelBlock && block.is(Const.WOODEN_BARRELS) && FROM.equals(Const.id("wood")))
        {
            final Level world = context.getLevel();
            final BlockPos mainPos = context.getClickedPos();
            final Player player = context.getPlayer();
            final ItemStack handStack = player.getItemInHand(context.getHand());
            if (!world.isClientSide)
            {
                upgradeVanillaBarrel(world, mainPos, state);
                handStack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level world, final List<Component> tooltip, final TooltipFlag context)
    {
        super.appendHoverText(stack, world, tooltip, context);
        tooltip.add(TOOLTIP);
        tooltip.add(DOUBLE_REQUIRES_2);
    }
}