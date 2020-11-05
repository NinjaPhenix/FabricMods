package ninjaphenix.tests.chainmail.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.chainmail.api.blockentity.ExpandedBlockEntity;

public class TestBlockEntity extends BlockEntity implements ExpandedBlockEntity
{
    public TestBlockEntity(final BlockPos pos, final BlockState state)
    {
        super(Registry.BLOCK_ENTITY_TYPE.get(new ResourceLocation("test_a", "test_block_entity")), pos, state);
    }

    @Override
    public void onLoad() { System.out.println("Test block entity instance loaded."); }
}