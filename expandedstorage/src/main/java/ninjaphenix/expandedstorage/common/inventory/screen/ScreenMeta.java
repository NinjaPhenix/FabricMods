package ninjaphenix.expandedstorage.common.inventory.screen;

import net.minecraft.resources.ResourceLocation;

public abstract class ScreenMeta
{
    public final int WIDTH, HEIGHT, TOTAL_SLOTS, TEXTURE_WIDTH, TEXTURE_HEIGHT;
    public final ResourceLocation TEXTURE;

    protected ScreenMeta(final int width, final int height, final int totalSlots, final ResourceLocation texture, final int textureWidth,
                         final int textureHeight)
    {
        WIDTH = width;
        HEIGHT = height;
        TOTAL_SLOTS = totalSlots;
        TEXTURE = texture;
        TEXTURE_WIDTH = textureWidth;
        TEXTURE_HEIGHT = textureHeight;
    }
}