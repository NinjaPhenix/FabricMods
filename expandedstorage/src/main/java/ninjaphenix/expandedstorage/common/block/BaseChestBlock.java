package ninjaphenix.expandedstorage.common.block;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.block.DoubleBlockProperties.PropertyRetriever;
import net.minecraft.block.DoubleBlockProperties.PropertySource;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import ninjaphenix.container_library.api.common.inventory.AbstractScreenHandler.ScreenMeta;
import ninjaphenix.container_library.api.common.inventory.DoubleSidedInventory;
import ninjaphenix.container_library.api.common.inventory.ExtendedScreenHandlerFactoryProvider;
import ninjaphenix.container_library.impl.client.NewContainerLibraryClient;
import ninjaphenix.container_library.impl.common.NewContainerLibrary;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.entity.AbstractChestBlockEntity;
import ninjaphenix.expandedstorage.common.misc.CursedChestType;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public abstract class BaseChestBlock<T extends AbstractChestBlockEntity> extends BlockWithEntity implements InventoryProvider,
        ExtendedScreenHandlerFactoryProvider
{
    public static final EnumProperty<CursedChestType> TYPE = EnumProperty.of("type", CursedChestType.class);
    private final PropertyRetriever<T, Optional<Text>> NAME_GETTER = new PropertyRetriever<T, Optional<Text>>()
    {
        @Override
        public Optional<Text> getFromBoth(final T first, final T second)
        {
            if (first.hasCustomName()) { return Optional.of(first.getDisplayName()); }
            else if (second.hasCustomName()) { return Optional.of(second.getDisplayName()); }
            return Optional.of(first.getDisplayName());
        }

        @Override
        public Optional<Text> getFrom(final T single)
        {
            return Optional.of(single.getDisplayName());
        }

        @Override
        public Optional<Text> getFallback()
        {
            return Optional.empty();
        }
    };
    private final Supplier<BlockEntityType<T>> blockEntityType;
    private final PropertyRetriever<T, Optional<SidedInventory>> INVENTORY_GETTER =
            new PropertyRetriever<T, Optional<SidedInventory>>()
            {
                @Override
                public Optional<SidedInventory> getFromBoth(final T first, final T second)
                {
                    return Optional.of(new DoubleSidedInventory(first, second));
                }

                @Override
                public Optional<SidedInventory> getFrom(final T single) { return Optional.of(single); }

                @Override
                public Optional<SidedInventory> getFallback() { return Optional.empty(); }
            };
    //private final PropertyRetriever<T, Optional<ExtendedScreenHandlerFactory>> CONTAINER_GETTER =
    //        new PropertyRetriever<T, Optional<ExtendedScreenHandlerFactory>>()
    //        {
    //            @Override
    //            public Optional<ExtendedScreenHandlerFactory> getFromBoth(final T first, final T second)
    //            {
    //                return Optional.of(new ExtendedScreenHandlerFactory()
    //                {
    //                    private final DoubleSidedInventory inventory = new DoubleSidedInventory(first, second);

    //                    @Override
    //                    public void writeScreenOpeningData(final ServerPlayerEntity player, final PacketByteBuf buffer)
    //                    {
    //                        buffer.writeBlockPos(first.getPos()).writeInt(inventory.size());
    //                    }

    //                    @Override
    //                    public Text getDisplayName()
    //                    {
    //                        if (first.hasCustomName()) { return first.getDisplayName(); }
    //                        else if (second.hasCustomName()) { return second.getDisplayName(); }
    //                        return new TranslatableText("container.expandedstorage.generic_double", first.getDisplayName());
    //                    }

    //                    @Nullable
    //                    @Override
    //                    public ScreenHandler createMenu(final int syncId, final PlayerInventory playerInventory, final PlayerEntity player)
    //                    {
    //                        if (first.canPlayerUse(player) && second.canPlayerUse(player))
    //                        {
    //                            first.checkLootInteraction(player);
    //                            second.checkLootInteraction(player);
    //                            return ContainerLibrary.INSTANCE.getScreenHandler(syncId, first.getPos(), inventory, player, getDisplayName());
    //                        }
    //                        return null;
    //                    }
    //                });
    //            }

    //            @Override
    //            public Optional<ExtendedScreenHandlerFactory> getFrom(final T single)
    //            {
    //                return Optional.of(new ExtendedScreenHandlerFactory()
    //                {
    //                    @Override
    //                    public void writeScreenOpeningData(final ServerPlayerEntity player, final PacketByteBuf buffer)
    //                    {
    //                        buffer.writeBlockPos(single.getPos()).writeInt(single.size());
    //                    }

    //                    @Override
    //                    public Text getDisplayName() { return single.getDisplayName(); }

    //                    @Nullable
    //                    @Override
    //                    public ScreenHandler createMenu(final int syncId, final PlayerInventory playerInventory, final PlayerEntity player)
    //                    {
    //                        if (single.canPlayerUse(player))
    //                        {
    //                            single.checkLootInteraction(player);
    //                            return ContainerLibrary.INSTANCE.getScreenHandler(syncId, single.getPos(), single, player, getDisplayName());
    //                        }
    //                        return null;
    //                    }
    //                });
    //            }

    //            @Override
    //            public Optional<ExtendedScreenHandlerFactory> getFallback() { return Optional.empty(); }
    //        };

    protected BaseChestBlock(final Settings builder, final Supplier<BlockEntityType<T>> blockEntityType)
    {
        super(builder);
        this.blockEntityType = blockEntityType;
        setDefaultState(getDefaultState().with(HORIZONTAL_FACING, Direction.NORTH).with(TYPE, CursedChestType.SINGLE));
    }

    public static Direction getDirectionToAttached(final BlockState state)
    {
        switch (state.get(TYPE))
        {
            case TOP: return Direction.DOWN;
            case BACK: return state.get(HORIZONTAL_FACING);
            case RIGHT: return state.get(HORIZONTAL_FACING).rotateYClockwise();
            case BOTTOM: return Direction.UP;
            case FRONT: return state.get(HORIZONTAL_FACING).getOpposite();
            case LEFT: return state.get(HORIZONTAL_FACING).rotateYCounterclockwise();
            default: throw new IllegalArgumentException("BaseChestBlock#getDirectionToAttached received an unexpected state.");
        }
    }

    public static DoubleBlockProperties.Type getMergeType(final BlockState state)
    {
        switch (state.get(TYPE))
        {
            case TOP:
            case LEFT:
            case FRONT: return DoubleBlockProperties.Type.FIRST;
            case BACK:
            case RIGHT:
            case BOTTOM: return DoubleBlockProperties.Type.SECOND;
            default: return DoubleBlockProperties.Type.SINGLE;
        }
    }

    public static CursedChestType getChestType(final Direction facing, final Direction offset)
    {
        if (facing.rotateYClockwise() == offset) { return CursedChestType.RIGHT; }
        else if (facing.rotateYCounterclockwise() == offset) { return CursedChestType.LEFT; }
        else if (facing == offset) { return CursedChestType.BACK; }
        else if (facing == offset.getOpposite()) { return CursedChestType.FRONT; }
        else if (offset == Direction.DOWN) { return CursedChestType.TOP; }
        else if (offset == Direction.UP) { return CursedChestType.BOTTOM; }
        return CursedChestType.SINGLE;
    }

    @Override
    protected void appendProperties(final StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(HORIZONTAL_FACING, TYPE);
    }

    public final PropertySource<? extends T> combine(final BlockState state, final WorldAccess world, final BlockPos pos,
                                                     final boolean alwaysOpen)
    {
        final BiPredicate<WorldAccess, BlockPos> isChestBlocked = alwaysOpen ? (_world, _pos) -> false : this::isBlocked;
        return DoubleBlockProperties.toPropertySource(blockEntityType.get(), BaseChestBlock::getMergeType,
                                                      BaseChestBlock::getDirectionToAttached, HORIZONTAL_FACING, state, world, pos,
                                                      isChestBlocked);
    }

    protected boolean isBlocked(final WorldAccess world, final BlockPos pos) { return ChestBlock.isChestBlocked(world, pos); }

    @Nullable
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(final BlockState state, final World world, final BlockPos pos)
    {
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand,
                              final BlockHitResult hit)
    {
        if (world.isClient)
        {
            NewContainerLibraryClient.INSTANCE.openContainer(state, world, pos);
            player.increaseStat(getOpenStat(), 1);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer,
                         final ItemStack stack)
    {
        if (stack.hasCustomName())
        {
            final BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractChestBlockEntity)
            {
                ((AbstractChestBlockEntity) blockEntity).setCustomName(stack.getName());
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStateReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState,
                                final boolean moved)
    {
        if (state.getBlock() != newState.getBlock())
        {
            final BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory)
            {
                ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
                world.updateNeighborsAlways(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    // todo: look at and see if it can be updated, specifically want to remove "BlockState state;", "Direction direction_3;" if possible
    // todo: add config to prevent automatic merging of chests.
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext context)
    {
        final World world = context.getWorld();
        final BlockPos pos = context.getBlockPos();
        CursedChestType chestType = CursedChestType.SINGLE;
        final Direction direction_1 = context.getPlayerFacing().getOpposite();
        final Direction direction_2 = context.getSide();
        if (context.shouldCancelInteraction())
        {
            final BlockState state;
            final Direction direction_3;
            if (direction_2.getAxis().isVertical())
            {
                state = world.getBlockState(pos.offset(direction_2.getOpposite()));
                direction_3 = state.getBlock() == this && state.get(TYPE) == CursedChestType.SINGLE ? state.get(HORIZONTAL_FACING) : null;
                if (direction_3 != null && direction_3.getAxis() != direction_2.getAxis() && direction_3 == direction_1)
                {
                    chestType = direction_2 == Direction.UP ? CursedChestType.TOP : CursedChestType.BOTTOM;
                }
            }
            else
            {
                Direction offsetDir = direction_2.getOpposite();
                final BlockState clickedBlock = world.getBlockState(pos.offset(offsetDir));
                if (clickedBlock.getBlock() == this && clickedBlock.get(TYPE) == CursedChestType.SINGLE)
                {
                    if (clickedBlock.get(HORIZONTAL_FACING) == direction_2 && clickedBlock.get(HORIZONTAL_FACING) == direction_1)
                    {
                        chestType = CursedChestType.FRONT;
                    }
                    else
                    {
                        state = world.getBlockState(pos.offset(direction_2.getOpposite()));
                        if (state.get(HORIZONTAL_FACING).getHorizontal() < 2) { offsetDir = offsetDir.getOpposite(); }
                        if (direction_1 == state.get(HORIZONTAL_FACING))
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
                final BlockState state = world.getBlockState(pos.offset(dir));
                if (state.getBlock() != this || state.get(TYPE) != CursedChestType.SINGLE || state.get(HORIZONTAL_FACING) != direction_1)
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
        return getDefaultState().with(HORIZONTAL_FACING, direction_1).with(TYPE, chestType);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction offset, final BlockState offsetState,
                                                final WorldAccess world, final BlockPos pos, final BlockPos offsetPos)
    {
        final DoubleBlockProperties.Type mergeType = getMergeType(state);
        if (mergeType == DoubleBlockProperties.Type.SINGLE)
        {
            final Direction facing = state.get(HORIZONTAL_FACING);
            if (!offsetState.contains(TYPE)) { return state.with(TYPE, CursedChestType.SINGLE); }
            final CursedChestType newType = getChestType(facing, offset);
            if (offsetState.get(TYPE) == newType.getOpposite() && facing == offsetState.get(HORIZONTAL_FACING))
            {
                return state.with(TYPE, newType);
            }
        }
        else if (world.getBlockState(pos.offset(getDirectionToAttached(state))).getBlock() != this)
        {
            return state.with(TYPE, CursedChestType.SINGLE);
        }
        return super.getStateForNeighborUpdate(state, offset, offsetState, world, pos, offsetPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos)
    {
        return combine(state, world, pos, true).apply(INVENTORY_GETTER).map(ScreenHandler::calculateComparatorOutput).orElse(0);
    }

    private Stat<Identifier> getOpenStat() { return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST); }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(final BlockState state, final BlockMirror mirror)
    {
        return state.rotate(mirror.getRotation(state.get(HORIZONTAL_FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(final BlockState state, final BlockRotation rotation)
    {
        return state.with(HORIZONTAL_FACING, rotation.rotate(state.get(HORIZONTAL_FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public final boolean hasComparatorOutput(final BlockState state) { return true; }

    public abstract <R extends Registries.TierData> SimpleRegistry<R> getDataRegistry();

    @Override // keep for hoppers.
    public SidedInventory getInventory(final BlockState state, final WorldAccess world, final BlockPos pos)
    {
        return combine(state, world, pos, true).apply(INVENTORY_GETTER).orElse(null);
    }

    @Override
    public ExtendedScreenHandlerFactory createFactory(final Identifier playerPreference, final ScreenMeta meta, final BlockState state, final ServerWorld world, final BlockPos pos)
    {
        PropertySource<? extends T> propertySource = combine(state, world, pos, true);
        SidedInventory inventory = propertySource.apply(INVENTORY_GETTER).orElse(null);
        Text containerName = propertySource.apply(NAME_GETTER).orElse(new LiteralText(""));
        return NewContainerLibrary.INSTANCE.createScreenHandlerFactory(playerPreference, meta, inventory, containerName);
    }
}