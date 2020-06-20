package torcherino.client.screen.widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class FixedSliderWidget extends SliderWidget
{
    private final float nudgeAmount;

    protected FixedSliderWidget(int x, int y, int width, double progress, int permutations)
    {
        super(x, y, width, 20, progress);
        nudgeAmount = 1.0F / permutations;
        this.updateMessage();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        boolean pressedLeft = keyCode == 263;
        if (pressedLeft || keyCode == 262) { this.setValue(this.value + (pressedLeft ? -nudgeAmount : nudgeAmount)); }
        return false;
    }

    private void setValue(double value)
    {
        double currentValue = this.value;
        this.value = MathHelper.clamp(value, 0, 1);
        if (currentValue != this.value) { this.applyValue(); }
        this.updateMessage();
    }
}
