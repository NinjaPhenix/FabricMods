package ninjaphenix.containerlib.impl.client.screen;


import net.minecraft.client.util.math.MatrixStack;
import ninjaphenix.containerlib.api.screen.SingleScreenMeta;
import ninjaphenix.containerlib.api.client.screen.AbstractScreen;
import ninjaphenix.containerlib.api.client.screen.widget.ScreenTypeSelectionScreenButton;
import ninjaphenix.containerlib.impl.inventory.SingleScreenHandler;

public class SingleScreen<T extends SingleScreenHandler> extends AbstractScreen<T, SingleScreenMeta>
{
    private Rectangle blankArea = null;

    public SingleScreen(final T container)
    {
        super(container, (screenMeta) -> (screenMeta.WIDTH * 18 + 14) / 2 - 80);
        backgroundWidth = 14 + 18 * SCREEN_META.WIDTH;
        backgroundHeight = 17 + 97 + 18 * SCREEN_META.HEIGHT;
    }

    @Override
    protected void init()
    {
        super.init();
        addButton(new ScreenTypeSelectionScreenButton(x + backgroundWidth - 19, y + 4));
        final int blanked = SCREEN_META.BLANK_SLOTS;
        if (blanked > 0)
        {
            final int xOffset = 7 + (SCREEN_META.WIDTH - blanked) * 18;
            blankArea = new Rectangle(x + xOffset, y + backgroundHeight - 115, blanked * 18, 18,
                    xOffset, backgroundHeight, SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
        }
    }

    @Override
    protected void drawBackground(final MatrixStack matrices, final float delta, final int mouseX, final int mouseY)
    {
        super.drawBackground(matrices, delta, mouseX, mouseY);
        if (blankArea != null) { blankArea.render(matrices); }
    }
}


