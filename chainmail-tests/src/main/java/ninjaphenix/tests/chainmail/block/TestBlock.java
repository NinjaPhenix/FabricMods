package ninjaphenix.tests.chainmail.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import ninjaphenix.tests.chainmail.block.entity.TestBlockEntity;

public class TestBlock extends Block implements BlockEntityProvider
{
    public TestBlock(Settings settings) { super(settings); }

    @Override
    public BlockEntity createBlockEntity(BlockView view) { return new TestBlockEntity(); }

    @Override
    public boolean hasBlockEntity()
    {
        return true;
    }
}
