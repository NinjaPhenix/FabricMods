package torcherino.api.blocks;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import torcherino.api.TierSupplier;
import torcherino.api.TorcherinoLogic;
import torcherino.api.blocks.entity.TorcherinoBlockEntity;

import java.util.Random;

@SuppressWarnings({ "SpellCheckingInspection", "deprecation" })
public class WallTorcherinoBlock extends WallTorchBlock implements BlockEntityProvider, TierSupplier
{
    private final Identifier tierID;
    private final DefaultParticleType flameParticle;

    public WallTorcherinoBlock(Identifier tier, Identifier dropID)
    {
        super(FabricBlockSettings.copy(Blocks.WALL_TORCH).drops(dropID).build());
        this.tierID = tier;
        String path = tier.getPath() + "_flame";
        if (path.equals("normal_flame")) { path = "flame"; }
        flameParticle = (DefaultParticleType) Registry.PARTICLE_TYPE.get(new Identifier(tier.getNamespace(), path));
    }

    @Override
    public Identifier getTier() { return tierID; }

    @Override
    public BlockEntity createBlockEntity(BlockView view) { return new TorcherinoBlockEntity(); }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) { return PistonBehavior.IGNORE; }

    @Override
    public void onBlockAdded(BlockState newState, World world, BlockPos pos, BlockState state, boolean boolean_1)
    {
        neighborUpdate(newState, world, pos, null, null, false);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        TorcherinoLogic.scheduledTick(state, world, pos, random);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        return TorcherinoLogic.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean boolean_1)
    {
        TorcherinoLogic.neighborUpdate(state, world, pos, neighborBlock, neighborPos, boolean_1, (be) -> be.setPoweredByRedstone(
                world.isEmittingRedstonePower(pos.offset(state.get(Properties.HORIZONTAL_FACING).getOpposite()), state.get(Properties.HORIZONTAL_FACING))));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        TorcherinoLogic.onPlaced(world, pos, state, placer, stack, this);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rnd)
    {
        Direction direction = state.get(FACING);
        double d = pos.getX() + 0.5D;
        double e = pos.getY() + 0.7D;
        double f = pos.getZ() + 0.5D;
        Direction direction2 = direction.getOpposite();
        world.addParticle(ParticleTypes.SMOKE, d + 0.27D * direction2.getOffsetX(), e + 0.22D, f + 0.27D * direction2.getOffsetZ(), 0.0D, 0.0D, 0.0D);
        world.addParticle(flameParticle, d + 0.27D * direction2.getOffsetX(), e + 0.22D, f + 0.27D * direction2.getOffsetZ(), 0.0D, 0.0D, 0.0D);
    }
}
