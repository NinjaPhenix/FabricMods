package ninjaphenix.expandedstorage.block.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.container.Container;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public abstract class BasicStorageBlock extends Block implements BlockEntityProvider, InventoryProvider
{
    protected BasicStorageBlock(Settings settings) { super(settings); }

    @Override
    public boolean hasComparatorOutput(BlockState state) { return true; }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) { return Container.calculateComparatorOutput(getInventory(state, world, pos)); }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (state.getBlock() != newState.getBlock())
        {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory)
            {
                ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
                world.updateNeighbors(pos, this);
            }
            super.onBlockRemoved(state, world, pos, newState, moved);
        }
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) { return PistonBehavior.IGNORE; }
}
