package ninjaphenix.tests.chainmail;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import ninjaphenix.chainmail.api.config.JanksonConfigParser;
import ninjaphenix.tests.chainmail.block.TestBlock;
import ninjaphenix.tests.chainmail.block.entity.TestBlockEntity;
import ninjaphenix.tests.chainmail.config.Config;
import org.apache.logging.log4j.MarkerManager;

public class Main implements ModInitializer
{
    public static Main INSTANCE = new Main();

    @Override
    public void onInitialize()
    {
        final Block TEST_BLOCK = Registry.register(Registry.BLOCK, new ResourceLocation("test_a", "test_block"),
                                                   new TestBlock(BlockBehaviour.Properties.of(Material.BAMBOO)));
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation("test_a", "test_block_entity"),
                          BlockEntityType.Builder.of(TestBlockEntity::new, TEST_BLOCK).build(null));
        Registry.register(Registry.ITEM, new ResourceLocation("test_a", "test_block"), new BlockItem(TEST_BLOCK, new Item.Properties()));
        final Config g = new JanksonConfigParser.Builder().build().load(Config.class, Config::new,
                                                                  FabricLoader.getInstance().getConfigDir().resolve("test/config.json"),
                                                                  new MarkerManager.Log4jMarker("chainmail-tests"));
        System.out.println(g.a);
        System.out.println(g.B);

    }
}