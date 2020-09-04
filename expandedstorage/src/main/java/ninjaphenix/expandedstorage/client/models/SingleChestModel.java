package ninjaphenix.expandedstorage.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

public class SingleChestModel extends Model
{
    protected final ModelPart lid;
    protected final ModelPart base;

    public SingleChestModel(final int textureWidth, final int textureHeight)
    {
        super(RenderType::entityCutout);
        this.texWidth = textureWidth;
        this.texHeight = textureHeight;
        lid = new ModelPart(this, 0, 0);
        base = new ModelPart(this, 0, 19);
    }

    public SingleChestModel()
    {
        this(64, 48);
        lid.addBox(0, 0, 0, 14, 5, 14, 0);
        lid.addBox(6, -2, 14, 2, 4, 1, 0);
        lid.setPos(1, 9, 1);
        base.addBox(0, 0, 0, 14, 10, 14, 0);
        base.setPos(1, 0, 1);
    }

    public void setLidPitch(float pitch)
    {
        pitch = 1.0f - pitch;
        lid.xRot = -((1.0F - pitch * pitch * pitch) * 1.5707964F);
    }

    public void render(final PoseStack matrices, final VertexConsumer vertexConsumer, final int i, final int j)
    {
        renderToBuffer(matrices, vertexConsumer, i, j, 1, 1, 1, 1);
    }

    @Override
    public void renderToBuffer(final PoseStack matrices, final VertexConsumer consumer, final int i, final int j, final float r, final float g,
                       final float b, final float f)
    {
        base.render(matrices, consumer, i, j);
        lid.render(matrices, consumer, i, j);
    }
}