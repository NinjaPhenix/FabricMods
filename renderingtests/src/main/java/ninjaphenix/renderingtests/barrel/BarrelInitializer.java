package ninjaphenix.renderingtests.barrel;

import net.fabricmc.fabric.api.client.model.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import ninjaphenix.renderingtests.Initializer;
import ninjaphenix.renderingtests.LazyBlockItem;
import ninjaphenix.renderingtests.Main;

public class BarrelInitializer implements Initializer
{
    public static final BarrelInitializer INSTANCE = new BarrelInitializer();

    public static final LazyBlockItem IRON_BARREL;
    public static final LazyBlockItem GOLD_BARREL;
    public static final LazyBlockItem DIAMOND_BARREL;
    public static final LazyBlockItem OBSIDIAN_BARREL;
    public static final LazyBlockItem NETHERITE_BARREL;

    static
    {
        // going to need custom barrel item eventually.
        IRON_BARREL = new LazyBlockItem(Main.resloc("iron_barrel"), () -> new BarrelBlock(makeProperties(1, Blocks.IRON_BLOCK)), Item.Properties::new);
        GOLD_BARREL = new LazyBlockItem(Main.resloc("gold_barrel"), () -> new BarrelBlock(makeProperties(2, Blocks.GOLD_BLOCK)), Item.Properties::new);
        DIAMOND_BARREL = new LazyBlockItem(Main.resloc("diamond_barrel"), () -> new BarrelBlock(makeProperties(2, Blocks.DIAMOND_BLOCK)), Item.Properties::new);
        OBSIDIAN_BARREL = new LazyBlockItem(Main.resloc("obsidian_barrel"), () -> new BarrelBlock(makeProperties(3, Blocks.OBSIDIAN)), Item.Properties::new);
        NETHERITE_BARREL = new LazyBlockItem(Main.resloc("netherite_barrel"), () -> new BarrelBlock(makeProperties(3, Blocks.NETHERITE_BLOCK)), Item.Properties::new);
    }
    
    private static FabricBlockSettings makeProperties(final int harvestLevel, final Block base)
    {
        return FabricBlockSettings.copyOf(Blocks.BARREL).breakByTool(FabricToolTags.AXES, harvestLevel)
                .requiresTool().strength(base.defaultBlockState().getDestroySpeed(null, null), base.getExplosionResistance());
    }

    @Override
    public void onCommon()
    {
        IRON_BARREL.register();
        GOLD_BARREL.register();
        DIAMOND_BARREL.register();
        OBSIDIAN_BARREL.register();
        NETHERITE_BARREL.register();
    }

    @Override
    public void onClient()
    {
        ModelLoadingRegistry.INSTANCE.registerVariantProvider(
                manager -> (modelResLoc, modelProviderContext) ->
                {
                    if (Main.MOD_ID.equals(modelResLoc.getNamespace()))
                    {
                        System.out.println("Loading model variant: " + modelResLoc);
                    }
                    // Ignore any other mods / vanilla models.
                    return null;
                });
    }
}