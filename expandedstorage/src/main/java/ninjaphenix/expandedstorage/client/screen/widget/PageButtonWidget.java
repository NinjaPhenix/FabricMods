package ninjaphenix.expandedstorage.client.screen.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.common.Const;

public class PageButtonWidget extends Button
{
    private static final ResourceLocation TEXTURE = Const.resloc("textures/gui/page_buttons.png");
    private final int TEXTURE_OFFSET;

    public PageButtonWidget(final int x, final int y, final int textureOffset, final Component text, final OnPress onPress,
                            final OnTooltip onTooltip)
    {
        super(x, y, 12, 12, text, onPress, onTooltip);
        TEXTURE_OFFSET = textureOffset;
    }

    public void setActive(final boolean active)
    {
        this.active = active;
        if (!active) { setFocused(false); }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void renderButton(final PoseStack matrices, final int mouseX, final int mouseY, final float delta)
    {
        final Minecraft minecraftClient = Minecraft.getInstance();
        minecraftClient.getTextureManager().bind(TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blit(matrices, x, y, TEXTURE_OFFSET * 12, getYImage(isHovered()) * 12, width, height, 32, 48);
    }

    public void renderTooltip(final PoseStack matrices, final int mouseX, final int mouseY)
    {
        if (active)
        {
            if (isHovered) { renderToolTip(matrices, mouseX, mouseY); }
            else if (isFocused()) { renderToolTip(matrices, x, y); }
        }
    }
}