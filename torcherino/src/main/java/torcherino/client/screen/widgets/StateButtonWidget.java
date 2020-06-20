package torcherino.client.screen.widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public abstract class StateButtonWidget extends ButtonWidget
{
    private static final ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    private final Screen screen;
    private Text narrationMessage;

    public StateButtonWidget(Screen screen, int x, int y)
    {
        super(x, y, 20, 20, new LiteralText(""), null);
        this.screen = screen;
        initialize();
    }

    protected abstract void initialize();

    protected abstract void nextState();

    protected abstract ItemStack getButtonIcon();

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (visible)
        {
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            itemRenderer.renderInGuiWithOverrides(getButtonIcon(), x + 2, y + 2);
            if (this.isHovered())
            {
                screen.renderTooltip(matrixStack, narrationMessage, x + 14, y + 18);
            }
        }
    }

    @Override
    public void onPress() { nextState(); }

    @Override
    public MutableText getNarrationMessage() { return new TranslatableText("gui.narrate.button", narrationMessage); }

    protected void setNarrationMessage(Text narrationMessage) { this.narrationMessage = narrationMessage; }
}
