package torcherino.api.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
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
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import torcherino.api.TierSupplier;
import torcherino.api.TorcherinoLogic;
import torcherino.api.blocks.entity.TorcherinoBlockEntity;

import java.util.Random;

public class TorcherinoBlock extends TorchBlock implements EntityBlock, TierSupplier
{
    private final ResourceLocation tierID;

    public TorcherinoBlock(final ResourceLocation tier, final ParticleOptions particleEffect)
    {
        super(BlockBehaviour.Properties.copy(Blocks.TORCH), particleEffect);
        tierID = tier;
    }

    @Override
    public ResourceLocation getTier() { return tierID; }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) { return new TorcherinoBlockEntity(pos, state); }

    @Override
    @SuppressWarnings({ "deprecation" })
    public PushReaction getPistonPushReaction(final BlockState state) { return PushReaction.IGNORE; }

    @Override
    @SuppressWarnings({ "deprecation" })
    public void onPlace(final BlockState newState, final Level level, final BlockPos pos, final BlockState state, final boolean boolean_1)
    {
        neighborChanged(null, level, pos, null, null, false);
    }

    @Override
    @SuppressWarnings({ "deprecation" })
    public void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final Random random)
    {
        TorcherinoLogic.scheduledTick(state, level, pos, random);
    }

    @Override
    @SuppressWarnings({ "deprecation" })
    public InteractionResult use(final BlockState state, final Level level, final BlockPos pos, final Player player,
                                 final InteractionHand hand, final BlockHitResult hit)
    {
        return TorcherinoLogic.onUse(state, level, pos, player, hand, hit);
    }

    @Override
    @SuppressWarnings({ "deprecation" })
    public void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block neighborBlock,
                                final BlockPos neighborPos, final boolean boolean_1)
    {
        TorcherinoLogic.neighborUpdate(state, level, pos, neighborBlock, neighborPos, boolean_1, (be) ->
                be.setPoweredByRedstone(level.hasSignal(pos.below(), Direction.UP)));
    }

    @Override
    public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack stack)
    {
        TorcherinoLogic.onPlaced(level, pos, state, placer, stack, this);
    }
}