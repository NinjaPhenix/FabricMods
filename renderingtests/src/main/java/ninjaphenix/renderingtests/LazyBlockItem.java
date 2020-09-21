package ninjaphenix.renderingtests;

import com.mojang.datafixers.types.Func;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import java.util.function.Function;
import java.util.function.Supplier;

public class LazyBlockItem
{
    private final ResourceLocation resourceLocation;
    private final Supplier<Block> blockSupplier;
    private final Function<Block, BlockItem> itemSupplier;

    private BlockItem value;

    public LazyBlockItem(final ResourceLocation resloc, final Supplier<Block> block, final Function<Block, BlockItem> item)
    {
        resourceLocation = resloc;
        blockSupplier = block;
        itemSupplier = item;
    }

    public void register()
    {
        final Block block = blockSupplier.get();
        Registry.register(Registry.BLOCK, resourceLocation, block);
        final BlockItem item = itemSupplier.apply(block);
        Registry.register(Registry.ITEM, resourceLocation, item);
        value = item;
    }

    public boolean isRegistered() { return value != null; }

    public ResourceLocation resLoc() { return resourceLocation; }

    public Block block() { return value.getBlock(); }

    public Item item() { return value; }
}