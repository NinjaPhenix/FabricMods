package ninjaphenix.expandedstorage.common.block;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.DoubleBlockCombiner.Combiner;
import net.minecraft.world.level.block.DoubleBlockCombiner.NeighborCombineResult;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import ninjaphenix.expandedstorage.common.ExpandedStorage;
import ninjaphenix.expandedstorage.common.block.entity.StorageBlockEntity;
import ninjaphenix.expandedstorage.common.inventory.DoubleSidedInventory;
import ninjaphenix.expandedstorage.common.misc.CursedChestType;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public abstract class ChestBlock<T extends StorageBlockEntity> extends StorageBlock
{
    public static final EnumProperty<CursedChestType> TYPE = EnumProperty.create("type", CursedChestType.class);
    private final Supplier<BlockEntityType<T>> blockEntityType;
    private final Combiner<T, Optional<WorldlyContainer>> INVENTORY_GETTER =
            new Combiner<T, Optional<WorldlyContainer>>()
            {
                @Override
                public Optional<WorldlyContainer> acceptDouble(final T first, final T second)
                {
                    return Optional.of(new DoubleSidedInventory(first, second));
                }

                @Override
                public Optional<WorldlyContainer> acceptSingle(final T single) { return Optional.of(single); }

                @Override
                public Optional<WorldlyContainer> acceptNone() { return Optional.empty(); }
            };
    private final Combiner<T, Optional<ExtendedScreenHandlerFactory>> CONTAINER_GETTER =
            new Combiner<T, Optional<ExtendedScreenHandlerFactory>>()
            {
                @Override
                public Optional<ExtendedScreenHandlerFactory> acceptDouble(final T first, final T second)
                {
                    return Optional.of(new ExtendedScreenHandlerFactory()
                    {
                        private final DoubleSidedInventory inventory = new DoubleSidedInventory(first, second);

                        @Override
                        public void writeScreenOpeningData(final ServerPlayer player, final FriendlyByteBuf buffer)
                        {
                            buffer.writeBlockPos(first.getBlockPos()).writeInt(inventory.getContainerSize());
                        }

                        @Override
                        public Component getDisplayName()
                        {
                            if (first.hasCustomName()) { return first.getDisplayName(); }
                            else if (second.hasCustomName()) { return second.getDisplayName(); }
                            return new TranslatableComponent("container.expandedstorage.generic_double", first.getDisplayName());
                        }

                        @Nullable
                        @Override
                        public AbstractContainerMenu createMenu(final int syncId, final Inventory playerInventory, final Player player)
                        {
                            if (first.stillValid(player) && second.stillValid(player))
                            {
                                first.unpackLootTable(player);
                                second.unpackLootTable(player);
                                return ExpandedStorage.INSTANCE.getScreenHandler(syncId, first.getBlockPos(), inventory, player, getDisplayName());
                            }
                            return null;
                        }
                    });
                }

                @Override
                public Optional<ExtendedScreenHandlerFactory> acceptSingle(final T single)
                {
                    return Optional.of(new ExtendedScreenHandlerFactory()
                    {
                        @Override
                        public void writeScreenOpeningData(final ServerPlayer player, final FriendlyByteBuf buffer)
                        {
                            buffer.writeBlockPos(single.getBlockPos()).writeInt(single.getContainerSize());
                        }

                        @Override
                        public Component getDisplayName() { return single.getDisplayName(); }

                        @Nullable
                        @Override
                        public AbstractContainerMenu createMenu(final int syncId, final Inventory playerInventory, final Player player)
                        {
                            if (single.stillValid(player))
                            {
                                single.unpackLootTable(player);
                                return ExpandedStorage.INSTANCE.getScreenHandler(syncId, single.getBlockPos(), single, player, getDisplayName());
                            }
                            return null;
                        }
                    });
                }

                @Override
                public Optional<ExtendedScreenHandlerFactory> acceptNone() { return Optional.empty(); }
            };

    protected ChestBlock(final Properties builder, final ResourceLocation tierId, final Supplier<BlockEntityType<T>> blockEntityType)
    {
        super(builder, tierId);
        this.blockEntityType = blockEntityType;
        registerDefaultState(defaultBlockState().setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(TYPE, CursedChestType.SINGLE));
    }

    public static Direction getDirectionToAttached(final BlockState state)
    {
        switch (state.getValue(TYPE))
        {
            case TOP: return Direction.DOWN;
            case BACK: return state.getValue(HORIZONTAL_FACING);
            case RIGHT: return state.getValue(HORIZONTAL_FACING).getClockWise();
            case BOTTOM: return Direction.UP;
            case FRONT: return state.getValue(HORIZONTAL_FACING).getOpposite();
            case LEFT: return state.getValue(HORIZONTAL_FACING).getCounterClockWise();
            default: throw new IllegalArgumentException("BaseChestBlock#getDirectionToAttached received an unexpected state.");
        }
    }

    public static DoubleBlockCombiner.BlockType getBlockType(final BlockState state)
    {
        switch (state.getValue(TYPE))
        {
            case TOP:
            case LEFT:
            case FRONT: return DoubleBlockCombiner.BlockType.FIRST;
            case BACK:
            case RIGHT:
            case BOTTOM: return DoubleBlockCombiner.BlockType.SECOND;
            default: return DoubleBlockCombiner.BlockType.SINGLE;
        }
    }

    public static CursedChestType getChestType(final Direction facing, final Direction offset)
    {
        if (facing.getClockWise() == offset) { return CursedChestType.RIGHT; }
        else if (facing.getCounterClockWise() == offset) { return CursedChestType.LEFT; }
        else if (facing == offset) { return CursedChestType.BACK; }
        else if (facing == offset.getOpposite()) { return CursedChestType.FRONT; }
        else if (offset == Direction.DOWN) { return CursedChestType.TOP; }
        else if (offset == Direction.UP) { return CursedChestType.BOTTOM; }
        return CursedChestType.SINGLE;
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(HORIZONTAL_FACING, TYPE);
    }

    public final NeighborCombineResult<? extends T> combine(final BlockState state, final LevelAccessor world, final BlockPos pos,
                                                     final boolean alwaysOpen)
    {
        final BiPredicate<LevelAccessor, BlockPos> isChestBlocked = alwaysOpen ? (_world, _pos) -> false : this::isBlocked;
        return DoubleBlockCombiner.combineWithNeigbour(blockEntityType.get(), ChestBlock::getBlockType,
                                                      ChestBlock::getDirectionToAttached, HORIZONTAL_FACING, state, world, pos,
                                                      isChestBlocked);
    }

    protected boolean isBlocked(final LevelAccessor world, final BlockPos pos) { return net.minecraft.world.level.block.ChestBlock.isChestBlockedAt(world, pos); }

    // todo: look at and see if it can be updated, specifically want to remove "BlockState state;", "Direction direction_3;" if possible
    // todo: add config to prevent automatic merging of chests.
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context)
    {
        final Level world = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        CursedChestType chestType = CursedChestType.SINGLE;
        final Direction direction_1 = context.getHorizontalDirection().getOpposite();
        final Direction direction_2 = context.getClickedFace();
        if (context.isSecondaryUseActive())
        {
            final BlockState state;
            final Direction direction_3;
            if (direction_2.getAxis().isVertical())
            {
                state = world.getBlockState(pos.relative(direction_2.getOpposite()));
                direction_3 = state.getBlock() == this && state.getValue(TYPE) == CursedChestType.SINGLE ? state.getValue(HORIZONTAL_FACING) : null;
                if (direction_3 != null && direction_3.getAxis() != direction_2.getAxis() && direction_3 == direction_1)
                {
                    chestType = direction_2 == Direction.UP ? CursedChestType.TOP : CursedChestType.BOTTOM;
                }
            }
            else
            {
                Direction offsetDir = direction_2.getOpposite();
                final BlockState clickedBlock = world.getBlockState(pos.relative(offsetDir));
                if (clickedBlock.getBlock() == this && clickedBlock.getValue(TYPE) == CursedChestType.SINGLE)
                {
                    if (clickedBlock.getValue(HORIZONTAL_FACING) == direction_2 && clickedBlock.getValue(HORIZONTAL_FACING) == direction_1)
                    {
                        chestType = CursedChestType.FRONT;
                    }
                    else
                    {
                        state = world.getBlockState(pos.relative(direction_2.getOpposite()));
                        if (state.getValue(HORIZONTAL_FACING).get2DDataValue() < 2) { offsetDir = offsetDir.getOpposite(); }
                        if (direction_1 == state.getValue(HORIZONTAL_FACING))
                        {
                            chestType = (offsetDir == Direction.WEST || offsetDir == Direction.NORTH) ? CursedChestType.LEFT : CursedChestType.RIGHT;
                        }
                    }
                }
            }
        }
        else
        {
            for (final Direction dir : Direction.values())
            {
                final BlockState state = world.getBlockState(pos.relative(dir));
                if (state.getBlock() != this || state.getValue(TYPE) != CursedChestType.SINGLE || state.getValue(HORIZONTAL_FACING) != direction_1)
                {
                    continue;
                }
                final CursedChestType type = getChestType(direction_1, dir);
                if (type != CursedChestType.SINGLE)
                {
                    chestType = type;
                    break;
                }
            }
        }
        return defaultBlockState().setValue(HORIZONTAL_FACING, direction_1).setValue(TYPE, chestType);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(final BlockState state, final Direction offset, final BlockState offsetState,
                                                final LevelAccessor world, final BlockPos pos, final BlockPos offsetPos)
    {
        final DoubleBlockCombiner.BlockType mergeType = getBlockType(state);
        if (mergeType == DoubleBlockCombiner.BlockType.SINGLE)
        {
            final Direction facing = state.getValue(HORIZONTAL_FACING);
            if (!offsetState.hasProperty(TYPE)) { return state.setValue(TYPE, CursedChestType.SINGLE); }
            final CursedChestType newType = getChestType(facing, offset);
            if (offsetState.getValue(TYPE) == newType.getOpposite() && facing == offsetState.getValue(HORIZONTAL_FACING))
            {
                return state.setValue(TYPE, newType);
            }
        }
        else if (world.getBlockState(pos.relative(getDirectionToAttached(state))).getBlock() != this)
        {
            return state.setValue(TYPE, CursedChestType.SINGLE);
        }
        return super.updateShape(state, offset, offsetState, world, pos, offsetPos);
    }

    @Override
    public int getAnalogOutputSignal(final BlockState state, final Level world, final BlockPos pos)
    {
        return combine(state, world, pos, true).apply(INVENTORY_GETTER).map(AbstractContainerMenu::getRedstoneSignalFromContainer).orElse(0);
    }

    @Override
    protected ResourceLocation getOpenStat() { return Stats.OPEN_CHEST; }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(final BlockState state, final Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(final BlockState state, final Rotation rotation)
    {
        return state.setValue(HORIZONTAL_FACING, rotation.rotate(state.getValue(HORIZONTAL_FACING)));
    }

    @Override // keep for hoppers.
    public WorldlyContainer getContainer(final BlockState state, final LevelAccessor world, final BlockPos pos)
    {
        return combine(state, world, pos, true).apply(INVENTORY_GETTER).orElse(null);
    }

    @Override
    protected ExtendedScreenHandlerFactory createContainerFactory(final BlockState state, final LevelAccessor world, final BlockPos pos)
    {
        return combine(state, world, pos, true).apply(CONTAINER_GETTER).orElse(null);
    }
}