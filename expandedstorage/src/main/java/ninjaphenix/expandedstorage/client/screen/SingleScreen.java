package ninjaphenix.expandedstorage.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Rectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.expandedstorage.client.screen.widget.ScreenTypeSelectionScreenButton;
import ninjaphenix.expandedstorage.common.inventory.SingleScreenHandler;
import ninjaphenix.expandedstorage.common.inventory.screen.SingleScreenMeta;
import java.util.Collections;
import java.util.List;

public final class SingleScreen extends AbstractScreen<SingleScreenHandler, SingleScreenMeta>
{
    private Image blankArea = null;

    public SingleScreen(final SingleScreenHandler container, final Inventory playerInventory, final Component title)
    {
        super(container, playerInventory, title, (screenMeta) -> (screenMeta.WIDTH * 18 + 14) / 2 - 80);
        imageWidth = 14 + 18 * SCREEN_META.WIDTH;
        imageHeight = 17 + 97 + 18 * SCREEN_META.HEIGHT;
    }

    @Override
    protected void init()
    {
        super.init();
        addButton(new ScreenTypeSelectionScreenButton(leftPos + imageWidth + 4, topPos, this::renderButtonTooltip));
        final int blanked = SCREEN_META.BLANK_SLOTS;
        if (blanked > 0)
        {
            final int xOffset = 7 + (SCREEN_META.WIDTH - blanked) * 18;
            blankArea = new Image(leftPos + xOffset, topPos + imageHeight - 115, blanked * 18, 18, xOffset, imageHeight,
                                  SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
        }
    }

    @Override
    protected void renderBg(final PoseStack matrices, final float delta, final int mouseX, final int mouseY)
    {
        super.renderBg(matrices, delta, mouseX, mouseY);
        if (blankArea != null) { blankArea.render(matrices); }
    }

    @Override
    public List<Rectangle> getReiRectangles()
    {
        return Collections.singletonList(new Rectangle(leftPos + imageWidth + 4, topPos, 22, 22));
    }
}