package ninjaphenix.containerlib.impl.client.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import ninjaphenix.containerlib.impl.client.ContainerLibraryClient;
import ninjaphenix.containerlib.impl.client.ScreenMiscSettings;

import java.util.HashMap;

public class SelectContainerScreen extends Screen
{
    private final HashMap<Identifier, ScreenMiscSettings> OPTIONS;
    private final int PADDING = 24;
    private int TOP;

    public SelectContainerScreen(HashMap<Identifier, ScreenMiscSettings> options)
    {
        super(new TranslatableText("screen.ninjaphenix-container-lib.screen_picker_title"));
        OPTIONS = options;
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    protected void init()
    {
        super.init();
        final int choices = OPTIONS.size();
        final int maxColumns = Math.min(MathHelper.floorDiv(width - PADDING, 96 + PADDING), choices);
        final int totalRows = MathHelper.ceil((double) choices / maxColumns);
        int x = 0;
        int y = 0;
        int leftPadding = MathHelper.ceil((width - 96 * maxColumns - PADDING * (maxColumns - 1)) / 2D);
        int topPadding = MathHelper.ceil((height - 96 * totalRows - PADDING * (totalRows - 1)) / 2D);
        TOP = topPadding;
        for (HashMap.Entry<Identifier, ScreenMiscSettings> entry : OPTIONS.entrySet())
        {
            final Identifier id = entry.getKey();
            final ScreenMiscSettings settings = entry.getValue();
            addButton(new ScreenTypeButton(leftPadding + (PADDING + 96) * x, topPadding + (PADDING + 96) * y, 96, 96,
                    settings.SELECT_TEXTURE_ID, settings.NARRATION_MESSAGE.asString(), button -> updatePlayerPreference(id)));
            x++;
            if (x == maxColumns)
            {
                x = 0;
                y++;
                if (y == totalRows - 1)
                {
                    final int remaining = choices - (maxColumns * (totalRows - 1));
                    leftPadding = MathHelper.ceil((width - 96 * remaining - PADDING * (remaining - 1)) / 2D);
                }
            }
        }
    }

    @Override
    public void onClose()
    {
        ContainerLibraryClient.sendCallbackRemoveToServer();
        super.onClose();
    }

    private void updatePlayerPreference(Identifier selection)
    {
        ContainerLibraryClient.setPreference(selection);
        ContainerLibraryClient.sendPreferencesToServer();
        MinecraftClient.getInstance().openScreen(null);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta)
    {
        setBlitOffset(0);
        renderBackground();
        super.render(mouseX, mouseY, delta);
        drawCenteredString(font, title.asFormattedString(), width / 2, Math.max(TOP - 2 * PADDING, 0), 0xFFFFFFFF);
    }

    private class ScreenTypeButton extends ButtonWidget
    {
        private final Identifier TEXTURE;

        public ScreenTypeButton(int x, int y, int width, int height, Identifier texture, String message, ButtonWidget.PressAction pressAction)
        {
            super(x, y, width, height, message, pressAction);
            TEXTURE = texture;
        }

        @Override
        public void renderButton(int mouseX, int mouseY, float delta)
        {
            MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
            blit(x, y, 0, isHovered() ? height : 0, width, height, width, height * 2);
            // todo: check in 1.16 if I can fix tooltips rendering under other buttons by sharing the matrix stack.
            if (isHovered()) { renderToolTip(x, y); }
        }

        @Override
        public void renderToolTip(int mouseX, int mouseY) { SelectContainerScreen.this.renderTooltip(this.getMessage(), mouseX, mouseY); }
    }

}
