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
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
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

import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public class ModBlocks
{
    public static final ModBlocks INSTANCE = new ModBlocks();

    public void initialize()
    {
        Map<Identifier, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
        tiers.forEach((tierId, tier) ->
        {
            if (!tierId.getNamespace().equals(Torcherino.MOD_ID)) { return; }
            final Identifier torcherinoId = id(tierId, "torcherino");
            final Identifier jackoLanterinoId = id(tierId, "lanterino");
            final Identifier lanterinoId = id(tierId, "lantern");
            ParticleEffect particleEffect = (DefaultParticleType) Registry.PARTICLE_TYPE.get(id(tierId, "flame"));
            TorcherinoBlock torcherinoBlock = new TorcherinoBlock(tierId, particleEffect);
            registerAndBlacklist(torcherinoId, torcherinoBlock);
            WallTorcherinoBlock torcherinoWallBlock = new WallTorcherinoBlock(tierId, torcherinoBlock, particleEffect);
            registerAndBlacklist(new Identifier(torcherinoId.getNamespace(), "wall_" + torcherinoId.getPath()), torcherinoWallBlock);
            JackoLanterinoBlock jackoLanterinoBlock = new JackoLanterinoBlock(tierId);
            registerAndBlacklist(jackoLanterinoId, jackoLanterinoBlock);
            LanterinoBlock lanterinoBlock = new LanterinoBlock(tierId);
            registerAndBlacklist(lanterinoId, lanterinoBlock);
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            {
                SetRenderLayer(torcherinoBlock);
                SetRenderLayer(torcherinoWallBlock);
                SetRenderLayer(lanterinoBlock);
            }
            WallStandingBlockItem torcherinoItem = new WallStandingBlockItem(torcherinoBlock, torcherinoWallBlock,
                    new Item.Settings().group(ItemGroup.DECORATIONS));
            Registry.register(Registry.ITEM, torcherinoId, torcherinoItem);
            BlockItem jackoLanterinoItem = new BlockItem(jackoLanterinoBlock, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS));
            Registry.register(Registry.ITEM, jackoLanterinoId, jackoLanterinoItem);
            BlockItem lanterinoItem = new BlockItem(lanterinoBlock, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS));
            Registry.register(Registry.ITEM, lanterinoId, lanterinoItem);
        });
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Torcherino.MOD_ID, "torcherino"),
                new TocherinoBlockEntityType(TorcherinoBlockEntity::new, null));
    }

    @Environment(EnvType.CLIENT)
    private void SetRenderLayer(Block block) { BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout()); }

    private void registerAndBlacklist(Identifier id, Block block)
    {
        Registry.register(Registry.BLOCK, id, block);
        TorcherinoAPI.INSTANCE.blacklistBlock(id);
    }

    private Identifier id(Identifier tierID, String type)
    {
        if (tierID.getPath().equals("normal")) { return new Identifier(Torcherino.MOD_ID, type); }
        return new Identifier(Torcherino.MOD_ID, tierID.getPath() + '_' + type);
    }
}
