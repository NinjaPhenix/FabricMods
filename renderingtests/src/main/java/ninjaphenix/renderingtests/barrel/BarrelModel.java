package ninjaphenix.renderingtests.barrel;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class BarrelModel implements UnbakedModel, BakedModel, FabricBakedModel
{
    @Override
    public boolean isVanillaAdapter() { return false; }

    @Override
    public void emitBlockQuads(final BlockAndTintGetter blockAndTintGetter, final BlockState state, final BlockPos pos,
                               final Supplier<Random> random, final RenderContext context)
    {

    }

    @Override
    public void emitItemQuads(final ItemStack stack, final Supplier<Random> random, final RenderContext context)
    {

    }

    @Override
    public List<BakedQuad> getQuads(@Nullable final BlockState state, @Nullable final Direction direction, final Random random)
    {
        return null;
    }

    @Override
    public boolean useAmbientOcclusion() { return false; }

    @Override
    public boolean isGui3d() { return false; }

    @Override
    public boolean usesBlockLight() { return false; }

    @Override
    public boolean isCustomRenderer() { return false; }

    @Override
    public TextureAtlasSprite getParticleIcon() { return null; }

    @Override
    public ItemTransforms getTransforms() { return null; }

    @Override
    public ItemOverrides getOverrides() { return null; }

    @Override
    public Collection<ResourceLocation> getDependencies() { return null; }

    @Override
    public Collection<Material> getMaterials(final Function<ResourceLocation, UnbakedModel> function, final Set<Pair<String, String>> set)
    {
        return null;
    }

    @Nullable
    @Override
    public BakedModel bake(final ModelBakery bakery, final Function<Material, TextureAtlasSprite> function,
                                     final ModelState state, final ResourceLocation resourceLocation)
    {
        return null;
    }
}
