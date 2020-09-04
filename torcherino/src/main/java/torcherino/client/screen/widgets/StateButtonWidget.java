package torcherino.client.screen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public abstract class StateButtonWidget extends Button
{
    private static final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    private final Screen screen;
    private Component narrationMessage;

    public StateButtonWidget(Screen screen, int x, int y)
    {
        super(x, y, 20, 20, new TextComponent(""), null);
        this.screen = screen;
        initialize();
    }

    protected abstract void initialize();

    protected abstract void nextState();

    protected abstract ItemStack getButtonIcon();

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (visible)
        {
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            itemRenderer.renderAndDecorateItem(getButtonIcon(), x + 2, y + 2);
            if (this.isHovered())
            {
                screen.renderTooltip(matrixStack, narrationMessage, x + 14, y + 18);
            }
        }
    }

    @Override
    public void onPress() { nextState(); }

    @Override
    public MutableComponent createNarrationMessage() { return new TranslatableComponent("gui.narrate.button", narrationMessage); }

    protected void setNarrationMessage(Component narrationMessage) { this.narrationMessage = narrationMessage; }
}
