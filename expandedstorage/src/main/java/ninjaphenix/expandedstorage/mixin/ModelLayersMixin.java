package ninjaphenix.expandedstorage.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import ninjaphenix.expandedstorage.client.CursedChestBlockEntityRenderer;
import ninjaphenix.expandedstorage.common.Const;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.Map;

@Mixin(LayerDefinitions.class)
public class ModelLayersMixin
{
    @Inject(method = "createRoots()Ljava/util/Map;",
            at = @At(value = "INVOKE:FIRST", target = "Lnet/minecraft/client/model/geom/builders/LayerDefinition;create(Lnet/minecraft/client/model/geom/builders/MeshDefinition;II)Lnet/minecraft/client/model/geom/builders/LayerDefinition;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private static void addLayerDefinitions(final CallbackInfoReturnable<Map<ModelLayerLocation, ModelPart>> cir,
                                            final ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder)
    {
        builder.put(Const.SINGLE_LAYER, CursedChestBlockEntityRenderer.createSingleBodyLayer());
        builder.put(Const.VANILLA_LEFT_LAYER, CursedChestBlockEntityRenderer.createVanillaLeftBodyLayer());
        builder.put(Const.VANILLA_RIGHT_LAYER, CursedChestBlockEntityRenderer.createVanillaRightBodyLayer());
        builder.put(Const.TALL_TOP_LAYER, CursedChestBlockEntityRenderer.createTallTopBodyLayer());
        builder.put(Const.TALL_BOTTOM_LAYER, CursedChestBlockEntityRenderer.createTallBottomBodyLayer());
        builder.put(Const.LONG_FRONT_LAYER, CursedChestBlockEntityRenderer.createLongFrontBodyLayer());
        builder.put(Const.LONG_BACK_LAYER, CursedChestBlockEntityRenderer.createLongBackBodyLayer());
    }
}
