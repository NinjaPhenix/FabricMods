package ninjaphenix.containerlib.impl.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import ninjaphenix.containerlib.api.Constants;
import ninjaphenix.containerlib.api.screen.PagedScreenMeta;
import ninjaphenix.containerlib.api.client.screen.AbstractScreen;
import ninjaphenix.containerlib.api.client.screen.widget.ScreenTypeSelectionScreenButton;
import ninjaphenix.containerlib.impl.inventory.PagedScreenHandler;

public class PagedScreen<T extends PagedScreenHandler> extends AbstractScreen<T, PagedScreenMeta>
{
    private Rectangle blankArea = null;
    private PageButtonWidget leftPageButton;
    private PageButtonWidget rightPageButton;
    private int page;
    private TranslatableText currentPageText;

    public PagedScreen(T container)
    {
        super(container, (screenMeta) -> (screenMeta.WIDTH * 18 + 14) / 2 - 80);
        backgroundWidth = 14 + 18 * SCREEN_META.WIDTH;
        backgroundHeight = 17 + 97 + 18 * SCREEN_META.HEIGHT;
    }

    private void setPage(int oldPage, int newPage)
    {
        page = newPage;
        if (newPage > oldPage)
        {
            if (page == SCREEN_META.PAGES)
            {
                rightPageButton.setActive(false);
                final int blanked = SCREEN_META.BLANK_SLOTS;
                if (blanked > 0)
                {
                    final int xOffset = 7 + (SCREEN_META.WIDTH - blanked) * 18;
                    blankArea = new Rectangle(x + xOffset, y + backgroundHeight - 115, blanked * 18, 18,
                            xOffset, backgroundHeight, SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
                }
            }
            if (!leftPageButton.active) { leftPageButton.setActive(true); }
        }
        else
        {
            if (page == 1) { leftPageButton.setActive(false); }
            if (blankArea != null) {blankArea = null; }
            if (!rightPageButton.active) { rightPageButton.setActive(true); }
        }
        final int slotsPerPage = SCREEN_META.WIDTH * SCREEN_META.HEIGHT;
        int oldMin = slotsPerPage * (oldPage - 1);
        int oldMax = Math.min(oldMin + slotsPerPage, SCREEN_META.TOTAL_SLOTS);
        handler.moveSlotRange(oldMin, oldMax, -2000);
        int newMin = slotsPerPage * (newPage - 1);
        int newMax = Math.min(newMin + slotsPerPage, SCREEN_META.TOTAL_SLOTS);
        handler.moveSlotRange(newMin, newMax, 2000);
        setPageText();
    }

    private void setPageText() { currentPageText = new TranslatableText("screen.ninjaphenix-container-lib.page_x_y", page, SCREEN_META.PAGES); }

    @Override
    protected void init()
    {
        super.init();
        addButton(new ScreenTypeSelectionScreenButton(x + backgroundWidth - 19, y + 4));
        if (SCREEN_META.PAGES != 1)
        {
            page = 1;
            setPageText();
            leftPageButton = new PageButtonWidget(x + backgroundWidth - 61, y + backgroundHeight - 96, 0, button -> setPage(page, page - 1));
            leftPageButton.active = false;
            addButton(leftPageButton);
            rightPageButton = new PageButtonWidget(x + backgroundWidth - 19, y + backgroundHeight - 96, 1, button -> setPage(page, page + 1));
            addButton(rightPageButton);
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
        super.drawBackground(matrices, delta, mouseX, mouseY);
        if (blankArea != null) { blankArea.render(matrices); }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height)
    {
        if (SCREEN_META.PAGES != 1)
        {
            int currentPage = page;
            if (currentPage != 1)
            {
                handler.resetSlotPositions(null);
                super.resize(client, width, height);
                setPage(1, currentPage);
                return;
            }
        }
        super.resize(client, width, height);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
    {
        super.drawForeground(matrices, mouseX, mouseY);
        if (currentPageText != null) { textRenderer.draw(matrices, currentPageText.getString(), backgroundWidth - 42, backgroundHeight - 94, 4210752); }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 262 || keyCode == 267) // Right Arrow, Page Down
        {
            if (SCREEN_META.PAGES != 1)
            {
                if (hasShiftDown()) { setPage(page, SCREEN_META.PAGES); }
                else { if (page != SCREEN_META.PAGES) { setPage(page, page + 1); } }
                return true;
            }
        }
        else if (keyCode == 263 || keyCode == 266) // Left Arrow, Page Up
        {
            if (SCREEN_META.PAGES != 1)
            {
                if (hasShiftDown()) { setPage(page, 1); }
                else { if (page != 1) { setPage(page, page - 1); } }
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private static class PageButtonWidget extends ButtonWidget
    {
        private final int TEXTURE_OFFSET;
        private static final Identifier TEXTURE = Constants.id("textures/gui/page_buttons.png");

        public PageButtonWidget(int x, int y, int textureOffset, PressAction onPress)
        {
            super(x, y, 12, 12, new LiteralText(""), onPress);
            TEXTURE_OFFSET = textureOffset;
        }

        public void setActive(final boolean active)
        {
            this.active = active;
            if (!active)
            {
                this.setFocused(false);
            }
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            minecraftClient.getTextureManager().bindTexture(TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            drawTexture(matrices, x, y, TEXTURE_OFFSET * 12, getYImage(isHovered()) * 12, width, height, 32, 48);
        }
    }
}



