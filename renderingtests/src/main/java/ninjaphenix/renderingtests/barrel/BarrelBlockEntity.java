package ninjaphenix.renderingtests.barrel;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BarrelBlockEntity extends BlockEntity
{
    private ResourceLocation BASE_BLOCK;

    public BarrelBlockEntity()
    {
        super(BarrelInitializer.BARREL_BLOCK_ENTITY);
    }

    @Override
    public CompoundTag save(final CompoundTag compoundTag)
    {
        final CompoundTag tag = super.save(compoundTag);
        if (BASE_BLOCK != null) { tag.putString("base", BASE_BLOCK.toString()); }
        return tag;
    }

    @Override
    public void load(final BlockState blockState, final CompoundTag compoundTag)
    {
        super.load(blockState, compoundTag);
        BASE_BLOCK = new ResourceLocation(compoundTag.getString("base"));
    }
}
