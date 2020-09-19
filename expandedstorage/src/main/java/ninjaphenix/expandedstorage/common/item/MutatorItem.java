package ninjaphenix.expandedstorage.common.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner.BlockType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import ninjaphenix.expandedstorage.common.Const;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.ChestBlock;
import ninjaphenix.expandedstorage.common.block.StorageBlock;
import ninjaphenix.expandedstorage.common.block.entity.StorageBlockEntity;
import ninjaphenix.expandedstorage.common.misc.CursedChestType;

import javax.annotation.Nullable;

import java.util.List;
import java.util.function.Predicate;

import static net.minecraft.world.level.block.Rotation.CLOCKWISE_180;
import static net.minecraft.world.level.block.Rotation.CLOCKWISE_90;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class MutatorItem extends ModifierItem
{
    public MutatorItem(final Item.Properties settings) { super(settings); }

    @Override
    protected InteractionResult useOnBlock(final UseOnContext context, final BlockState state, final BlockPos pos)
    {
        final ItemStack stack = context.getItemInHand();
        final Level level = context.getLevel();
        final Player player = context.getPlayer();
        final Block block = state.getBlock();
        if (block instanceof AbstractChestBlock)
        {
            if (getMode(stack) == MutatorMode.ROTATE)
            {
                if (state.hasProperty(CHEST_TYPE))
                {
                    final ChestType chestType = state.getValue(CHEST_TYPE);
                    if (chestType != ChestType.SINGLE)
                    {
                        if (!level.isClientSide)
                        {
                            final BlockPos otherPos = pos.relative(net.minecraft.world.level.block.ChestBlock.getConnectedDirection(state));
                            final BlockState otherState = level.getBlockState(otherPos);
                            level.setBlockAndUpdate(pos, state.rotate(CLOCKWISE_180).setValue(CHEST_TYPE, state.getValue(CHEST_TYPE).getOpposite()));
                            level.setBlockAndUpdate(otherPos, otherState.rotate(CLOCKWISE_180).setValue(CHEST_TYPE, otherState.getValue(CHEST_TYPE).getOpposite()));
                        }
                        player.getCooldowns().addCooldown(this, 5);
                        return InteractionResult.SUCCESS;
                    }
                }
                level.setBlockAndUpdate(pos, state.rotate(CLOCKWISE_90));
                player.getCooldowns().addCooldown(this, 5);
                return InteractionResult.SUCCESS;
            }
        }
        if (block instanceof net.minecraft.world.level.block.ChestBlock)
        {
            final MutatorMode mode = getMode(stack);
            if (mode == MutatorMode.MERGE)
            {
                final CompoundTag tag = stack.getOrCreateTag();
                if (tag.contains("pos"))
                {
                    final BlockPos otherPos = NbtUtils.readBlockPos(tag.getCompound("pos"));
                    final BlockState otherState = level.getBlockState(otherPos);
                    if (otherState.getBlock() == state.getBlock() &&
                            otherState.getValue(HORIZONTAL_FACING) == state.getValue(HORIZONTAL_FACING) &&
                            otherState.getValue(CHEST_TYPE) == ChestType.SINGLE)
                    {
                        if (!level.isClientSide)
                        {
                            final BlockPos offset = otherPos.subtract(pos);
                            final Direction direction = Direction.fromNormal(offset.getX(), offset.getY(), offset.getZ());
                            if (direction != null)
                            {
                                final Registries.TierData entry = Registries.CHEST.get(Const.resloc("wood"));
                                final CursedChestType type = ChestBlock.getChestType(state.getValue(HORIZONTAL_FACING), direction);
                                final Predicate<BlockEntity> isRandomizable = b -> b instanceof RandomizableContainerBlockEntity;
                                convertContainer(level, state, pos, entry, type, isRandomizable);
                                convertContainer(level, otherState, otherPos, entry, type.getOpposite(), isRandomizable);
                                tag.remove("pos");
                                player.displayClientMessage(new TranslatableComponent("tooltip.expandedstorage.chest_mutator.merge_end"), true);
                            }
                        }
                        player.getCooldowns().addCooldown(this, 5);
                        return InteractionResult.SUCCESS;
                    }
                }
                else
                {
                    if (!level.isClientSide)
                    {
                        tag.put("pos", NbtUtils.writeBlockPos(pos));
                        player.displayClientMessage(new TranslatableComponent("tooltip.expandedstorage.chest_mutator.merge_start"), true);
                    }
                    player.getCooldowns().addCooldown(this, 5);
                    return InteractionResult.SUCCESS;
                }
            }
            else if (mode == MutatorMode.UNMERGE)
            {
                final ChestType chestType = state.getValue(CHEST_TYPE);
                if (chestType != ChestType.SINGLE)
                {
                    if (!level.isClientSide)
                    {
                        final BlockPos otherPos = pos.relative(net.minecraft.world.level.block.ChestBlock.getConnectedDirection(state));
                        final BlockState otherState = level.getBlockState(otherPos);
                        level.setBlockAndUpdate(pos, state.setValue(CHEST_TYPE, ChestType.SINGLE));
                        level.setBlockAndUpdate(otherPos, otherState.setValue(CHEST_TYPE, ChestType.SINGLE));
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        else if (block instanceof BarrelBlock)
        {
            if (getMode(stack) == MutatorMode.ROTATE)
            {
                if (!level.isClientSide)
                {
                    final Direction direction = state.getValue(FACING);
                    level.setBlockAndUpdate(pos, state.setValue(FACING, Direction.from3DDataValue(direction.get3DDataValue() + 1)));
                }
                player.getCooldowns().addCooldown(this, 5);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    private void convertContainer(final Level level, final BlockState state, final BlockPos pos, final Registries.TierData data,
                                  @Nullable final CursedChestType type, final Predicate<BlockEntity> check)
    {
        final BlockEntity targetBlockEntity = level.getBlockEntity(pos);
        if (check.test(targetBlockEntity))
        {
            Registry.BLOCK.getOptional(data.RESOURCE_LOCATION).ifPresent(
                    block ->
                    {
                        final NonNullList<ItemStack> invData = NonNullList.withSize(data.SLOT_COUNT, ItemStack.EMPTY);
                        ContainerHelper.loadAllItems(targetBlockEntity.save(new CompoundTag()), invData);
                        level.removeBlockEntity(pos);
                        BlockState newState = block.defaultBlockState();
                        if (state.hasProperty(WATERLOGGED)) { newState = newState.setValue(WATERLOGGED, state.getValue(WATERLOGGED)); }
                        if (state.hasProperty(FACING)) { newState = newState.setValue(FACING, state.getValue(FACING)); }
                        else if (state.hasProperty(HORIZONTAL_FACING))
                        {
                            newState = newState.setValue(HORIZONTAL_FACING, state.getValue(HORIZONTAL_FACING));
                        }
                        if (type != null) { newState = newState.setValue(ChestBlock.TYPE, type); }
                        level.setBlockAndUpdate(pos, newState);
                        final BlockEntity newEntity = level.getBlockEntity(pos);
                        newEntity.load(newState, ContainerHelper.saveAllItems(newEntity.save(new CompoundTag()), invData));
                    });
        }
    }

    @Override
    protected InteractionResult useModifierOnBlock(final UseOnContext context, final BlockState state, final BlockPos pos, final BlockType type)
    {
        final Level level = context.getLevel();
        final Player player = context.getPlayer();
        final ItemStack stack = context.getItemInHand();
        final StorageBlock block = (StorageBlock) state.getBlock();
        switch (getMode(context.getItemInHand()))
        {
            case MERGE:
                if (block instanceof ChestBlock && state.getValue(ChestBlock.TYPE) == CursedChestType.SINGLE)
                {
                    CompoundTag tag = stack.getOrCreateTag();
                    if (tag.contains("pos"))
                    {
                        final BlockPos otherPos = NbtUtils.readBlockPos(tag.getCompound("pos"));
                        final BlockState otherState = level.getBlockState(otherPos);
                        final Direction facing = state.getValue(HORIZONTAL_FACING);
                        if (block == otherState.getBlock()
                                && facing == otherState.getValue(HORIZONTAL_FACING)
                                && otherState.getValue(ChestBlock.TYPE) == CursedChestType.SINGLE)
                        {
                            if (!level.isClientSide)
                            {
                                final BlockPos offset = otherPos.subtract(pos);
                                final Direction direction = Direction.fromNormal(offset.getX(), offset.getY(), offset.getZ());
                                if (direction != null)
                                {
                                    final Registries.TierData entry = block.getDataRegistry().get(block.TIER_ID);
                                    final CursedChestType chestType = ChestBlock.getChestType(state.getValue(HORIZONTAL_FACING), direction);
                                    final Predicate<BlockEntity> isStorage = b -> b instanceof StorageBlockEntity;
                                    convertContainer(level, state, pos, entry, chestType, isStorage);
                                    convertContainer(level, otherState, otherPos, entry, chestType.getOpposite(), isStorage);
                                    tag.remove("pos");
                                    player.displayClientMessage(new TranslatableComponent("tooltip.expandedstorage.chest_mutator.merge_end"), true);
                                }
                            }
                            player.getCooldowns().addCooldown(this, 5);
                            return InteractionResult.SUCCESS;
                        }


                    }
                    else
                    {
                        if (!level.isClientSide)
                        {
                            tag.put("pos", NbtUtils.writeBlockPos(pos));
                            player.displayClientMessage(new TranslatableComponent("tooltip.expandedstorage.chest_mutator.merge_start"), true);
                        }
                        player.getCooldowns().addCooldown(this, 5);
                        return InteractionResult.SUCCESS;
                    }
                }
                break;
            case UNMERGE:
                if (block instanceof ChestBlock && state.getValue(ChestBlock.TYPE) != CursedChestType.SINGLE)
                {
                    if (!level.isClientSide)
                    {
                        final BlockPos otherPos = pos.relative(ChestBlock.getDirectionToAttached(state));
                        final BlockState otherState = level.getBlockState(otherPos);
                        level.setBlockAndUpdate(pos, state.setValue(ChestBlock.TYPE, CursedChestType.SINGLE));
                        level.setBlockAndUpdate(otherPos, otherState.setValue(ChestBlock.TYPE, CursedChestType.SINGLE));
                    }
                    player.getCooldowns().addCooldown(this, 5);
                    return InteractionResult.SUCCESS;
                }
                break;
            case ROTATE:
            {
                if (state.hasProperty(FACING))
                {
                    if (!level.isClientSide)
                    {
                        final Direction direction = state.getValue(FACING);
                        level.setBlockAndUpdate(pos, state.setValue(FACING, Direction.from3DDataValue(direction.get3DDataValue() + 1)));
                    }
                    player.getCooldowns().addCooldown(this, 5);
                    return InteractionResult.SUCCESS;
                }
                else if (state.hasProperty(HORIZONTAL_FACING))
                {
                    if (block instanceof ChestBlock)
                    {
                        switch (state.getValue(ChestBlock.TYPE))
                        {
                            case SINGLE:
                                if (!level.isClientSide) { level.setBlockAndUpdate(pos, state.rotate(CLOCKWISE_90)); }
                                player.getCooldowns().addCooldown(this, 5);
                                return InteractionResult.SUCCESS;
                            case TOP:
                            case BOTTOM:
                                if (!level.isClientSide)
                                {
                                    level.setBlockAndUpdate(pos, state.rotate(CLOCKWISE_90));
                                    final BlockPos otherPos = pos.relative(ChestBlock.getDirectionToAttached(state));
                                    final BlockState otherState = level.getBlockState(otherPos);
                                    level.setBlockAndUpdate(otherPos, otherState.rotate(CLOCKWISE_90));
                                }
                                player.getCooldowns().addCooldown(this, 5);
                                return InteractionResult.SUCCESS;
                            case FRONT:
                            case BACK:
                            case LEFT:
                            case RIGHT:
                                if (!level.isClientSide)
                                {
                                    level.setBlockAndUpdate(pos, state.rotate(CLOCKWISE_180).setValue(ChestBlock.TYPE, state.getValue(ChestBlock.TYPE).getOpposite()));
                                    final BlockPos otherPos = pos.relative(ChestBlock.getDirectionToAttached(state));
                                    final BlockState otherState = level.getBlockState(otherPos);
                                    level.setBlockAndUpdate(otherPos, otherState.rotate(CLOCKWISE_180).setValue(ChestBlock.TYPE, otherState.getValue(ChestBlock.TYPE).getOpposite()));
                                }
                                player.getCooldowns().addCooldown(this, 5);
                                return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected InteractionResultHolder<ItemStack> useModifierInAir(final Level world, final Player player, final InteractionHand hand)
    {
        if (player.isShiftKeyDown())
        {
            final ItemStack stack = player.getItemInHand(hand);
            final CompoundTag tag = stack.getOrCreateTag();
            tag.putByte("mode", getMode(stack).next);
            if (tag.contains("pos")) { tag.remove("pos"); }
            if (!world.isClientSide) { player.displayClientMessage(getMode(stack).title, true); }
            return InteractionResultHolder.success(stack);
        }
        return super.useModifierInAir(world, player, hand);
    }

    @Override
    public void onCraftedBy(final ItemStack stack, final Level world, final Player player)
    {
        super.onCraftedBy(stack, world, player);
        getMode(stack);
    }

    @Override
    public ItemStack getDefaultInstance()
    {
        final ItemStack stack = super.getDefaultInstance();
        getMode(stack);
        return stack;
    }

    @Override
    public void fillItemCategory(final CreativeModeTab group, final NonNullList<ItemStack> stacks)
    {
        if (allowdedIn(group))
        {
            stacks.add(getDefaultInstance());
        }
    }

    private MutatorMode getMode(final ItemStack stack)
    {
        final CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("mode", 1)) { tag.putByte("mode", (byte) 0); }
        return MutatorMode.values()[tag.getByte("mode")];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(final ItemStack stack, final Level world, final List<Component> tooltip, final TooltipFlag context)
    {
        final MutatorMode mode = getMode(stack);
        tooltip.add(new TranslatableComponent("tooltip.expandedstorage.tool_mode", mode.title).withStyle(ChatFormatting.GRAY));
        tooltip.add(mode.description);
        super.appendHoverText(stack, world, tooltip, context);
    }
}