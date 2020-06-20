package ninjaphenix.containerlib.api.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import ninjaphenix.containerlib.api.screen.ScreenMeta;
import ninjaphenix.containerlib.api.inventory.AbstractContainer;
import ninjaphenix.containerlib.impl.client.ContainerLibraryClient;

import java.util.function.Function;

public abstract class AbstractScreen<T extends AbstractContainer<R>, R extends ScreenMeta> extends ContainerScreen<T>
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
    protected void drawBackground(final float delta, final int mouseX, final int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //noinspection ConstantConditions
        minecraft.getTextureManager().bindTexture(SCREEN_META.TEXTURE);
        final int left = (width - containerWidth) / 2;
        final int top = (height - containerHeight) / 2;
        blit(left, top, 0, 0, containerWidth, containerHeight, SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta)
    {
        renderBackground();
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY)
    {
        font.draw(title.asFormattedString(), 8, 6, 4210752);
        font.draw(playerInventory.getDisplayName().asFormattedString(), INVENTORY_LABEL_LEFT, this.containerHeight - 96 + 2, 4210752);
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

        public void render() { blit(X, Y, TEXTURE_X, TEXTURE_Y, WIDTH, HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT); }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256 || this.minecraft.options.keyInventory.matchesKey(keyCode, scanCode)) {
            ContainerLibraryClient.sendCallbackRemoveToServer();
            this.minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

}
