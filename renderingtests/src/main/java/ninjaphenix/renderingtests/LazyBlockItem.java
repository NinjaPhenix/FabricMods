package ninjaphenix.renderingtests;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import java.util.function.Supplier;

public class LazyBlockItem
{
    private final ResourceLocation resourceLocation;
    private final Supplier<Block> blockSupplier;
    private final Supplier<Item.Properties> itemPropertiesSupplier;

    private BlockItem value;

    public LazyBlockItem(final ResourceLocation resloc, final Supplier<Block> block, final Supplier<Item.Properties> properties)
    {
        resourceLocation = resloc;
        blockSupplier = block;
        itemPropertiesSupplier = properties;
    }

    public void register()
    {
        final Block block = blockSupplier.get();
        Registry.register(Registry.BLOCK, resourceLocation, block);
        final BlockItem item = new BlockItem(block, itemPropertiesSupplier.get());
        Registry.register(Registry.ITEM, resourceLocation, item);
        value = item;
    }

    public boolean isRegistered() { return value != null; }

    public ResourceLocation resLoc() { return resourceLocation; }

    public Block block() { return value.getBlock(); }

    public Item item() { return value; }
}