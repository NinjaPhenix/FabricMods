package ninjaphenix.expandedstorage.common.block;

import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.entity.OldChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import ninjaphenix.expandedstorage.common.ModContent;

public final class OldChestBlock extends ChestBlock<OldChestBlockEntity>
{
    public OldChestBlock(final Properties settings, final ResourceLocation tierId) { super(settings, tierId, () -> ModContent.OLD_CHEST); }

    @Override
    public BlockEntity newBlockEntity(final BlockGetter view) { return new OldChestBlockEntity(TIER_ID); }

    @Override
    protected boolean isBlocked(final LevelAccessor world, final BlockPos pos)
    {
        final BlockPos upPos = pos.above();
        final BlockState upState = world.getBlockState(upPos);
        return (upState.isRedstoneConductor(world, upPos) && upState.getBlock() != this) ||
                world.getEntitiesOfClass(Cat.class, new AABB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1,
                                                                        pos.getY() + 2, pos.getZ() + 1))
                        .stream().anyMatch(Cat::isInSittingPose);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public MappedRegistry<Registries.TierData> getDataRegistry() { return Registries.OLD_CHEST; }
}