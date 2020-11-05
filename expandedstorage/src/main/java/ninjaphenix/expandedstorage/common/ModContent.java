package ninjaphenix.expandedstorage.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import ninjaphenix.chainmail.api.ChainmailCommonApi;
import ninjaphenix.expandedstorage.common.block.BarrelBlock;
import ninjaphenix.expandedstorage.common.block.CursedChestBlock;
import ninjaphenix.expandedstorage.common.block.OldChestBlock;
import ninjaphenix.expandedstorage.common.block.entity.BarrelBlockEntity;
import ninjaphenix.expandedstorage.common.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.common.block.entity.OldChestBlockEntity;
import ninjaphenix.expandedstorage.common.inventory.PagedScreenHandler;
import ninjaphenix.expandedstorage.common.inventory.ScrollableScreenHandler;
import ninjaphenix.expandedstorage.common.inventory.SingleScreenHandler;
import ninjaphenix.expandedstorage.common.item.ConversionItem;
import ninjaphenix.expandedstorage.common.item.MutatorItem;

public final class ModContent
{
    public static final MenuType<PagedScreenHandler> PAGED_HANDLER_TYPE;
    public static final MenuType<SingleScreenHandler> SINGLE_HANDLER_TYPE;
    public static final MenuType<ScrollableScreenHandler> SCROLLABLE_HANDLER_TYPE;
    public static final BlockEntityType<CursedChestBlockEntity> CHEST;
    public static final BlockEntityType<OldChestBlockEntity> OLD_CHEST;
    public static final BlockEntityType<BarrelBlockEntity> BARREL;
    public static final CursedChestBlock DIAMOND_CHEST;

    static
    {
        SCROLLABLE_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(Const.resloc("scrollable"), new ScrollableScreenHandler.Factory());
        PAGED_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(Const.resloc("paged"), new PagedScreenHandler.Factory());
        SINGLE_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(Const.resloc("single"), new SingleScreenHandler.Factory());
        final CreativeModeTab tab = ChainmailCommonApi.INSTANCE.registerItemGroup((index) -> new CreativeModeTab(index, Const.MOD_ID)
        {
            @Override
            @Environment(EnvType.CLIENT)
            public ItemStack makeIcon() { return new ItemStack(DIAMOND_CHEST); }
        });
        final ResourceLocation WOOD = Const.resloc("wood");
        final ResourceLocation IRON = Const.resloc("iron");
        final ResourceLocation GOLD = Const.resloc("gold");
        final ResourceLocation DIAMOND = Const.resloc("diamond");
        final ResourceLocation OBSIDIAN = Const.resloc("obsidian");
        final ResourceLocation NETHERITE = Const.resloc("netherite");
        CHEST = Registry.register(
                Registry.BLOCK_ENTITY_TYPE, Const.resloc("cursed_chest"),
                BlockEntityType.Builder.of(
                        CursedChestBlockEntity::new,
                        chest(Blocks.OAK_PLANKS, Const.resloc("wood_chest"), WOOD, 3, tab),
                        chest(Blocks.PUMPKIN, Const.resloc("pumpkin_chest"), Const.resloc("pumpkin"), 3, tab),
                        chest(Blocks.OAK_PLANKS, Const.resloc("christmas_chest"), Const.resloc("christmas"), 3, tab),
                        chest(Blocks.IRON_BLOCK, Const.resloc("iron_chest"), IRON, 6, tab),
                        chest(Blocks.GOLD_BLOCK, Const.resloc("gold_chest"), GOLD, 9, tab),
                        DIAMOND_CHEST = chest(Blocks.DIAMOND_BLOCK, Const.resloc("diamond_chest"), DIAMOND, 12, tab),
                        chest(Blocks.OBSIDIAN, Const.resloc("obsidian_chest"), OBSIDIAN, 12, tab),
                        chest(Blocks.NETHERITE_BLOCK, Const.resloc("netherite_chest"), NETHERITE, 15, tab))
                        .build(null));
        OLD_CHEST = Registry.register(
                Registry.BLOCK_ENTITY_TYPE, Const.resloc("old_cursed_chest"),
                BlockEntityType.Builder.of(
                        OldChestBlockEntity::new,
                        old(Blocks.OAK_PLANKS, "wood_chest", WOOD, 3, tab),
                        old(Blocks.IRON_BLOCK, "iron_chest", IRON, 6, tab),
                        old(Blocks.GOLD_BLOCK, "gold_chest", GOLD, 9, tab),
                        old(Blocks.DIAMOND_BLOCK, "diamond_chest", DIAMOND, 12, tab),
                        old(Blocks.OBSIDIAN, "obsidian_chest", OBSIDIAN, 12, tab),
                        old(Blocks.NETHERITE_BLOCK, "netherite_chest", NETHERITE, 15, tab))
                        .build(null));
        BARREL = Registry.register(
                Registry.BLOCK_ENTITY_TYPE, Const.resloc("barrel"),
                BlockEntityType.Builder.of(
                        BarrelBlockEntity::new,
                        barrel(1, 5, 6, Const.resloc("iron_barrel"), IRON, 6, tab),
                        barrel(2, 3, 6, Const.resloc("gold_barrel"), GOLD, 9, tab),
                        barrel(2, 5, 6, Const.resloc("diamond_barrel"), DIAMOND, 12, tab),
                        barrel(3, 50, 1200, Const.resloc("obsidian_barrel"), OBSIDIAN, 12, tab),
                        barrel(4, 50, 1200, Const.resloc("netherite_barrel"), NETHERITE, 15, tab)).build(null));
        registerConversionPath(
                tab,
                new Tuple<>(WOOD, "wood"),
                new Tuple<>(IRON, "iron"),
                new Tuple<>(GOLD, "gold"),
                new Tuple<>(DIAMOND, "diamond"),
                new Tuple<>(OBSIDIAN, "obsidian"),
                new Tuple<>(NETHERITE, "netherite"));
        Registry.register(Registry.ITEM, Const.resloc("chest_mutator"), new MutatorItem(new Item.Properties().stacksTo(1).tab(tab)));
    }

    private static BarrelBlock barrel(final int miningLevel, final float hardness, final float resistance,
                                      final ResourceLocation location, final ResourceLocation tierLocation, final int rows,
                                      final CreativeModeTab tab)
    {
        final BlockBehaviour.Properties settings = FabricBlockSettings.copyOf(Blocks.BARREL).breakByTool(FabricToolTags.AXES, miningLevel)
                .strength(hardness, resistance).requiresCorrectToolForDrops();

        final BarrelBlock block = new BarrelBlock(settings, tierLocation);
        Registry.register(Registry.BLOCK, location, block);
        Registry.register(Registry.ITEM, location, new BlockItem(block, new Item.Properties().tab(tab)));
        Registry.register(Registries.BARREL, tierLocation, new Registries.TierData(
                rows * 9, new TranslatableComponent("container.expandedstorage." + location.getPath()), location));
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
        {
            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped());
        }
        return block;
    }

    @SuppressWarnings("EmptyMethod")
    public static void register() { }

    private static OldChestBlock old(final Block material, final String name, final ResourceLocation tierLocation, final int rows,
                                     final CreativeModeTab tab)
    {
        final ResourceLocation registryId = Const.resloc("old_" + name);
        final OldChestBlock block = new OldChestBlock(FabricBlockSettings.copyOf(material), tierLocation);
        Registry.register(Registry.BLOCK, registryId, block);
        Registry.register(Registry.ITEM, registryId, new BlockItem(block, new Item.Properties().tab(tab)));
        Registry.register(Registries.OLD_CHEST, tierLocation, new Registries.TierData(
                rows * 9, new TranslatableComponent("container.expandedstorage." + name), registryId));
        return block;
    }

    private static CursedChestBlock chest(final Block material, final ResourceLocation location, final ResourceLocation tierLocation,
                                          final int rows, final CreativeModeTab tab)
    {
        final CursedChestBlock block = new CursedChestBlock(FabricBlockSettings.copyOf(material), tierLocation);
        Registry.register(Registry.BLOCK, location, block);
        Registry.register(Registry.ITEM, location, new BlockItem(block, new Item.Properties().tab(tab)));
        Registry.register(Registries.CHEST, tierLocation, new Registries.ChestTierData(
                rows * 9, new TranslatableComponent("container.expandedstorage." + location.getPath()), location,
                type -> Const.resloc(String.format("entity/%s/%s", location.getPath(), type.getSerializedName()))));
        return block;
    }

    @SafeVarargs
    private static void registerConversionPath(final CreativeModeTab tab, final Tuple<ResourceLocation, String>... path)
    {
        final int length = path.length;
        for (int i = 0; i < length - 1; i++)
        {
            for (int x = i + 1; x < length; x++)
            {
                final Tuple<ResourceLocation, String> from = path[i];
                final Tuple<ResourceLocation, String> to = path[x];
                final ResourceLocation id = Const.resloc(from.getB() + "_to_" + to.getB() + "_conversion_kit");
                Registry.register(Registry.ITEM, id, new ConversionItem(new Item.Properties().tab(tab).stacksTo(16), from, to));
            }
        }
    }
}