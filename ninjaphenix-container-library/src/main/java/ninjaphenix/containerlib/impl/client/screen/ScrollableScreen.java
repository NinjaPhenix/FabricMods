package ninjaphenix.containerlib.impl.client.screen;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import ninjaphenix.containerlib.api.screen.ScrollableScreenMeta;
import ninjaphenix.containerlib.api.client.screen.AbstractScreen;
import ninjaphenix.containerlib.api.client.screen.widget.ScreenTypeSelectionScreenButton;
import ninjaphenix.containerlib.impl.client.ContainerLibraryClient;
import ninjaphenix.containerlib.impl.inventory.ScrollableContainer;

import java.util.Optional;

public class ScrollableScreen<T extends ScrollableContainer> extends AbstractScreen<T, ScrollableScreenMeta>
{
    private Rectangle blankArea = null;
    protected final boolean hasScrollbar;
    private boolean isDragging;
    private int topRow;

    public ScrollableScreen(T container)
    {
        super(container, (screenMeta) -> (screenMeta.WIDTH * 18 + 14) / 2 - 80);
        containerWidth = 14 + 18 * SCREEN_META.WIDTH;
        containerHeight = 17 + 97 + 18 * SCREEN_META.HEIGHT;
        hasScrollbar = SCREEN_META.TOTAL_ROWS != SCREEN_META.HEIGHT;
    }

    public Optional<me.shedaniel.math.api.Rectangle> getReiRectangle() {
        if(!hasScrollbar) { return Optional.empty(); }
        final int height = SCREEN_META.HEIGHT * 18 + (SCREEN_META.WIDTH > 9 ? 34 : 24);
        return Optional.of(new me.shedaniel.math.api.Rectangle(x + containerWidth - 4, y, 22, height));
    }

    @Override
    protected void init()
    {
        super.init();
        addButton(new ScreenTypeSelectionScreenButton(x + containerWidth -
                (hasScrollbar ? (ContainerLibraryClient.CONFIG.settings_button_center_on_scrollbar ? 2 : 1) : 19), y + 4));
        if (hasScrollbar)
        {
            isDragging = false;
            topRow = 0;
        }
        else {
            final int blanked = SCREEN_META.BLANK_SLOTS;
            if(blanked > 0) {
                final int xOffset = 7 + (SCREEN_META.WIDTH - blanked) * 18;
                blankArea = new Rectangle(x + xOffset, y + containerHeight - 115, blanked * 18, 18,
                        xOffset, containerHeight, SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
            }
        }
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY)
    {
        super.drawBackground(delta, mouseX, mouseY);
        if (hasScrollbar)
        {
            final int slotsHeight = SCREEN_META.HEIGHT * 18;
            final int scrollbarHeight = slotsHeight + (SCREEN_META.WIDTH > 9 ? 34 : 24);
            blit(x + containerWidth - 4, y, containerWidth, 0, 22, scrollbarHeight, SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
            int yOffset = MathHelper.floor((slotsHeight - 17) * (((double) topRow) / (SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT)));
            blit(x + containerWidth - 2, y + yOffset + 18, containerWidth, scrollbarHeight, 12, 15, SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
        }
        if (blankArea != null) { blankArea.render(); }
    }

    private boolean isMouseOverScrollbar(double mouseX, double mouseY)
    {
        final int top = y + 18;
        final int left = x + containerWidth - 2;
        return mouseX >= left && mouseY >= top && mouseX < left + 12 && mouseY < top + SCREEN_META.HEIGHT * 18;
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button)
    {
        return super.isClickOutsideBounds(mouseX, mouseY, left, top, button) && !isMouseOverScrollbar(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (hasScrollbar)
        {
            if (keyCode == 264 || keyCode == 267) // Down Arrow, Page Down
            {
                if (topRow != SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT)
                {
                    if (hasShiftDown()) { setTopRow(topRow, Math.min(topRow + SCREEN_META.HEIGHT, SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT)); }
                    else {setTopRow(topRow, topRow + 1);}
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
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (hasScrollbar && isMouseOverScrollbar(mouseX, mouseY) && button == 0)
        {
            isDragging = true;
            updateTopRow(mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (hasScrollbar && isDragging)
        {
            updateTopRow(mouseY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void updateTopRow(double mouseY)
    {
        int top = y + 18;
        int height = SCREEN_META.HEIGHT * 18;
        int newTopRow = MathHelper.floor(MathHelper.clampedLerp(0, SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT, (mouseY - top) / height));
        setTopRow(topRow, newTopRow);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        if (hasScrollbar && (!ContainerLibraryClient.CONFIG.restrictive_scrolling || isMouseOverScrollbar(mouseX, mouseY)))
        {
            int newTop;
            if (delta < 0)
            {
                newTop = Math.min(topRow + (hasShiftDown() ? SCREEN_META.HEIGHT : 1), SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT);
            }
            else
            {
                newTop = Math.max(topRow - (hasShiftDown() ? SCREEN_META.HEIGHT : 1), 0);
            }
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
                container.setSlotRange(setOutBegin, setOutBegin + setAmount, index -> -2000);
                container.moveSlotRange(movableBegin, setInBegin, -18 * rows);
                container.setSlotRange(setInBegin, Math.min(setInBegin + setAmount, SCREEN_META.TOTAL_SLOTS),
                        index -> 18 * MathHelper.floorDiv(index - movableBegin + SCREEN_META.WIDTH, SCREEN_META.WIDTH));
            }
            else
            {
                final int setInBegin = newTopRow * SCREEN_META.WIDTH;
                final int movableBegin = oldTopRow * SCREEN_META.WIDTH;
                final int setOutBegin = movableBegin + movableAmount;
                container.setSlotRange(setInBegin, setInBegin + setAmount,
                        index -> 18 * MathHelper.floorDiv(index - setInBegin + SCREEN_META.WIDTH, SCREEN_META.WIDTH));
                container.moveSlotRange(movableBegin, setOutBegin, 18 * rows);
                container.setSlotRange(setOutBegin, Math.min(setOutBegin + setAmount, SCREEN_META.TOTAL_SLOTS), index -> -2000);
            }
        }
        else
        {
            final int oldMin = oldTopRow * SCREEN_META.WIDTH;
            container.setSlotRange(oldMin, Math.min(oldMin + SCREEN_META.WIDTH * SCREEN_META.HEIGHT, SCREEN_META.TOTAL_SLOTS), index -> -2000);
            final int newMin = newTopRow * SCREEN_META.WIDTH;
            container.setSlotRange(newMin, newMin + SCREEN_META.WIDTH * SCREEN_META.HEIGHT,
                    index -> 18 + 18 * MathHelper.floorDiv(index - newMin, SCREEN_META.WIDTH));
        }

        if (newTopRow == SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT)
        {
            int blanked = SCREEN_META.BLANK_SLOTS;
            if(blanked > 0) {
                final int xOffset = 7 + (SCREEN_META.WIDTH - blanked) * 18;
                blankArea = new Rectangle(x + xOffset, y + containerHeight - 115, blanked * 18, 18,
                        xOffset, containerHeight, SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
            }
        }
        else
        {
            blankArea = null;
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height)
    {
        if (hasScrollbar)
        {
            int row = topRow;
            super.resize(client, width, height);
            setTopRow(topRow, row);
        }
        else
        {
            super.resize(client, width, height);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (hasScrollbar && isDragging)
        {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}


