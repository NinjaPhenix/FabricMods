package ninjaphenix.renderingtests;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlas;

public class Sprite extends net.minecraft.client.renderer.texture.TextureAtlasSprite
{
    public Sprite(final TextureAtlas atlas, final Info info, final int mipmap, final int u, final int v, final int x,
                  final int y, final NativeImage image)
    {
        super(atlas, info, mipmap, u, v, x, y, image);
    }
}
