package ninjaphenix.chainmail.api.client.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

/**
 * @since 0.0.2
 */
@FunctionalInterface
public interface ItemStackRenderFunction
{
    void render(ItemStack stack, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, int light, int overlay);
}
