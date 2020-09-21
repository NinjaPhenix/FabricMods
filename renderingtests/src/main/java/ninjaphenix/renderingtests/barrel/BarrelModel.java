package ninjaphenix.renderingtests.barrel;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.renderingtests.Main;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class BarrelModel implements UnbakedModel, BakedModel, FabricBakedModel
{
    Mesh mesh;

    public BarrelModel()
    {

    }

    @Override
    public boolean isVanillaAdapter() { return false; }

    @Override
    public void emitBlockQuads(final BlockAndTintGetter blockAndTintGetter, final BlockState state, final BlockPos pos,
                               final Supplier<Random> random, final RenderContext context)
    {
        context.meshConsumer().accept(mesh);
    }

    @Override
    public void emitItemQuads(final ItemStack stack, final Supplier<Random> random, final RenderContext context)
    {

    }

    @Override
    public List<BakedQuad> getQuads(@Nullable final BlockState state, @Nullable final Direction direction, final Random random)
    {
        return Collections.emptyList(); // Vanilla method, not used by fabric rendering.
    }

    @Override
    public boolean useAmbientOcclusion() { return false; }

    @Override
    public boolean isGui3d() { return true; }

    @Override
    public boolean usesBlockLight() { return true; }

    @Override
    public boolean isCustomRenderer() { return true; }

    @Override
    public TextureAtlasSprite getParticleIcon() { return Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(Main.resloc("block/diamond_barrel_strip")); }

    @Override
    public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }

    @Override
    public ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }

    @Override
    public Collection<ResourceLocation> getDependencies() { return Collections.emptyList(); }

    @Override
    public Collection<Material> getMaterials(final Function<ResourceLocation, UnbakedModel> function, final Set<Pair<String, String>> set)
    {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public BakedModel bake(final ModelBakery bakery, final Function<Material, TextureAtlasSprite> function,
                           final ModelState state, final ResourceLocation resourceLocation)
    {
        MeshBuilder builder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        builder.getEmitter().square(Direction.NORTH, 0, 0, 1, 1, 0).emit()
                .square(Direction.EAST, 0, 0, 1, 1, 0).emit()
                .square(Direction.SOUTH, 0, 0, 1, 1, 0).emit()
                .square(Direction.WEST, 0, 0, 1, 1, 0).emit()
                .square(Direction.UP, 0, 0, 1, 1, 0).emit()
                .square(Direction.DOWN, 0, 0, 1, 1, 0).emit();
        mesh = builder.build();
        return this;
    }
}