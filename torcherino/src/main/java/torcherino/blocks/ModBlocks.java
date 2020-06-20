package torcherino.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.api.blocks.JackoLanterinoBlock;
import torcherino.api.blocks.LanterinoBlock;
import torcherino.api.blocks.TorcherinoBlock;
import torcherino.api.blocks.WallTorcherinoBlock;
import torcherino.api.blocks.entity.TocherinoBlockEntityType;
import torcherino.api.blocks.entity.TorcherinoBlockEntity;

import java.util.HashMap;

@SuppressWarnings("SpellCheckingInspection")
public class ModBlocks
{
    public static final ModBlocks INSTANCE = new ModBlocks();
    private HashMap<Identifier, Block> blocks;
    private HashMap<Identifier, Item> items;

    public void initialize()
    {
        blocks = new HashMap<>();
        items = new HashMap<>();
        TorcherinoAPI.INSTANCE.getTiers().forEach(this::createBlocks);
        registerBlocks();
        registerItems();
        registerBlockEntity();
    }

    private void createBlocks(Identifier tierID, Tier tier)
    {
        if (tierID.getNamespace().equals(Torcherino.MOD_ID))
        {
            Identifier torcherinoID = getIdentifier(tierID, "torcherino");
            Identifier jackoLanterinoID = getIdentifier(tierID, "lanterino");
            Identifier lanterinoID = getIdentifier(tierID, "lantern");
            Block torcherinoBlock = new TorcherinoBlock(tierID);
            Block torcherinoWallBlock = new WallTorcherinoBlock(tierID, new Identifier(Torcherino.MOD_ID, "blocks/" + torcherinoID.getPath()));
            Block jackoLanterinoBlock = new JackoLanterinoBlock(tierID);
            Block lanterinoBlock = new LanterinoBlock(tierID);
            blocks.put(torcherinoID, torcherinoBlock);
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            {
                SetRenderLayer(torcherinoBlock);
                SetRenderLayer(torcherinoWallBlock);
                SetRenderLayer(lanterinoBlock);
            }
            blocks.put(new Identifier(Torcherino.MOD_ID, "wall_" + torcherinoID.getPath()), torcherinoWallBlock);
            blocks.put(jackoLanterinoID, jackoLanterinoBlock);
            blocks.put(lanterinoID, lanterinoBlock);
            Item torcherinoItem = new WallStandingBlockItem(torcherinoBlock, torcherinoWallBlock, new Item.Settings().group(ItemGroup.DECORATIONS));
            Item jackoLanterinoItem = new BlockItem(jackoLanterinoBlock, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS));
            Item lanterinoItem = new BlockItem(lanterinoBlock, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS));
            items.put(torcherinoID, torcherinoItem);
            items.put(jackoLanterinoID, jackoLanterinoItem);
            items.put(lanterinoID, lanterinoItem);
        }
    }

    @Environment(EnvType.CLIENT)
    private void SetRenderLayer(Block block) { BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout()); }

    private void registerBlocks()
    {
        blocks.forEach((id, block) ->
        {
            Registry.register(Registry.BLOCK, id, block);
            TorcherinoAPI.INSTANCE.blacklistBlock(id);
        });
    }

    private void registerItems() { items.forEach((id, item) -> Registry.register(Registry.ITEM, id, item)); }

    private void registerBlockEntity()
    {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Torcherino.MOD_ID, "torcherino"),
                new TocherinoBlockEntityType(TorcherinoBlockEntity::new, null));
    }

    private Identifier getIdentifier(Identifier tierID, String type)
    {
        if (tierID.getPath().equals("normal")) { return new Identifier(Torcherino.MOD_ID, type); }
        return new Identifier(Torcherino.MOD_ID, tierID.getPath() + '_' + type);
    }
}
