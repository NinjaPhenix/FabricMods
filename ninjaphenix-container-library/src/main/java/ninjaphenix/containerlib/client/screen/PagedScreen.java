package ninjaphenix.containerlib.client.screen;

import ninjaphenix.containerlib.inventory.CContainer;

public class PagedScreen<T extends CContainer> extends FixedScreen<T>
{
    public PagedScreen(T container)
    {
        super(container);
    }
}
