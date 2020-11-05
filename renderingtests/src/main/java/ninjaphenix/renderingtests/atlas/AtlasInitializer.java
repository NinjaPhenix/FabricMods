package ninjaphenix.renderingtests.atlas;

import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.server.packs.PackType;
import ninjaphenix.renderingtests.Initializer;

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
