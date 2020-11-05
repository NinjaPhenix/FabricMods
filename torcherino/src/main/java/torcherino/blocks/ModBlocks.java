package torcherino.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
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

public class ModBlocks
{
    public static final ModBlocks INSTANCE = new ModBlocks();

    public void initialize()
    {
        final Map<ResourceLocation, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
        tiers.forEach((tierRl, tier) ->
        {
            if (!tierRl.getNamespace().equals(Torcherino.MOD_ID)) { return; }
            final ResourceLocation torcherinoId = resloc(tierRl, "torcherino");
            final ResourceLocation jackoLanterinoId = resloc(tierRl, "lanterino");
            final ResourceLocation lanterinoId = resloc(tierRl, "lantern");
            final ParticleOptions particleEffect = (SimpleParticleType) Registry.PARTICLE_TYPE.get(resloc(tierRl, "flame"));
            final TorcherinoBlock torcherinoBlock = new TorcherinoBlock(tierRl, particleEffect);
            registerAndBlacklist(torcherinoId, torcherinoBlock);
            final WallTorcherinoBlock torcherinoWallBlock = new WallTorcherinoBlock(tierRl, torcherinoBlock, particleEffect);
            registerAndBlacklist(new ResourceLocation(torcherinoId.getNamespace(), "wall_" + torcherinoId.getPath()), torcherinoWallBlock);
            final JackoLanterinoBlock jackoLanterinoBlock = new JackoLanterinoBlock(tierRl);
            registerAndBlacklist(jackoLanterinoId, jackoLanterinoBlock);
            final LanterinoBlock lanterinoBlock = new LanterinoBlock(tierRl);
            registerAndBlacklist(lanterinoId, lanterinoBlock);
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            {
                SetRenderLayer(torcherinoBlock);
                SetRenderLayer(torcherinoWallBlock);
                SetRenderLayer(lanterinoBlock);
            }
            final StandingAndWallBlockItem torcherinoItem = new StandingAndWallBlockItem(torcherinoBlock, torcherinoWallBlock,
                    new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS));
            Registry.register(Registry.ITEM, torcherinoId, torcherinoItem);
            final BlockItem jackoLanterinoItem = new BlockItem(jackoLanterinoBlock, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));
            Registry.register(Registry.ITEM, jackoLanterinoId, jackoLanterinoItem);
            final BlockItem lanterinoItem = new BlockItem(lanterinoBlock, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));
            Registry.register(Registry.ITEM, lanterinoId, lanterinoItem);
        });
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(Torcherino.MOD_ID, "torcherino"),
                new TocherinoBlockEntityType(TorcherinoBlockEntity::new, null));
    }

    @Environment(EnvType.CLIENT)
    private void SetRenderLayer(final Block block) { BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped()); }

    private void registerAndBlacklist(final ResourceLocation resloc, final Block block)
    {
        Registry.register(Registry.BLOCK, resloc, block);
        TorcherinoAPI.INSTANCE.blacklistBlock(resloc);
    }

    private ResourceLocation resloc(final ResourceLocation tierRl, final String type)
    {
        if (tierRl.getPath().equals("normal")) { return new ResourceLocation(Torcherino.MOD_ID, type); }
        return new ResourceLocation(Torcherino.MOD_ID, tierRl.getPath() + '_' + type);
    }
}
