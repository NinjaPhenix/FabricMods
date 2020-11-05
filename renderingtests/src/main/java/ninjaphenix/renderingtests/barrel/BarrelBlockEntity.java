package ninjaphenix.renderingtests.barrel;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BarrelBlockEntity extends BlockEntity
{
    private ResourceLocation BASE_BLOCK;

    public BarrelBlockEntity(final BlockPos pos, final BlockState state)
    {
        super(BarrelInitializer.BARREL_BLOCK_ENTITY, pos, state);
    }

    @Override
    public CompoundTag save(final CompoundTag tag)
    {
        final CompoundTag compoundTag = super.save(tag);
        if (BASE_BLOCK != null) { compoundTag.putString("base", BASE_BLOCK.toString()); }
        return compoundTag;
    }

    @Override
    public void load(final CompoundTag tag)
    {
        super.load(tag);
        BASE_BLOCK = new ResourceLocation(tag.getString("base"));
    }
}
