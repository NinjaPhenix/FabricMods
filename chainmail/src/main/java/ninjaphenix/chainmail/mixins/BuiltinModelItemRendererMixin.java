package ninjaphenix.chainmail.mixins;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import ninjaphenix.chainmail.api.client.render.ItemStackRenderFunction;
import ninjaphenix.chainmail.impl.mixinhelpers.BuiltinModelItemRendererExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.function.Predicate;

@Mixin(BuiltinModelItemRenderer.class)
public abstract class BuiltinModelItemRendererMixin implements BuiltinModelItemRendererExtensions
{
    private final HashMap<Predicate<ItemStack>, ItemStackRenderFunction> chainmail_renderers = new HashMap<>();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void chainmail_render(ItemStack stack, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, int light, int overlay, CallbackInfo ci)
    {
        chainmail_renderers.forEach((predicate, renderer) -> {
            if (predicate.test(stack))
            {
                renderer.render(stack, matrix, vertexConsumerProvider, light, overlay);
                ci.cancel();
                return;
            }
        });
    }

    @Override
    public void chainmail_addRenderer(Predicate<ItemStack> stackPredicate, ItemStackRenderFunction renderFunction)
    {
        if (stackPredicate == null) { throw new NullPointerException("Stack predicate must not be null."); }
        if (renderFunction == null) { throw new NullPointerException("Render Function must not be null."); }
        chainmail_renderers.put(stackPredicate, renderFunction);
    }
}
