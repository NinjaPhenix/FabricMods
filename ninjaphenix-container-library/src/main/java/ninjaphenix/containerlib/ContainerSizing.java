package ninjaphenix.containerlib;

/**
 * Just a data class.
 */
public class ContainerSizing
{
    public final int FIXED_WIDTH;
    public final int FIXED_HEIGHT;
    public final int SCROLL_WIDTH;
    public final int SCROLL_HEIGHT;
    public final int SCROLL_ROWS;
    public final int PAGED_WIDTH;
    public final int PAGED_HEIGHT;
    public final int PAGED_PAGES;

    public ContainerSizing(int fWidth, int fHeight, int sWidth, int sHeight, int sRows, int pWidth, int pHeight, int pages)
    {
        this.FIXED_WIDTH = fWidth; this.FIXED_HEIGHT = fHeight;
        this.SCROLL_WIDTH = sWidth; this.SCROLL_HEIGHT = sHeight; this.SCROLL_ROWS = sRows;
        this.PAGED_WIDTH = pWidth; this.PAGED_HEIGHT = pHeight; this.PAGED_PAGES = pages;
    }

    public ContainerSizing(int width, int height)
    {
        this(width, height, width, height, height, width, height, 1);
    }

    public boolean hasPages()
    {
        return PAGED_PAGES > 1;
    }

    public boolean hasScroll()
    {
        return SCROLL_ROWS > SCROLL_HEIGHT;
    }
}
