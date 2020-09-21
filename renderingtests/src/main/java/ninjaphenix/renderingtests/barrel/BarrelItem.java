package ninjaphenix.renderingtests.barrel;

import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BarrelItem extends BlockItem
{
    public BarrelItem(final Block block, final Properties properties)
    {
        super(block, properties);
    }

    @Override
    public ItemStack getDefaultInstance()
    {
        final ItemStack stack =  super.getDefaultInstance();
        stack.getOrCreateTagElement("BlockEntityTag").putString("base", Registry.BLOCK.getKey(Blocks.BARREL).toString());
        return stack;
    }
}
