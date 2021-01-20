package ninjaphenix.expandedstorage.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.List;
import me.shedaniel.math.Rectangle;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.expandedstorage.client.screen.widget.PageButtonWidget;
import ninjaphenix.expandedstorage.client.screen.widget.ScreenTypeSelectionScreenButton;
import ninjaphenix.expandedstorage.common.inventory.PagedScreenHandler;
import ninjaphenix.expandedstorage.common.inventory.screen.PagedScreenMeta;

public final class PagedScreen extends AbstractScreen<PagedScreenHandler, PagedScreenMeta>
{
    private Image blankArea = null;
    private PageButtonWidget leftPageButton;
    private PageButtonWidget rightPageButton;
    private int page;
    private TranslatableComponent currentPageText;
    private float pageTextX;

    public PagedScreen(final PagedScreenHandler screenHandler, final Inventory playerInventory, final Component title)
    {
        super(screenHandler, playerInventory, title, (screenMeta) -> (screenMeta.WIDTH * 18 + 14) / 2 - 80);
        imageWidth = 14 + 18 * SCREEN_META.WIDTH;
        imageHeight = 17 + 97 + 18 * SCREEN_META.HEIGHT;
    }

    private void setPage(final int oldPage, final int newPage)
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
                    blankArea = new Image(leftPos + xOffset, topPos + imageHeight - 115, blanked * 18, 18, xOffset, imageHeight,
                                          SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
                }
            }
            if (!leftPageButton.active) { leftPageButton.setActive(true); }
        }
        else if (newPage < oldPage)
        {
            if (page == 1) { leftPageButton.setActive(false); }
            if (blankArea != null) {blankArea = null; }
            if (!rightPageButton.active) { rightPageButton.setActive(true); }
        }
        final int slotsPerPage = SCREEN_META.WIDTH * SCREEN_META.HEIGHT;
        final int oldMin = slotsPerPage * (oldPage - 1);
        final int oldMax = Math.min(oldMin + slotsPerPage, SCREEN_META.TOTAL_SLOTS);
        menu.moveSlotRange(oldMin, oldMax, -2000);
        final int newMin = slotsPerPage * (newPage - 1);
        final int newMax = Math.min(newMin + slotsPerPage, SCREEN_META.TOTAL_SLOTS);
        menu.moveSlotRange(newMin, newMax, 2000);
        setPageText();
    }

    private void setPageText() { currentPageText = new TranslatableComponent("screen.expandedstorage.page_x_y", page, SCREEN_META.PAGES); }

    @Override
    public void render(final PoseStack matrices, final int mouseX, final int mouseY, final float delta)
    {
        if (matrices == null) { return; } // Not sure why this can be null, but don't render in case it is.
        super.render(matrices, mouseX, mouseY, delta);
        if (SCREEN_META.PAGES != 1)
        {
            leftPageButton.renderTooltip(matrices, mouseX, mouseY);
            rightPageButton.renderTooltip(matrices, mouseX, mouseY);
        }
    }

    @Override
    protected void init()
    {
        final FabricLoader instance = FabricLoader.getInstance();
        final boolean inventoryProfilesLoaded = instance.isModLoaded("inventoryprofiles");
        final boolean inventorySorterLoaded = instance.isModLoaded("inventorysorter");
        super.init();
        addButton(new ScreenTypeSelectionScreenButton(leftPos + imageWidth + 4, topPos, this::renderButtonTooltip));
        if (SCREEN_META.PAGES != 1)
        {
            final int pageButtonsXOffset;
            if (inventoryProfilesLoaded) { pageButtonsXOffset = -12; }
            else if (inventorySorterLoaded) { pageButtonsXOffset = -18; }
            else { pageButtonsXOffset = 0; }
            page = 1;
            setPageText();
            leftPageButton = new PageButtonWidget(leftPos + imageWidth - 61 + pageButtonsXOffset, topPos + imageHeight - 96, 0,
                                                  new TranslatableComponent("screen.expandedstorage.prev_page"), button -> setPage(page, page - 1),
                                                  this::renderButtonTooltip);
            leftPageButton.active = false;
            addButton(leftPageButton);
            rightPageButton = new PageButtonWidget(leftPos + imageWidth - 19 + pageButtonsXOffset, topPos + imageHeight - 96, 1,
                                                   new TranslatableComponent("screen.expandedstorage.next_page"), button -> setPage(page, page + 1),
                                                   this::renderButtonTooltip);
            addButton(rightPageButton);
            pageTextX = (1 + leftPageButton.x + rightPageButton.x - rightPageButton.getWidth() / 2F) / 2F;
        }
    }

    @Override
    protected void renderBg(final PoseStack matrices, final float delta, final int mouseX, final int mouseY)
    {
        super.renderBg(matrices, delta, mouseX, mouseY);
        if (blankArea != null) { blankArea.render(matrices); }
    }

    @Override
    public void resize(final Minecraft client, final int width, final int height)
    {
        if (SCREEN_META.PAGES != 1)
        {
            final int currentPage = page;
            if (currentPage != 1)
            {
                menu.resetSlotPositions(false);
                super.resize(client, width, height);
                setPage(1, currentPage);
                return;
            }
        }
        super.resize(client, width, height);
    }

    @Override
    protected void renderLabels(final PoseStack matrices, final int mouseX, final int mouseY)
    {
        super.renderLabels(matrices, mouseX, mouseY);
        if (currentPageText != null)
        {
            font.draw(matrices, currentPageText.getVisualOrderText(), pageTextX - leftPos, imageHeight - 94,
                      0x404040);
        }
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers)
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

    @Override
    public List<Rectangle> getReiRectangles()
    {
        return Collections.singletonList(new Rectangle(leftPos + imageWidth + 4, topPos, 22, 22));
    }
}