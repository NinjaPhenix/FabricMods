package ninjaphenix.expandedstorage.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Function;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.expandedstorage.client.ExpandedStorageClient;
import ninjaphenix.expandedstorage.common.inventory.AbstractScreenHandler;
import ninjaphenix.expandedstorage.common.inventory.screen.ScreenMeta;

public abstract class AbstractScreen<T extends AbstractScreenHandler<R>, R extends ScreenMeta> extends AbstractContainerScreen<T>
{
    protected final R SCREEN_META;
    private final Integer INVENTORY_LABEL_LEFT;

    protected AbstractScreen(final T container, final Inventory playerInventory, final Component title,
                             final Function<R, Integer> inventoryLabelLeftFunction)
    {
        super(container, playerInventory, title);
        SCREEN_META = container.SCREEN_META;
        INVENTORY_LABEL_LEFT = inventoryLabelLeftFunction.apply(SCREEN_META);
    }

    @Override
    @SuppressWarnings({"ConstantConditions", "deprecation"})
    protected void renderBg(final PoseStack matrices, final float delta, final int mouseX, final int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(SCREEN_META.TEXTURE);
        blit(matrices, leftPos, topPos, 0, 0, imageWidth, imageHeight, SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
    }

    @Override
    public void render(final PoseStack matrices, final int mouseX, final int mouseY, final float delta)
    {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        renderTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(final PoseStack matrices, final int mouseX, final int mouseY)
    {
        font.draw(matrices, title, 8, 6, 4210752);
        font.draw(matrices, inventory.getDisplayName(), INVENTORY_LABEL_LEFT, imageHeight - 96 + 2, 4210752);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers)
    {
        if (keyCode == 256 || minecraft.options.keyInventory.matches(keyCode, scanCode))
        {
            ExpandedStorageClient.sendCallbackRemoveToServer();
            minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected static class Rectangle
    {
        public final int X, Y, WIDTH, HEIGHT, TEXTURE_X, TEXTURE_Y, TEXTURE_WIDTH, TEXTURE_HEIGHT;

        public Rectangle(final int x, final int y, final int width, final int height, final int textureX, final int textureY,
                         final int textureWidth, final int textureHeight)
        {
            X = x;
            Y = y;
            WIDTH = width;
            HEIGHT = height;
            TEXTURE_X = textureX;
            TEXTURE_Y = textureY;
            TEXTURE_WIDTH = textureWidth;
            TEXTURE_HEIGHT = textureHeight;
        }

        public void render(final PoseStack matrices)
        {
            blit(matrices, X, Y, TEXTURE_X, TEXTURE_Y, WIDTH, HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
    }

    protected final void renderButtonTooltip(final AbstractButton widget, final PoseStack stack, final int x, final int y)
    {
        renderTooltip(stack, widget.getMessage(), x, y);
    }
}