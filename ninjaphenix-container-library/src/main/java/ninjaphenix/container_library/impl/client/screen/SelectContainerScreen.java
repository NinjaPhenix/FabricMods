package ninjaphenix.container_library.impl.client.screen;

import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import ninjaphenix.container_library.impl.common.Const;

public final class SelectContainerScreen extends Screen
{
    private final Map<Identifier, Pair<Identifier, Text>> OPTIONS;
    private final Consumer<Identifier> CLOSE_CALLBACK;
    private final int PADDING = 24;
    private int TOP;

    public SelectContainerScreen(final Map<Identifier, Pair<Identifier, Text>> options, final Consumer<Identifier> onSuccessfulClose)
    {
        super(Const.translation("screen.%s.screen_picker_title"));
        OPTIONS = options;
        CLOSE_CALLBACK = onSuccessfulClose;
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
        final int topPadding = MathHelper.ceil((height - 96 * totalRows - PADDING * (totalRows - 1)) / 2D);
        TOP = topPadding;
        for (final Map.Entry<Identifier, Pair<Identifier, Text>> entry : OPTIONS.entrySet())
        {
            final Identifier id = entry.getKey();
            final Pair<Identifier, Text> settings = entry.getValue();
            addButton(new ScreenTypeButton(leftPadding + (PADDING + 96) * x, topPadding + (PADDING + 96) * y, 96, 96,
                                           settings.getLeft(), settings.getRight(), button -> updatePlayerPreference(id),
                                           (button, matrices, tX, tY) -> renderTooltip(matrices, button.getMessage(), tX, tY)));
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

    private void updatePlayerPreference(final Identifier selection)
    {
        CLOSE_CALLBACK.accept(selection);
    }

    @Override
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta)
    {
        setZOffset(0);
        renderBackground(matrices);
        for (final AbstractButtonWidget button : buttons) { button.render(matrices, mouseX, mouseY, delta); }
        for (final AbstractButtonWidget button : buttons)
        {
            if (button instanceof ScreenTypeButton) { ((ScreenTypeButton) button).renderTooltip(matrices, mouseX, mouseY); }
        }
        drawCenteredText(matrices, textRenderer, title, width / 2, Math.max(TOP - 2 * PADDING, 0), 0xFFFFFFFF);
    }

    private static class ScreenTypeButton extends ButtonWidget
    {
        private final Identifier TEXTURE;

        public ScreenTypeButton(final int x, final int y, final int width, final int height, final Identifier texture, final Text message,
                                final PressAction pressAction, final TooltipSupplier tooltipSupplier)
        {
            super(x, y, width, height, message, pressAction, tooltipSupplier);
            TEXTURE = texture;
        }

        @Override
        public void renderButton(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta)
        {
            MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
            drawTexture(matrices, x, y, 0, isHovered() ? height : 0, width, height, width, height * 2);
        }

        public void renderTooltip(final MatrixStack matrices, final int mouseX, final int mouseY)
        {
            if (active)
            {
                if (hovered) { renderToolTip(matrices, mouseX, mouseY); }
                else if (isHovered()) { renderToolTip(matrices, x, y); }
            }
        }
    }
}