package ninjaphenix.expandedstorage.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import ninjaphenix.expandedstorage.client.ExpandedStorageClient;

public final class SelectContainerScreen extends Screen
{
    private final HashMap<ResourceLocation, Tuple<ResourceLocation, Component>> OPTIONS;
    private final int PADDING = 24;
    private int TOP;

    public SelectContainerScreen(final HashMap<ResourceLocation, Tuple<ResourceLocation, Component>> options)
    {
        super(new TranslatableComponent("screen.expandedstorage.screen_picker_title"));
        OPTIONS = options;
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    protected void init()
    {
        super.init();
        final int choices = OPTIONS.size();
        final int maxColumns = Math.min(Mth.intFloorDiv(width - PADDING, 96 + PADDING), choices);
        final int totalRows = Mth.ceil((double) choices / maxColumns);
        int x = 0;
        int y = 0;
        int leftPadding = Mth.ceil((width - 96 * maxColumns - PADDING * (maxColumns - 1)) / 2D);
        final int topPadding = Mth.ceil((height - 96 * totalRows - PADDING * (totalRows - 1)) / 2D);
        TOP = topPadding;
        for (final HashMap.Entry<ResourceLocation, Tuple<ResourceLocation, Component>> entry : OPTIONS.entrySet())
        {
            final ResourceLocation id = entry.getKey();
            final Tuple<ResourceLocation, Component> settings = entry.getValue();
            addButton(new ScreenTypeButton(leftPadding + (PADDING + 96) * x, topPadding + (PADDING + 96) * y, 96, 96,
                                           settings.getA(), settings.getB(), button -> updatePlayerPreference(id),
                                           (button, matrices, tX, tY) -> renderTooltip(matrices, button.getMessage(), tX, tY)));
            x++;
            if (x == maxColumns)
            {
                x = 0;
                y++;
                if (y == totalRows - 1)
                {
                    final int remaining = choices - (maxColumns * (totalRows - 1));
                    leftPadding = Mth.ceil((width - 96 * remaining - PADDING * (remaining - 1)) / 2D);
                }
            }
        }
    }

    @Override
    public void onClose()
    {
        ExpandedStorageClient.sendCallbackRemoveToServer();
        super.onClose();
    }

    private void updatePlayerPreference(final ResourceLocation selection)
    {
        ExpandedStorageClient.setPreference(selection);
        ExpandedStorageClient.sendPreferencesToServer();
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }

    @Override
    public void render(final PoseStack matrices, final int mouseX, final int mouseY, final float delta)
    {
        setBlitOffset(0);
        renderBackground(matrices);
        int buttonsSize = buttons.size();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < buttonsSize; i++) { buttons.get(i).render(matrices, mouseX, mouseY, delta); }
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < buttonsSize; i++)
        {
            AbstractWidget button = buttons.get(i);
            if (button instanceof ScreenTypeButton) { ((ScreenTypeButton) button).renderTooltip(matrices, mouseX, mouseY); }
        }
        drawCenteredString(matrices, font, title, width / 2, Math.max(TOP - 2 * PADDING, 0), 0xFFFFFFFF);
    }

    private static class ScreenTypeButton extends Button
    {
        private final ResourceLocation TEXTURE;

        public ScreenTypeButton(final int x, final int y, final int width, final int height, final ResourceLocation texture, final Component message,
                                final OnPress pressAction, final OnTooltip tooltipSupplier)
        {
            super(x, y, width, height, message, pressAction, tooltipSupplier);
            TEXTURE = texture;
        }

        @Override
        public void renderButton(final PoseStack matrices, final int mouseX, final int mouseY, final float delta)
        {
            Minecraft.getInstance().getTextureManager().bind(TEXTURE);
            blit(matrices, x, y, 0, isHovered() ? height : 0, width, height, width, height * 2);
        }

        public void renderTooltip(final PoseStack matrices, final int mouseX, final int mouseY)
        {
            if (active)
            {
                if (isHovered) { renderToolTip(matrices, mouseX, mouseY); }
                else if (isFocused()) { renderToolTip(matrices, x, y); }
            }
        }
    }
}