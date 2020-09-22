package ninjaphenix.renderingtests.barrel;

import net.fabricmc.fabric.api.client.model.*;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import ninjaphenix.renderingtests.Initializer;
import ninjaphenix.renderingtests.LazyBlockItem;
import ninjaphenix.renderingtests.Main;

public class BarrelInitializer implements Initializer
{
    public static final Initializer INSTANCE = new BarrelInitializer();

    public static final LazyBlockItem IRON_BARREL;
    public static final LazyBlockItem GOLD_BARREL;
    public static final LazyBlockItem DIAMOND_BARREL;
    public static final LazyBlockItem OBSIDIAN_BARREL;
    public static final LazyBlockItem NETHERITE_BARREL;
    public static BlockEntityType<?> BARREL_BLOCK_ENTITY;

    static
    {
        // going to need custom barrel item eventually.
        IRON_BARREL = new LazyBlockItem(Main.resloc("iron_barrel"), () -> new BarrelBlock(makeProperties(1, Blocks.IRON_BLOCK)),
                                        (block) -> new BarrelItem(block, new Item.Properties()));
        GOLD_BARREL = new LazyBlockItem(Main.resloc("gold_barrel"), () -> new BarrelBlock(makeProperties(2, Blocks.GOLD_BLOCK)),
                                        (block) -> new BarrelItem(block, new Item.Properties()));
        DIAMOND_BARREL = new LazyBlockItem(Main.resloc("diamond_barrel"), () -> new BarrelBlock(makeProperties(2, Blocks.DIAMOND_BLOCK)),
                                           (block) -> new BarrelItem(block, new Item.Properties()));
        OBSIDIAN_BARREL = new LazyBlockItem(Main.resloc("obsidian_barrel"), () -> new BarrelBlock(makeProperties(3, Blocks.OBSIDIAN)),
                                            (block) -> new BarrelItem(block, new Item.Properties()));
        NETHERITE_BARREL = new LazyBlockItem(Main.resloc("netherite_barrel"), () -> new BarrelBlock(makeProperties(3, Blocks.NETHERITE_BLOCK)),
                                             (block) -> new BarrelItem(block, new Item.Properties()));
    }

    private static FabricBlockSettings makeProperties(final int harvestLevel, final Block base)
    {
        return FabricBlockSettings.copyOf(Blocks.BARREL).breakByTool(FabricToolTags.AXES, harvestLevel)
                .strength(base.defaultBlockState().getDestroySpeed(null, null), base.getExplosionResistance());
    }

    @Override
    public void onCommon()
    {
        IRON_BARREL.register();
        GOLD_BARREL.register();
        DIAMOND_BARREL.register();
        OBSIDIAN_BARREL.register();
        NETHERITE_BARREL.register();
        BARREL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Main.resloc("barrel"), BlockEntityType.Builder.of(BarrelBlockEntity::new,
                                                                                                                              IRON_BARREL.block(),
                                                                                                                              GOLD_BARREL.block(),
                                                                                                                              DIAMOND_BARREL.block(),
                                                                                                                              OBSIDIAN_BARREL.block(),
                                                                                                                              NETHERITE_BARREL.block()).build(null));
    }

    @Override
    public void onClient()
    {
        ClientSpriteRegistryCallback.event(TextureAtlas.LOCATION_BLOCKS).register(
                (atlas, registry) ->
                {
                    registry.register(Main.resloc("block/iron_barrel_strip"));
                    registry.register(Main.resloc("block/gold_barrel_strip"));
                    registry.register(Main.resloc("block/diamond_barrel_strip"));
                    registry.register(Main.resloc("block/obsidian_barrel_strip"));
                    registry.register(Main.resloc("block/netherite_barrel_strip"));
                });

        ModelLoadingRegistry.INSTANCE.registerVariantProvider(
                manager -> (modelResLoc, modelProviderContext) ->
                {
                    if (Main.MOD_ID.equals(modelResLoc.getNamespace()))
                    {
                        return new BarrelModel();
                    }
                    // Ignore any other mods / vanilla models.
                    return null;
                });
    }
}