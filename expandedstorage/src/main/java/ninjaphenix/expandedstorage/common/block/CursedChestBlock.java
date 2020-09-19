package ninjaphenix.expandedstorage.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import ninjaphenix.expandedstorage.common.ModContent;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.common.misc.CursedChestType;

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
    public BlockEntity newBlockEntity(final BlockGetter view) { return new CursedChestBlockEntity(TIER_ID); }

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