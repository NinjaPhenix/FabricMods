package ninjaphenix.expandedstorage.common.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.entity.BarrelBlockEntity;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.OPEN;

public class BarrelBlock extends StorageBlock
{
    public BarrelBlock(final Properties builder, final ResourceLocation tierId)
    {
        super(builder, tierId);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, OPEN);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(final BlockState state, final ServerLevel world, final BlockPos pos, final Random random)
    {
        final BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BarrelBlockEntity)
        {
            ((BarrelBlockEntity) blockEntity).tick();
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    @SuppressWarnings("unchecked")
    public MappedRegistry<Registries.TierData> getDataRegistry() { return Registries.BARREL; }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockGetter world) { return new BarrelBlockEntity(TIER_ID); }

    @Override
    protected ResourceLocation getOpenStat() { return Stats.OPEN_BARREL; }
}