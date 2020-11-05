package ninjaphenix.expandedstorage.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import ninjaphenix.expandedstorage.common.ModContent;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.common.misc.CursedChestType;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public final class CursedChestBlock extends FluidLoggableChestBlock<CursedChestBlockEntity>
{
    private static final VoxelShape SINGLE_SHAPE = box(1, 0, 1, 15, 14, 15);
    private static final VoxelShape TOP_SHAPE = box(1, 0, 1, 15, 14, 15);
    private static final VoxelShape BOTTOM_SHAPE = box(1, 0, 1, 15, 16, 15);
    private static final VoxelShape[] HORIZONTAL_VALUES = {
            box(1, 0, 0, 15, 14, 15),
            box(1, 0, 1, 16, 14, 15),
            box(1, 0, 1, 15, 14, 16),
            box(0, 0, 1, 15, 14, 15)
    };

    public CursedChestBlock(final Properties settings, final ResourceLocation tierId)
    {
        super(settings, tierId, () -> ModContent.CHEST);
        registerDefaultState(defaultBlockState().setValue(HORIZONTAL_FACING, Direction.SOUTH));
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) { return new CursedChestBlockEntity(pos, state); }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final Level level, final BlockState state, final BlockEntityType<T> type)
    {
        return level.isClientSide ? createTickerHelper(type, ModContent.CHEST, CursedChestBlockEntity::lidAnimateTick) : null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final Random random)
    {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CursedChestBlockEntity)
        {
            ((CursedChestBlockEntity) blockEntity).recheckOpen();
        }

    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(final BlockState state, final BlockGetter view, final BlockPos pos, final CollisionContext context)
    {
        final CursedChestType type = state.getValue(TYPE);
        if (type == CursedChestType.TOP) { return TOP_SHAPE; }
        else if (type == CursedChestType.BOTTOM) { return BOTTOM_SHAPE; }
        else if (type == CursedChestType.SINGLE) {return SINGLE_SHAPE; }
        else { return HORIZONTAL_VALUES[(state.getValue(HORIZONTAL_FACING).get2DDataValue() + type.getOffset()) % 4]; }
    }

    @Override
    public RenderShape getRenderShape(final BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }

    @Override
    @SuppressWarnings({"unchecked"})
    public MappedRegistry<Registries.ChestTierData> getDataRegistry() { return Registries.CHEST; }
}