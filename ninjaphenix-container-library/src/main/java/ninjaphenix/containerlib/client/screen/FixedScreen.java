package ninjaphenix.containerlib.client.screen;

import net.minecraft.client.gui.screen.ingame.ContainerProvider;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import ninjaphenix.containerlib.inventory.CContainer;

public class FixedScreen<T extends CContainer> extends ContainerScreen<T> implements ContainerProvider<T>
{
    public FixedScreen(T container)
    {
        super(container, container.PLAYER_INVENTORY, container.getDisplayName());
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY)
    {
        renderBackground();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta)
    {
        //super.render(mouseX, mouseY, delta);
        this.drawBackground(delta, mouseX, mouseY);
    }
}
