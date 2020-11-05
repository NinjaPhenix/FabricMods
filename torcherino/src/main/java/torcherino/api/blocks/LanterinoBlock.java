package torcherino.api.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import torcherino.api.TierSupplier;
import torcherino.api.TorcherinoLogic;
import torcherino.api.blocks.entity.TorcherinoBlockEntity;

import java.util.Random;

public class LanterinoBlock extends LanternBlock implements EntityBlock, TierSupplier
{
    private final ResourceLocation tierID;

    public LanterinoBlock(final ResourceLocation tier)
    {
        super(BlockBehaviour.Properties.copy(Blocks.LANTERN));
        this.tierID = tier;
    }

    private static boolean isEmittingStrongRedstonePower(final Level level, final BlockPos pos, final Direction direction)
    {
        BlockState state = level.getBlockState(pos);
        return state.getDirectSignal(level, pos, direction) > 0;
    }

    @Override
    public ResourceLocation getTier() { return tierID; }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) { return new TorcherinoBlockEntity(pos, state); }

    @Override
    public PushReaction getPistonPushReaction(final BlockState state) { return PushReaction.IGNORE; }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(final BlockState newState, final Level level, final BlockPos pos, final BlockState state, final boolean boolean_1)
    {
        neighborChanged(null, level, pos, null, null, false);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final Random random)
    {
        TorcherinoLogic.scheduledTick(state, level, pos, random);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(final BlockState state, final Level level, final BlockPos pos, final Player player,
                                 final InteractionHand hand, final BlockHitResult hit)
    {
        return TorcherinoLogic.onUse(state, level, pos, player, hand, hit);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block neighborBlock,
                                final BlockPos neighborPos, final boolean boolean_1)
    {
        TorcherinoLogic.neighborUpdate(state, level, pos, neighborBlock, neighborPos, boolean_1, (be) ->
        {
            if (state == null) { return; }
            if (state.getValue(BlockStateProperties.HANGING).equals(true))
            {
                be.setPoweredByRedstone(level.hasSignal(pos.above(), Direction.UP));
            }
            else
            {
                boolean powered = isEmittingStrongRedstonePower(level, pos.west(), Direction.WEST) ||
                        isEmittingStrongRedstonePower(level, pos.east(), Direction.EAST) ||
                        isEmittingStrongRedstonePower(level, pos.south(), Direction.SOUTH) ||
                        isEmittingStrongRedstonePower(level, pos.north(), Direction.NORTH);
                be.setPoweredByRedstone(powered);
            }
        });

    }

    @Override
    public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack stack)
    {
        TorcherinoLogic.onPlaced(level, pos, state, placer, stack, this);
    }
}
