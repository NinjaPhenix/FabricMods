package ninjaphenix.containerlib.client.screen;

import ninjaphenix.containerlib.inventory.CContainer;

public class ScrollingScreen<T extends CContainer> extends FixedScreen<T>
{
    public ScrollingScreen(T container)
    {
        super(container);
    }
}
