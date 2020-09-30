package ninjaphenix.expandedstorage.common.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner.BlockType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import ninjaphenix.expandedstorage.common.Const;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.ChestBlock;
import ninjaphenix.expandedstorage.common.block.CursedChestBlock;
import ninjaphenix.expandedstorage.common.block.StorageBlock;
import ninjaphenix.expandedstorage.common.block.entity.StorageBlockEntity;
import ninjaphenix.expandedstorage.common.misc.CursedChestType;
import org.jetbrains.annotations.Nullable;

public final class ConversionItem extends ModifierItem
{
    private final Component TOOLTIP;
    private final ResourceLocation FROM, TO;
    private static final Component DOUBLE_REQUIRES_2 = new TranslatableComponent("tooltip.expandedstorage.conversion_kit_double_requires_2").withStyle(ChatFormatting.GRAY);

    public ConversionItem(final Item.Properties settings, final Tuple<ResourceLocation, String> from, final Tuple<ResourceLocation, String> to)
    {
        super(settings);
        FROM = from.getA();
        TO = to.getA();
        TOOLTIP = new TranslatableComponent(String.format("tooltip.expandedstorage.conversion_kit_%s_%s", from.getB(), to.getB()), Const.leftShiftRightClick).withStyle(ChatFormatting.GRAY);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected InteractionResult useModifierOnBlock(final UseOnContext context, final BlockState state, final BlockPos pos, final BlockType type)
    {
        final Block block = state.getBlock();
        if (block instanceof StorageBlock)
        {
            final StorageBlock instance = (StorageBlock) block;
            if (instance.TIER_ID != FROM) { return InteractionResult.FAIL; }
            final Player player = context.getPlayer();
            final Level level = context.getLevel();
            final ItemStack handStack = player.getItemInHand(context.getHand());
            if (type == BlockType.SINGLE)
            {
                if (!level.isClientSide)
                {
                    upgradeStorageBlock(level, state, pos);
                    handStack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
            else if (handStack.getCount() > 1 || player.isCreative())
            {
                if (!level.isClientSide)
                {
                    final BlockPos otherPos = pos.relative(ChestBlock.getDirectionToAttached(state));
                    final BlockState otherState = level.getBlockState(otherPos);
                    upgradeStorageBlock(level, state, pos);
                    upgradeStorageBlock(level, otherState, otherPos);
                    handStack.shrink(2);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.FAIL;
    }

    @SuppressWarnings("ConstantConditions")
    private void upgradeStorageBlock(final Level level, final BlockState state, final BlockPos pos)
    {
        StorageBlockEntity blockEntity = (StorageBlockEntity) level.getBlockEntity(pos);
        final MappedRegistry<Registries.TierData> registry = ((StorageBlock) state.getBlock()).getDataRegistry();
        final NonNullList<ItemStack> inventory = NonNullList.withSize(registry.get(TO).SLOT_COUNT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(blockEntity.save(new CompoundTag()), inventory);
        level.removeBlockEntity(pos);
        BlockState newState = Registry.BLOCK.get(registry.get(TO).RESOURCE_LOCATION).defaultBlockState();
        if (newState.hasProperty(BlockStateProperties.WATERLOGGED))
        {
            newState = newState.setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));
        }
        if (newState.hasProperty(CursedChestBlock.TYPE))
        {
            newState = newState.setValue(CursedChestBlock.TYPE, state.getValue(CursedChestBlock.TYPE));
        }
        if (newState.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
        {
            newState = newState.setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
        }
        else if (newState.hasProperty(BlockStateProperties.FACING))
        {
            newState = newState.setValue(BlockStateProperties.FACING, state.getValue(BlockStateProperties.FACING));
        }
        level.setBlockAndUpdate(pos, newState);
        blockEntity = (StorageBlockEntity) level.getBlockEntity(pos);
        blockEntity.load(level.getBlockState(pos), ContainerHelper.saveAllItems(blockEntity.save(new CompoundTag()), inventory));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected InteractionResult useOnBlock(final UseOnContext context, final BlockState state, final BlockPos pos)
    {
        if (!FROM.equals(Const.resloc("wood"))) { return InteractionResult.FAIL; }
        final Level level = context.getLevel();
        final Block block = state.getBlock();
        final Player player = context.getPlayer();
        final ItemStack handStack = player.getItemInHand(context.getHand());
        if (block instanceof net.minecraft.world.level.block.ChestBlock)
        {
            if (state.getValue(BlockStateProperties.CHEST_TYPE) == ChestType.SINGLE)
            {
                if (!level.isClientSide)
                {
                    upgradeVanillaBlock(level, state, pos);
                    handStack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
            else if (handStack.getCount() > 1 || player.isCreative())
            {
                if (!level.isClientSide)
                {
                    final BlockPos otherPos = pos.relative(net.minecraft.world.level.block.ChestBlock.getConnectedDirection(state));
                    final BlockState otherState = level.getBlockState(otherPos);
                    upgradeVanillaBlock(level, state, pos);
                    upgradeVanillaBlock(level, otherState, otherPos);
                    handStack.shrink(2);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        else if (block instanceof BarrelBlock)
        {
            if (!level.isClientSide)
            {
                upgradeVanillaBlock(level, state, pos);
                handStack.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @SuppressWarnings("ConstantConditions")
    private void upgradeVanillaBlock(final Level level, final BlockState state, final BlockPos pos)
    {
        RandomizableContainerBlockEntity blockEntity = (RandomizableContainerBlockEntity) level.getBlockEntity(pos);
        final MappedRegistry<? extends Registries.TierData> registry = getVanillaRegistry(state.getBlock());
        final NonNullList<ItemStack> inventory = NonNullList.withSize(registry.get(TO).SLOT_COUNT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(blockEntity.save(new CompoundTag()), inventory);
        level.removeBlockEntity(pos);
        BlockState newState = Registry.BLOCK.get(registry.get(TO).RESOURCE_LOCATION).defaultBlockState();
        if (newState.hasProperty(BlockStateProperties.WATERLOGGED))
        {
            newState = newState.setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));
        }
        if (newState.hasProperty(CursedChestBlock.TYPE))
        {
            newState = newState.setValue(CursedChestBlock.TYPE, CursedChestType.valueOf(state.getValue(BlockStateProperties.CHEST_TYPE)));
        }
        if (newState.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
        {
            newState = newState.setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
        }
        else if (newState.hasProperty(BlockStateProperties.FACING))
        {
            newState = newState.setValue(BlockStateProperties.FACING, state.getValue(BlockStateProperties.FACING));
        }
        level.setBlockAndUpdate(pos, newState);
        blockEntity = (StorageBlockEntity) level.getBlockEntity(pos);
        blockEntity.load(level.getBlockState(pos), ContainerHelper.saveAllItems(blockEntity.save(new CompoundTag()), inventory));
    }

    private MappedRegistry<? extends Registries.TierData> getVanillaRegistry(final Block block)
    {
        if (block instanceof net.minecraft.world.level.block.ChestBlock) { return Registries.CHEST; }
        else if (block instanceof BarrelBlock) { return Registries.BARREL; }
        throw new IllegalArgumentException("Unexpected block passed to ConversionItem#getVanillaRegistry");
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level world, final List<Component> tooltip, final TooltipFlag context)
    {
        super.appendHoverText(stack, world, tooltip, context);
        tooltip.add(TOOLTIP);
        tooltip.add(DOUBLE_REQUIRES_2);
    }
}