package ninjaphenix.tests.chainmail.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.tests.chainmail.block.entity.TestBlockEntity;
import org.jetbrains.annotations.Nullable;

public class TestBlock extends Block implements EntityBlock
{
    public TestBlock(Properties settings) { super(settings); }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) { return new TestBlockEntity(pos, state); }
}