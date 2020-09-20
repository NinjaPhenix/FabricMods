package ninjaphenix.renderingtests.barrel;

import net.fabricmc.fabric.api.client.model.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import ninjaphenix.renderingtests.Initializer;
import ninjaphenix.renderingtests.Main;

public class BarrelInitializer implements Initializer
{
    public static final BarrelInitializer INSTANCE = new BarrelInitializer();

    public static final BarrelBlock BARREL_BLOCK;
    private static final Item BARREL_ITEM;
    private static final ResourceLocation BARREL = new ResourceLocation(Main.MOD_ID, "barrel");

    static
    {
        BARREL_BLOCK = new BarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL));
        BARREL_ITEM = new BlockItem(BARREL_BLOCK, new Item.Properties());
    }

    @Override
    public void onCommon()
    {
        Registry.register(Registry.BLOCK, BARREL, BARREL_BLOCK);
        Registry.register(Registry.ITEM, BARREL, BARREL_ITEM);
    }

    @Override
    public void onClient()
    {
        ModelLoadingRegistry.INSTANCE.registerVariantProvider(
                manager -> (modelResLoc, modelProviderContext) ->
                {
                    if (Main.MOD_ID.equals(modelResLoc.getNamespace()))
                    {
                        
                    }
                    System.out.println("Loading model variant: " + modelResLoc);
                    return null;
                });
    }
}