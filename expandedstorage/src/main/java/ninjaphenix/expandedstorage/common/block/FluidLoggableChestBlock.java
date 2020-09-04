package ninjaphenix.expandedstorage.common.block;

import ninjaphenix.expandedstorage.common.block.entity.StorageBlockEntity;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

@SuppressWarnings("deprecation")
public abstract class FluidLoggableChestBlock<T extends StorageBlockEntity> extends ChestBlock<T> implements SimpleWaterloggedBlock
{
    protected FluidLoggableChestBlock(final Properties settings, final ResourceLocation tierId,
                                      final Supplier<BlockEntityType<T>> blockEntityType)
    {
        super(settings, tierId, blockEntityType);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    public FluidState getFluidState(final BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public BlockState getStateForPlacement(final BlockPlaceContext context)
    {
        return super.getStateForPlacement(context)
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()) == Fluids.WATER.defaultFluidState());
    }

    @Override
    public BlockState updateShape(final BlockState state, final Direction direction, final BlockState neighborState,
                                                final LevelAccessor world, final BlockPos pos, final BlockPos neighborPos)
    {
        if (state.getValue(WATERLOGGED)) { world.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world)); }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }
}