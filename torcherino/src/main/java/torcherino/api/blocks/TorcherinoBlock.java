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
import net.minecraft.world.level.BlockGetter;
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

@SuppressWarnings({ "deprecation" })
public class TorcherinoBlock extends TorchBlock implements EntityBlock, TierSupplier
{
    private final ResourceLocation tierID;

    public TorcherinoBlock(ResourceLocation tier, ParticleOptions particleEffect)
    {
        super(BlockBehaviour.Properties.copy(Blocks.TORCH), particleEffect);
        tierID = tier;
    }

    @Override
    public ResourceLocation getTier() { return tierID; }

    @Override
    public BlockEntity newBlockEntity(BlockGetter view) { return new TorcherinoBlockEntity(); }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) { return PushReaction.IGNORE; }

    @Override
    public void onPlace(BlockState newState, Level world, BlockPos pos, BlockState state, boolean boolean_1)
    {
        neighborChanged(null, world, pos, null, null, false);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random)
    {
        TorcherinoLogic.scheduledTick(state, world, pos, random);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return TorcherinoLogic.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean boolean_1)
    {
        TorcherinoLogic.neighborUpdate(state, world, pos, neighborBlock, neighborPos, boolean_1, (be) ->
                be.setPoweredByRedstone(world.hasSignal(pos.below(), Direction.UP)));
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        TorcherinoLogic.onPlaced(world, pos, state, placer, stack, this);
    }
}
