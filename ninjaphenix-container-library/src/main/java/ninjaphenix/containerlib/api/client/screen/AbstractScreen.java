package ninjaphenix.containerlib.api.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import ninjaphenix.containerlib.api.screen.ScreenMeta;
import ninjaphenix.containerlib.api.inventory.AbstractContainer;
import ninjaphenix.containerlib.impl.client.ContainerLibraryClient;

import java.util.function.Function;

public abstract class AbstractScreen<T extends AbstractContainer<R>, R extends ScreenMeta> extends HandledScreen<T>
{
    protected final R SCREEN_META;
    private final Integer INVENTORY_LABEL_LEFT;

    protected AbstractScreen(T container, Function<R, Integer> inventoryLabelLeftFunction)
    {
        super(container, container.PLAYER_INVENTORY, container.getDisplayName());
        SCREEN_META = container.SCREEN_META;
        INVENTORY_LABEL_LEFT = inventoryLabelLeftFunction.apply(SCREEN_META);
    }

    @Override
    protected void drawBackground(final MatrixStack matrices, final float delta, final int mouseX, final int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //noinspection ConstantConditions
        client.getTextureManager().bindTexture(SCREEN_META.TEXTURE);
        final int left = (width - backgroundWidth) / 2;
        final int top = (height - backgroundHeight) / 2;
        drawTexture(matrices, left, top, 0, 0, backgroundWidth, backgroundHeight, SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
    }

    @Override
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta)
    {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(final MatrixStack matrices, final int mouseX, final int mouseY)
    {
        textRenderer.draw(matrices, title, 8, 6, 4210752);
        textRenderer.draw(matrices, playerInventory.getDisplayName(), INVENTORY_LABEL_LEFT, this.backgroundHeight - 96 + 2, 4210752);
    }

    protected static class Rectangle
    {
        public final int X;
        public final int Y;
        public final int WIDTH;
        public final int HEIGHT;
        public final int TEXTURE_X;
        public final int TEXTURE_Y;
        public final int TEXTURE_WIDTH;
        public final int TEXTURE_HEIGHT;

        public Rectangle(final int x, final int y, final int width, final int height,
                final int textureX, final int textureY, final int textureWidth, final int textureHeight)
        {
            X = x; Y = y; WIDTH = width; HEIGHT = height;
            TEXTURE_X = textureX; TEXTURE_Y = textureY; TEXTURE_WIDTH = textureWidth; TEXTURE_HEIGHT = textureHeight;
        }

        public void render(final MatrixStack matrices) { drawTexture(matrices, X, Y, TEXTURE_X, TEXTURE_Y, WIDTH, HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT); }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256 || client.options.keyInventory.matchesKey(keyCode, scanCode)) {
            ContainerLibraryClient.sendCallbackRemoveToServer();
            client.player.closeHandledScreen();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

}
