package ninjaphenix.creativebuttonmover.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.impl.item.group.CreativeGuiExtensions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class PageSwitchWidget extends ButtonWidget
{
    private final Identifier texture;
    private final CreativeInventoryScreen screen;

    public PageSwitchWidget(int x, int y, int width, int height, Identifier texture, PressAction onPress)
    {
        super(x, y, width, height, new LiteralText(""), onPress);
        screen = (CreativeInventoryScreen) MinecraftClient.getInstance().currentScreen;
        this.texture = texture;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta)
    {
        final MinecraftClient client = MinecraftClient.getInstance();
        client.getTextureManager().bindTexture(texture);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        setZOffset(200);
        DrawableHelper.drawTexture(matrixStack, x, y, 0, height * i, width, height, width, 3 * height);
        setZOffset(0);
        if (isHovered() && active) { screen.renderTooltip(matrixStack, getNarrationMessage(), x, y); }
    }

    @Override
    public boolean changeFocus(boolean bl)
    {
        if (this.visible)
        {
            this.setFocused(!this.isFocused());
            this.onFocusedChanged(this.isFocused());
            return this.isFocused() && active;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void onPress()
    {
        super.onPress();
        nextNarration = 0;
        narrate();
    }

    @Override
    protected MutableText getNarrationMessage()
    {
        return new TranslatableText("creativebuttonmover.gui.creativeTabPage", ((CreativeGuiExtensions) screen).fabric_currentPage() + 1,
                (ItemGroup.GROUPS.length - 12) / 9 + 2);
    }

    public void setFocusedExternal(boolean focused)
    {
        this.setFocused(focused);
    }
}
