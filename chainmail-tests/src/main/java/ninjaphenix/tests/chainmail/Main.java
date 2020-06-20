package ninjaphenix.tests.chainmail;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
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
        final Block TEST_BLOCK = Registry.register(Registry.BLOCK, new Identifier("test_a", "test_block"), new TestBlock(Block.Settings.of(Material.CLAY)));
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("test_a", "test_block_entity"),
                BlockEntityType.Builder.create(TestBlockEntity::new, TEST_BLOCK).build(null));
        Registry.register(Registry.ITEM, new Identifier("test_a", "test_block"), new BlockItem(TEST_BLOCK, new Item.Settings()));

        Config g = new JanksonConfigParser.Builder().build()
                                                    .load(Config.class, FabricLoader.getInstance().getConfigDirectory().toPath().resolve("test/config.json"),
                                                            new MarkerManager.Log4jMarker("chainmail-tests"));
        System.out.println(g.a);
        System.out.println(g.B);

    }
}
