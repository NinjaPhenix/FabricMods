package ninjaphenix.expandedstorage.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.expandedstorage.client.ExpandedStorageClient;
import ninjaphenix.expandedstorage.client.screen.widget.ScreenTypeSelectionScreenButton;
import ninjaphenix.expandedstorage.common.inventory.ScrollableScreenHandler;
import ninjaphenix.expandedstorage.common.inventory.screen.ScrollableScreenMeta;

public final class ScrollableScreen extends AbstractScreen<ScrollableScreenHandler, ScrollableScreenMeta>
{
    protected final boolean hasScrollbar;
    private final int renderBackgroundWidth;
    private Rectangle blankArea = null;
    private boolean isDragging;
    private int topRow;

    public ScrollableScreen(final ScrollableScreenHandler container, final Inventory playerInventory, final Component title)
    {
        super(container, playerInventory, title, (screenMeta) -> (screenMeta.WIDTH * 18 + 14) / 2 - 80);
        renderBackgroundWidth = 14 + 18 * SCREEN_META.WIDTH;
        hasScrollbar = SCREEN_META.TOTAL_ROWS != SCREEN_META.HEIGHT;
        imageWidth = renderBackgroundWidth + (hasScrollbar ? 18 : 0);
        imageHeight = 17 + 97 + 18 * SCREEN_META.HEIGHT;
    }

    @Override
    protected void init()
    {
        super.init();
        if (!hasScrollbar)
        {
            final int blanked = SCREEN_META.BLANK_SLOTS;
            if (blanked > 0)
            {
                final int xOffset = 7 + (SCREEN_META.WIDTH - blanked) * 18;
                blankArea = new Rectangle(leftPos + xOffset, topPos + imageHeight - 115, blanked * 18, 18, xOffset, imageHeight, SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
            }
        }
        else
        {
            isDragging = false;
            topRow = 0;
        }
        addButton(new ScreenTypeSelectionScreenButton(leftPos + imageWidth + 4, topPos, this::renderButtonTooltip));
    }

    @Override
    @SuppressWarnings({"deprecation", "ConstantConditions"})
    protected void renderBg(final PoseStack matrices, final float delta, final int mouseX, final int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(SCREEN_META.TEXTURE);
        blit(matrices, leftPos, topPos, 0, 0, renderBackgroundWidth, imageHeight, SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
        if (hasScrollbar)
        {
            final int slotsHeight = SCREEN_META.HEIGHT * 18;
            final int scrollbarHeight = slotsHeight + (SCREEN_META.WIDTH > 9 ? 34 : 24);
            blit(matrices, leftPos + renderBackgroundWidth - 4, topPos, renderBackgroundWidth, 0, 22, scrollbarHeight,
                        SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
            final int yOffset = Mth.floor((slotsHeight - 17) * (((double) topRow) / (SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT)));
            blit(matrices, leftPos + renderBackgroundWidth - 2, topPos + yOffset + 18, renderBackgroundWidth, scrollbarHeight, 12, 15,
                        SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
        }
        if (blankArea != null) { blankArea.render(matrices); }
    }

    private boolean isMouseOverScrollbar(final double mouseX, final double mouseY)
    {
        final int top = topPos + 18;
        final int left = leftPos + renderBackgroundWidth - 2;
        return mouseX >= left && mouseY >= top && mouseX < left + 12 && mouseY < top + SCREEN_META.HEIGHT * 18;
    }

    @Override
    protected boolean hasClickedOutside(final double mouseX, final double mouseY, final int left, final int top, final int button)
    {
        return super.hasClickedOutside(mouseX, mouseY, left, top, button) && !isMouseOverScrollbar(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers)
    {
        if (hasScrollbar)
        {
            if (keyCode == 264 || keyCode == 267) // Down Arrow, Page Down
            {
                if (topRow != SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT)
                {
                    if (hasShiftDown())
                    {
                        setTopRow(topRow, Math.min(topRow + SCREEN_META.HEIGHT, SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT));
                    }
                    else { setTopRow(topRow, topRow + 1); }
                }
                return true;
            }
            else if (keyCode == 265 || keyCode == 266) // Up Arrow, Page Up
            {
                if (topRow != 0)
                {
                    if (hasShiftDown()) { setTopRow(topRow, Math.max(topRow - SCREEN_META.HEIGHT, 0)); }
                    else {setTopRow(topRow, topRow - 1);}
                }
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button)
    {
        if (hasScrollbar && isMouseOverScrollbar(mouseX, mouseY) && button == 0)
        {
            isDragging = true;
            updateTopRow(mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY)
    {
        if (hasScrollbar && isDragging)
        {
            updateTopRow(mouseY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void updateTopRow(final double mouseY)
    {
        setTopRow(topRow, Mth.floor(Mth.clampedLerp(0, SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT, (mouseY - (topPos + 18)) / (SCREEN_META.HEIGHT * 18))));
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double delta)
    {
        if (hasScrollbar && (!ExpandedStorageClient.CONFIG.restrictive_scrolling || isMouseOverScrollbar(mouseX, mouseY)))
        {
            final int newTop;
            if (delta < 0)
            {
                newTop = Math.min(topRow + (hasShiftDown() ? SCREEN_META.HEIGHT : 1), SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT);
            }
            else { newTop = Math.max(topRow - (hasShiftDown() ? SCREEN_META.HEIGHT : 1), 0); }
            setTopRow(topRow, newTop);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void setTopRow(final int oldTopRow, final int newTopRow)
    {
        if (oldTopRow == newTopRow) { return; }
        topRow = newTopRow;
        final int delta = newTopRow - oldTopRow;
        final int rows = Math.abs(delta);
        if (rows < SCREEN_META.HEIGHT)
        {
            final int setAmount = rows * SCREEN_META.WIDTH;
            final int movableAmount = (SCREEN_META.HEIGHT - rows) * SCREEN_META.WIDTH;
            if (delta > 0)
            {
                final int setOutBegin = oldTopRow * SCREEN_META.WIDTH;
                final int movableBegin = newTopRow * SCREEN_META.WIDTH;
                final int setInBegin = movableBegin + movableAmount;
                menu.setSlotRange(setOutBegin, setOutBegin + setAmount, index -> -2000);
                menu.moveSlotRange(movableBegin, setInBegin, -18 * rows);
                menu.setSlotRange(setInBegin, Math.min(setInBegin + setAmount, SCREEN_META.TOTAL_SLOTS),
                                     index -> 18 * Mth.intFloorDiv(index - movableBegin + SCREEN_META.WIDTH, SCREEN_META.WIDTH));
            }
            else
            {
                final int setInBegin = newTopRow * SCREEN_META.WIDTH;
                final int movableBegin = oldTopRow * SCREEN_META.WIDTH;
                final int setOutBegin = movableBegin + movableAmount;
                menu.setSlotRange(setInBegin, setInBegin + setAmount,
                                     index -> 18 * Mth.intFloorDiv(index - setInBegin + SCREEN_META.WIDTH, SCREEN_META.WIDTH));
                menu.moveSlotRange(movableBegin, setOutBegin, 18 * rows);
                menu.setSlotRange(setOutBegin, Math.min(setOutBegin + setAmount, SCREEN_META.TOTAL_SLOTS), index -> -2000);
            }
        }
        else
        {
            final int oldMin = oldTopRow * SCREEN_META.WIDTH;
            menu.setSlotRange(oldMin, Math.min(oldMin + SCREEN_META.WIDTH * SCREEN_META.HEIGHT, SCREEN_META.TOTAL_SLOTS), index -> -2000);
            final int newMin = newTopRow * SCREEN_META.WIDTH;
            menu.setSlotRange(newMin, newMin + SCREEN_META.WIDTH * SCREEN_META.HEIGHT,
                                 index -> 18 + 18 * Mth.intFloorDiv(index - newMin, SCREEN_META.WIDTH));
        }

        if (newTopRow == SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT)
        {
            final int blanked = SCREEN_META.BLANK_SLOTS;
            if (blanked > 0)
            {
                final int xOffset = 7 + (SCREEN_META.WIDTH - blanked) * 18;
                blankArea = new Rectangle(leftPos + xOffset, topPos + imageHeight - 115, blanked * 18, 18, xOffset, imageHeight,
                                          SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
            }
        }
        else { blankArea = null; }
    }

    @Override
    public void resize(final Minecraft client, final int width, final int height)
    {
        if (hasScrollbar)
        {
            final int row = topRow;
            super.resize(client, width, height);
            setTopRow(topRow, row);
        }
        else { super.resize(client, width, height); }
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button)
    {
        if (hasScrollbar && isDragging)
        {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}