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
import ninjaphenix.expandedstorage.common.block.entity.BarrelBlockEntity;
import ninjaphenix.expandedstorage.common.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.common.block.entity.OldChestBlockEntity;
import ninjaphenix.expandedstorage.common.block.CursedChestBlock;
import ninjaphenix.expandedstorage.common.block.OldChestBlock;
import ninjaphenix.expandedstorage.common.inventory.PagedScreenHandler;
import ninjaphenix.expandedstorage.common.inventory.ScrollableScreenHandler;
import ninjaphenix.expandedstorage.common.inventory.SingleScreenHandler;
import ninjaphenix.expandedstorage.common.item.ChestConversionItem;
import ninjaphenix.expandedstorage.common.item.ChestMutatorItem;

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
        SCROLLABLE_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(Const.id("scrollable"), new ScrollableScreenHandler.Factory());
        PAGED_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(Const.id("paged"), new PagedScreenHandler.Factory());
        SINGLE_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(Const.id("single"), new SingleScreenHandler.Factory());
        final CreativeModeTab group = ChainmailCommonApi.INSTANCE.registerItemGroup((index) -> new CreativeModeTab(index, Const.MOD_ID)
        {
            @Override
            @Environment(EnvType.CLIENT)
            public ItemStack makeIcon() { return new ItemStack(DIAMOND_CHEST); }
        });

        final ResourceLocation WOOD = Const.id("wood");
        final ResourceLocation IRON = Const.id("iron");
        final ResourceLocation GOLD = Const.id("gold");
        final ResourceLocation DIAMOND = Const.id("diamond");
        final ResourceLocation OBSIDIAN = Const.id("obsidian");
        final ResourceLocation NETHERITE = Const.id("netherite");

        CHEST = Registry.register(Registry.BLOCK_ENTITY_TYPE, Const.id("cursed_chest"),
                                  BlockEntityType.Builder.of(() -> new CursedChestBlockEntity(null),
                                                                 chest(Blocks.OAK_PLANKS, Const.id("wood_chest"), WOOD, 3, group),
                                                                 chest(Blocks.PUMPKIN, Const.id("pumpkin_chest"), Const.id("pumpkin"), 3, group),
                                                                 chest(Blocks.OAK_PLANKS, Const.id("christmas_chest"), Const.id("christmas"), 3, group),
                                                                 chest(Blocks.IRON_BLOCK, Const.id("iron_chest"), IRON, 6, group),
                                                                 chest(Blocks.GOLD_BLOCK, Const.id("gold_chest"), GOLD, 9, group),
                                                                 DIAMOND_CHEST = chest(Blocks.DIAMOND_BLOCK, Const.id("diamond_chest"), DIAMOND, 12, group),
                                                                 chest(Blocks.OBSIDIAN, Const.id("obsidian_chest"), OBSIDIAN, 12, group),
                                                                 chest(Blocks.NETHERITE_BLOCK, Const.id("netherite_chest"), NETHERITE, 15, group)).build(null));
        OLD_CHEST = Registry.register(Registry.BLOCK_ENTITY_TYPE, Const.id("old_cursed_chest"),
                                      BlockEntityType.Builder.of(() -> new OldChestBlockEntity(null),
                                                                     old(Blocks.OAK_PLANKS, "wood_chest", WOOD, 3, group),
                                                                     old(Blocks.IRON_BLOCK, "iron_chest", IRON, 6, group),
                                                                     old(Blocks.GOLD_BLOCK, "gold_chest", GOLD, 9, group),
                                                                     old(Blocks.DIAMOND_BLOCK, "diamond_chest", DIAMOND, 12, group),
                                                                     old(Blocks.OBSIDIAN, "obsidian_chest", OBSIDIAN, 12, group),
                                                                     old(Blocks.NETHERITE_BLOCK, "netherite_chest", NETHERITE, 15, group))
                                              .build(null));
        BARREL = Registry.register(Registry.BLOCK_ENTITY_TYPE, Const.id("barrel"),
                                   BlockEntityType.Builder.of(() -> new BarrelBlockEntity(null),
                                                                  barrel(1, 5, 6, Const.id("iron_barrel"), IRON, 6, group),
                                                                  barrel(2, 3, 6, Const.id("gold_barrel"), GOLD, 9, group),
                                                                  barrel(2, 5, 6, Const.id("diamond_barrel"), DIAMOND, 12, group),
                                                                  barrel(3, 50, 1200, Const.id("obsidian_barrel"), OBSIDIAN, 12, group),
                                                                  barrel(4, 50, 1200, Const.id("netherite_barrel"), NETHERITE, 15, group)).build(null));
        registerConversionPath(group,
                               new Tuple<>(WOOD, "wood"),
                               new Tuple<>(IRON, "iron"),
                               new Tuple<>(GOLD, "gold"),
                               new Tuple<>(DIAMOND, "diamond"),
                               new Tuple<>(OBSIDIAN, "obsidian"),
                               new Tuple<>(NETHERITE, "netherite"));
        Registry.register(Registry.ITEM, Const.id("chest_mutator"), new ChestMutatorItem(new Item.Properties().stacksTo(1).tab(group)));
    }

    private static BarrelBlock barrel(final int miningLevel, final float hardness, final float resistance, final ResourceLocation registryId,
                                      final ResourceLocation genericTier, final int rows, final CreativeModeTab group)
    {
        final BlockBehaviour.Properties settings = FabricBlockSettings.copyOf(Blocks.BARREL).breakByTool(FabricToolTags.AXES, miningLevel)
                .strength(hardness, resistance).requiresCorrectToolForDrops();

        final BarrelBlock block = new BarrelBlock(settings, genericTier);
        Registry.register(Registry.BLOCK, registryId, block);
        Registry.register(Registry.ITEM, registryId, new BlockItem(block, new Item.Properties().tab(group)));
        Registry.register(Registries.BARREL, genericTier, new Registries.TierData(
                rows * 9, new TranslatableComponent("container.expandedstorage." + registryId.getPath()), registryId));
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
        {
            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped());
        }
        return block;
    }

    @SuppressWarnings("EmptyMethod")
    public static void register() { }

    private static OldChestBlock old(final Block material, final String name, final ResourceLocation genericTier, final int rows,
                                     final CreativeModeTab group)
    {
        final ResourceLocation registryId = Const.id("old_" + name);
        final OldChestBlock block = new OldChestBlock(FabricBlockSettings.copyOf(material), genericTier);
        Registry.register(Registry.BLOCK, registryId, block);
        Registry.register(Registry.ITEM, registryId, new BlockItem(block, new Item.Properties().tab(group)));
        Registry.register(Registries.OLD_CHEST, genericTier, new Registries.TierData(
                rows * 9, new TranslatableComponent("container.expandedstorage." + name), registryId));
        return block;
    }

    private static CursedChestBlock chest(final Block material, final ResourceLocation registryId, final ResourceLocation genericTier, final int rows,
                                          final CreativeModeTab group)
    {
        final CursedChestBlock block = new CursedChestBlock(FabricBlockSettings.copyOf(material), genericTier);
        Registry.register(Registry.BLOCK, registryId, block);
        Registry.register(Registry.ITEM, registryId, new BlockItem(block, new Item.Properties().tab(group)));
        Registry.register(Registries.CHEST, genericTier, new Registries.ChestTierData(
                rows * 9, new TranslatableComponent("container.expandedstorage." + registryId.getPath()), registryId,
                type -> Const.id(String.format("entity/%s/%s", registryId.getPath(), type.getSerializedName()))));
        return block;
    }

    @SafeVarargs
    private static void registerConversionPath(final CreativeModeTab group, final Tuple<ResourceLocation, String>... values)
    {
        final int length = values.length;
        for (int i = 0; i < length - 1; i++)
        {
            for (int x = i + 1; x < length; x++)
            {
                final Tuple<ResourceLocation, String> from = values[i];
                final Tuple<ResourceLocation, String> to = values[x];
                final ResourceLocation id = Const.id(from.getB() + "_to_" + to.getB() + "_conversion_kit");
                Registry.register(Registry.ITEM, id, new ChestConversionItem(new Item.Properties().tab(group).stacksTo(16), from, to));
            }
        }
    }
}