package ninjaphenix.containerlib;

import net.minecraft.util.Identifier;

/**
 * Just a data class.
 */
public class ScreenSizing
{
    public final ScreenSize FIXED;
    public final ScreenSize SCROLLING;
    public final ScreenSize PAGED;

    private ScreenSizing(Builder builder)
    {
        FIXED = builder.fixed();
        SCROLLING = builder.scrolling();
        PAGED = builder.paged();
    }

    public static class ScreenSize
    {
        public final int WIDTH;
        public final int HEIGHT;
        public final Identifier TEXTURE;
        public final int EXTRA;

        private ScreenSize(int width, int height, Identifier texture, int extra)
        {
            WIDTH = width;
            HEIGHT = height;
            EXTRA = extra;
            TEXTURE = texture;
        }

        public boolean hasPages() { return EXTRA > 1; }

        public boolean hasScroll() { return EXTRA > HEIGHT; }
    }

    public static class Builder
    {
        private ScreenSize fixed;
        private ScreenSize scrolling;
        private ScreenSize paged;

        public Builder() {}

        public Builder fixedParams(int width, int height, Identifier texture) { fixed = new ScreenSize(width, height, texture, 0); return this; }

        public Builder scrollParams(int width, int height, Identifier texture, int totalRows)
        { scrolling = new ScreenSize(width, height, texture, totalRows); return this; }

        public Builder pagedParams(int width, int height, Identifier texture, int pages)
        { paged = new ScreenSize(width, height, texture, pages); return this; }

        public ScreenSize fixed() { return fixed; }

        public ScreenSize scrolling() { return scrolling; }

        public ScreenSize paged() { return paged; }

        public ScreenSizing build()
        {
            assert fixed != null;
            if (scrolling == null) { scrollParams(fixed.WIDTH, fixed.HEIGHT, fixed.TEXTURE, fixed.HEIGHT); }
            if (paged == null) { pagedParams(fixed.WIDTH, fixed.HEIGHT, fixed.TEXTURE, 1); }
            return new ScreenSizing(this);
        }
    }
}
