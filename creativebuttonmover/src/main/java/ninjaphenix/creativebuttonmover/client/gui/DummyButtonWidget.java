package ninjaphenix.creativebuttonmover.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import ninjaphenix.creativebuttonmover.client.Config;

public class DummyButtonWidget extends AbstractPressableButtonWidget
{
    private final int xOffset;
    private final int yOffset;
    private Identifier texture;
    private Config.Button button;

    public DummyButtonWidget(Config.Button button, int xOffset, int yOffset)
    {
        super(xOffset + button.x, yOffset + button.y, button.width, button.height, new LiteralText(""));
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.texture = button.texture;
        this.button = button;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta)
    {
        final MinecraftClient client = MinecraftClient.getInstance();
        client.getTextureManager().bindTexture(this.texture);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        final int i = this.getYImage(this.isFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        DrawableHelper.drawTexture(matrixStack, x, y, 0, height * i, width, height, width, 3 * height);
    }

    public void update(Config.Button newValue)
    {
        this.button = newValue;
        x = newValue.x + xOffset;
        y = newValue.y + yOffset;
        width = newValue.width;
        height = newValue.height;
        texture = newValue.texture;
    }

    public void save()
    {
        button.x = x - xOffset;
        button.y = y - yOffset;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (active && visible)
        {
            if (keyCode == 257 || keyCode == 32 || keyCode == 335)
            {
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                onPress();
                return true;
            }
            else if (keyCode == 263)
            {
                x--;
                return true;
            }
            else if (keyCode == 262)
            {
                x++;
                return true;
            }
            else if (keyCode == 265)
            {
                y--;
                return true;
            }
            else if (keyCode == 264)
            {
                y++;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY)
    {
        x = (int) (mouseX - (width / 2));
        y = (int) (mouseY - (height / 2));
    }

    @Override
    public void onPress() { this.setFocused(true); }
}
