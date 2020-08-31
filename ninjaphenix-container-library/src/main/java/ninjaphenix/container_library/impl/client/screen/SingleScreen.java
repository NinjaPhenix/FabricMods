package ninjaphenix.container_library.impl.client.screen;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import ninjaphenix.container_library.api.client.screen.AbstractScreen;
import ninjaphenix.container_library.api.client.screen.widget.ScreenTypeSelectionScreenButton;
import ninjaphenix.container_library.impl.common.inventory.SingleScreenHandler;

public final class SingleScreen extends AbstractScreen<SingleScreenHandler, SingleScreenHandler.SingleScreenMeta>
{
    private Rectangle blankArea = null;

    public SingleScreen(final SingleScreenHandler container, final PlayerInventory playerInventory, final Text title)
    {
        super(container, playerInventory, title, (screenMeta) -> (screenMeta.WIDTH * 18 + 14) / 2 - 80);
        backgroundWidth = 14 + 18 * SCREEN_META.WIDTH;
        backgroundHeight = 17 + 97 + 18 * SCREEN_META.HEIGHT;
    }

    @Override
    protected void init()
    {
        super.init();
        final FabricLoader instance = FabricLoader.getInstance();
        final boolean inventoryProfilesLoaded = instance.isModLoaded("inventoryprofiles");
        final boolean inventorySorterLoaded = instance.isModLoaded("inventorysorter");
        final int settingsXOffset;
        if (inventoryProfilesLoaded) { settingsXOffset = -67; }
        else if (inventorySorterLoaded) { settingsXOffset = -37; }
        else { settingsXOffset = -19;}
        addButton(new ScreenTypeSelectionScreenButton(x + backgroundWidth + settingsXOffset, y + 4, (button, matrices, mouseX, mouseY) ->
                renderTooltip(matrices, button.getMessage(), mouseX, mouseY)));
        final int blanked = SCREEN_META.BLANK_SLOTS;
        if (blanked > 0)
        {
            final int xOffset = 7 + (SCREEN_META.WIDTH - blanked) * 18;
            blankArea = new Rectangle(x + xOffset, y + backgroundHeight - 115, blanked * 18, 18, xOffset, backgroundHeight,
                                      SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
        }
    }

    @Override
    protected void drawBackground(final MatrixStack matrices, final float delta, final int mouseX, final int mouseY)
    {
        super.drawBackground(matrices, delta, mouseX, mouseY);
        if (blankArea != null) { blankArea.render(matrices); }
    }
}