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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import ninjaphenix.expandedstorage.common.Const;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.ChestBlock;
import ninjaphenix.expandedstorage.common.misc.CursedChestType;

import java.util.List;

import static net.minecraft.world.level.block.Rotation.CLOCKWISE_180;
import static net.minecraft.world.level.block.Rotation.CLOCKWISE_90;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

@SuppressWarnings("ConstantConditions")
public final class ChestMutatorItem extends ChestModifierItem
{
    private static final EnumProperty<CursedChestType> TYPE = ChestBlock.TYPE;

    public ChestMutatorItem(final Properties settings) { super(settings); }

    @Override
    protected InteractionResult useModifierOnBarrel(final UseOnContext context, final BlockState state, final BlockPos pos)
    {
        final Player player = context.getPlayer();
        final Level world = context.getLevel();
        final ItemStack stack = context.getItemInHand();
        if (getMode(stack) == MutatorMode.ROTATE)
        {
            final Direction direction = state.getValue(FACING);
            if (!world.isClientSide) { world.setBlockAndUpdate(pos, state.setValue(FACING, Direction.from3DDataValue(direction.get3DDataValue() + 1))); }
            player.getCooldowns().addCooldown(this, 5);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected InteractionResult useModifierOnChestBlock(final UseOnContext context, final BlockState mainState, final BlockPos mainBlockPos,
                                                   final BlockState otherState, final BlockPos otherBlockPos)
    {
        final Player player = context.getPlayer();
        final Level world = context.getLevel();
        final ItemStack stack = context.getItemInHand();
        switch (getMode(stack))
        {
            case MERGE:
                final CompoundTag tag = stack.getOrCreateTag();
                if (tag.contains("pos"))
                {
                    if (mainState.getValue(TYPE) == CursedChestType.SINGLE)
                    {
                        final BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("pos"));
                        final BlockState realOtherState = world.getBlockState(pos);
                        if (realOtherState.getBlock() == mainState.getBlock() && realOtherState.getValue(BlockStateProperties.HORIZONTAL_FACING) == mainState.getValue(BlockStateProperties.HORIZONTAL_FACING) && realOtherState.getValue(TYPE) == CursedChestType.SINGLE)
                        {
                            if (!world.isClientSide)
                            {
                                final BlockPos vec = pos.subtract(mainBlockPos);
                                final int sum = vec.getX() + vec.getY() + vec.getZ();
                                if (sum == 1 || sum == -1)
                                {
                                    final CursedChestType mainChestType = ChestBlock.getChestType(mainState.getValue(BlockStateProperties.HORIZONTAL_FACING), Direction.fromNormal(vec.getX(), vec.getY(), vec.getZ()));
                                    world.setBlockAndUpdate(mainBlockPos, mainState.setValue(TYPE, mainChestType));
                                    world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(TYPE, mainChestType.getOpposite()));
                                    tag.remove("pos");
                                    player.displayClientMessage(new TranslatableComponent("tooltip.expandedstorage.chest_mutator.merge_end"), true);
                                    player.getCooldowns().addCooldown(this, 5);
                                    return InteractionResult.SUCCESS;
                                }

                            }
                        }
                        return InteractionResult.FAIL;
                    }
                }
                else
                {
                    if (mainState.getValue(TYPE) == CursedChestType.SINGLE)
                    {
                        tag.put("pos", NbtUtils.writeBlockPos(mainBlockPos));
                        player.displayClientMessage(new TranslatableComponent("tooltip.expandedstorage.chest_mutator.merge_start"), true);
                        player.getCooldowns().addCooldown(this, 5);
                        return InteractionResult.SUCCESS;
                    }

                }
                break;
            case UNMERGE:
                if (otherState != null)
                {
                    if (!world.isClientSide)
                    {
                        world.setBlockAndUpdate(mainBlockPos, world.getBlockState(mainBlockPos).setValue(TYPE, CursedChestType.SINGLE));
                        world.setBlockAndUpdate(otherBlockPos, world.getBlockState(otherBlockPos).setValue(TYPE, CursedChestType.SINGLE));
                    }
                    player.getCooldowns().addCooldown(this, 5);
                    return InteractionResult.SUCCESS;
                }
                break;
            case ROTATE:
                switch (mainState.getValue(ChestBlock.TYPE))
                {
                    case SINGLE:
                        if (!world.isClientSide) { world.setBlockAndUpdate(mainBlockPos, mainState.rotate(CLOCKWISE_90)); }
                        player.getCooldowns().addCooldown(this, 5);
                        return InteractionResult.SUCCESS;
                    case FRONT:
                    case BACK:
                    case LEFT:
                    case RIGHT:
                        if (!world.isClientSide)
                        {
                            world.setBlockAndUpdate(mainBlockPos, mainState.rotate(CLOCKWISE_180).setValue(TYPE, mainState.getValue(TYPE).getOpposite()));
                            world.setBlockAndUpdate(otherBlockPos, otherState.rotate(CLOCKWISE_180).setValue(TYPE, otherState.getValue(TYPE).getOpposite()));
                        }
                        player.getCooldowns().addCooldown(this, 5);
                        return InteractionResult.SUCCESS;
                    case TOP:
                    case BOTTOM:
                        if (!world.isClientSide)
                        {
                            world.setBlockAndUpdate(mainBlockPos, mainState.rotate(CLOCKWISE_90));
                            world.setBlockAndUpdate(otherBlockPos, otherState.rotate(CLOCKWISE_90));
                        }
                        player.getCooldowns().addCooldown(this, 5);
                        return InteractionResult.SUCCESS;
                }
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected InteractionResult useModifierOnBlock(final UseOnContext context, final BlockState state)
    {
        final Player player = context.getPlayer();
        final ItemStack stack = context.getItemInHand();
        final Level world = context.getLevel();
        final BlockPos mainPos = context.getClickedPos();
        final MutatorMode mode = getMode(stack);
        if (state.getBlock() instanceof net.minecraft.world.level.block.ChestBlock)
        {
            if (mode == MutatorMode.MERGE)
            {
                final CompoundTag tag = stack.getOrCreateTag();
                if (tag.contains("pos"))
                {
                    if (state.getValue(net.minecraft.world.level.block.ChestBlock.TYPE) == ChestType.SINGLE)
                    {
                        final BlockPos otherPos = NbtUtils.readBlockPos(tag.getCompound("pos"));
                        final BlockState realOtherState = world.getBlockState(otherPos);
                        if (realOtherState.getBlock() == state.getBlock() && realOtherState.getValue(BlockStateProperties.HORIZONTAL_FACING) == state.getValue(BlockStateProperties.HORIZONTAL_FACING) && realOtherState.getValue(net.minecraft.world.level.block.ChestBlock.TYPE) == ChestType.SINGLE)
                        {
                            final BlockPos vec = otherPos.subtract(mainPos);
                            final int sum = vec.getX() + vec.getY() + vec.getZ();
                            if (sum == 1 || sum == -1)
                            {
                                if (!world.isClientSide)
                                {
                                    final Registries.TierData entry = Registries.CHEST.get(Const.resloc("wood_chest"));
                                    final BlockState defState = Registry.BLOCK.get(entry.RESOURCE_LOCATION).defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
                                    final CursedChestType mainChestType = ChestBlock.getChestType(state.getValue(BlockStateProperties.HORIZONTAL_FACING), Direction.fromNormal(vec.getX(), vec.getY(), vec.getZ()));
                                    // todo: refactor into method.
                                    BlockEntity blockEntity = world.getBlockEntity(mainPos);
                                    NonNullList<ItemStack> invData = NonNullList.withSize(entry.SLOT_COUNT, ItemStack.EMPTY);
                                    ContainerHelper.loadAllItems(blockEntity.save(new CompoundTag()), invData);
                                    world.removeBlockEntity(mainPos);
                                    world.setBlockAndUpdate(mainPos, defState.setValue(WATERLOGGED, state.getValue(WATERLOGGED)).setValue(TYPE, mainChestType));
                                    blockEntity = world.getBlockEntity(mainPos);
                                    blockEntity.load(world.getBlockState(mainPos), ContainerHelper.saveAllItems(blockEntity.save(new CompoundTag()), invData));

                                    blockEntity = world.getBlockEntity(otherPos);
                                    invData = NonNullList.withSize(entry.SLOT_COUNT, ItemStack.EMPTY);
                                    ContainerHelper.loadAllItems(blockEntity.save(new CompoundTag()), invData);
                                    world.removeBlockEntity(otherPos);
                                    world.setBlockAndUpdate(otherPos, defState.setValue(WATERLOGGED, state.getValue(WATERLOGGED)).setValue(TYPE, mainChestType.getOpposite()));
                                    blockEntity = world.getBlockEntity(otherPos);
                                    blockEntity.load(world.getBlockState(otherPos), ContainerHelper.saveAllItems(blockEntity.save(new CompoundTag()), invData));

                                    tag.remove("pos");
                                    player.displayClientMessage(new TranslatableComponent("tooltip.expandedstorage.chest_mutator.merge_end"), true);
                                    player.getCooldowns().addCooldown(this, 5);
                                }
                                return InteractionResult.SUCCESS;
                            }
                        }
                        return InteractionResult.FAIL;
                    }
                }
                else
                {
                    if (state.getValue(net.minecraft.world.level.block.ChestBlock.TYPE) == ChestType.SINGLE)
                    {
                        tag.put("pos", NbtUtils.writeBlockPos(mainPos));
                        player.displayClientMessage(new TranslatableComponent("tooltip.expandedstorage.chest_mutator.merge_start"), true);
                        player.getCooldowns().addCooldown(this, 5);
                        return InteractionResult.SUCCESS;
                    }

                }
            }
            else if (mode == MutatorMode.UNMERGE)
            {
                final BlockPos otherPos;
                switch (state.getValue(net.minecraft.world.level.block.ChestBlock.TYPE))
                {
                    case LEFT: otherPos = mainPos.relative(state.getValue(net.minecraft.world.level.block.ChestBlock.FACING).getClockWise());
                        break;
                    case RIGHT: otherPos = mainPos.relative(state.getValue(net.minecraft.world.level.block.ChestBlock.FACING).getCounterClockWise());
                        break;
                    default:
                        return InteractionResult.FAIL;
                }
                if (!world.isClientSide)
                {
                    world.setBlockAndUpdate(mainPos, state.setValue(net.minecraft.world.level.block.ChestBlock.TYPE, ChestType.SINGLE));
                    world.setBlockAndUpdate(otherPos, world.getBlockState(otherPos).setValue(net.minecraft.world.level.block.ChestBlock.TYPE, ChestType.SINGLE));
                }
                player.getCooldowns().addCooldown(this, 5);
                return InteractionResult.SUCCESS;
            }
            else if (mode == MutatorMode.ROTATE)
            {
                final BlockPos otherPos;
                switch (state.getValue(net.minecraft.world.level.block.ChestBlock.TYPE))
                {
                    case LEFT: otherPos = mainPos.relative(state.getValue(net.minecraft.world.level.block.ChestBlock.FACING).getClockWise());
                        break;
                    case RIGHT: otherPos = mainPos.relative(state.getValue(net.minecraft.world.level.block.ChestBlock.FACING).getCounterClockWise());
                        break;
                    case SINGLE:
                        if (!world.isClientSide) { world.setBlockAndUpdate(mainPos, state.rotate(CLOCKWISE_90)); }
                        player.getCooldowns().addCooldown(this, 5);
                        return InteractionResult.SUCCESS;
                    default:
                        return InteractionResult.FAIL;
                }
                if (!world.isClientSide)
                {
                    final BlockState otherState = world.getBlockState(otherPos);
                    world.setBlockAndUpdate(mainPos, state.rotate(CLOCKWISE_180).setValue(net.minecraft.world.level.block.ChestBlock.TYPE, state.getValue(net.minecraft.world.level.block.ChestBlock.TYPE).getOpposite()));
                    world.setBlockAndUpdate(otherPos, otherState.rotate(CLOCKWISE_180).setValue(net.minecraft.world.level.block.ChestBlock.TYPE, otherState.getValue(net.minecraft.world.level.block.ChestBlock.TYPE).getOpposite()));
                }
                player.getCooldowns().addCooldown(this, 5);
                return InteractionResult.SUCCESS;
            }
        }
        else if (state.getBlock() == Blocks.ENDER_CHEST)
        {
            if (mode == MutatorMode.ROTATE)
            {
                if (!world.isClientSide) { world.setBlockAndUpdate(mainPos, state.rotate(CLOCKWISE_90)); }
                player.getCooldowns().addCooldown(this, 5);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }
        return super.useModifierOnBlock(context, state);
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
    public void fillItemCategory(final CreativeModeTab group, final NonNullList<ItemStack> stacks) { if (allowdedIn(group)) { stacks.add(getDefaultInstance()); } }

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