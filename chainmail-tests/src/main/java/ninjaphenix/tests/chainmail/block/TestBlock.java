package ninjaphenix.tests.chainmail.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import ninjaphenix.tests.chainmail.block.entity.TestBlockEntity;

public class TestBlock extends Block implements EntityBlock
{
    public TestBlock(Properties settings) { super(settings); }

    @Override
    public BlockEntity newBlockEntity(BlockGetter view) { return new TestBlockEntity(); }
}