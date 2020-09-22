package ninjaphenix.renderingtests.atlas;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.SimpleResource;
import ninjaphenix.renderingtests.Initializer;
import ninjaphenix.renderingtests.Main;
import java.io.IOException;

public class AtlasInitializer implements Initializer
{
    public static final Initializer INSTANCE = new AtlasInitializer();

    @Override
    public void onCommon()
    {

    }

    @Override
    public void onClient()
    {
        ClientSpriteRegistryCallback.event(TextureAtlas.LOCATION_BLOCKS).register(AssetReloadListener.INSTANCE::uploadSprites);
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(AssetReloadListener.INSTANCE);
    }
}
