package ninjaphenix.renderingtests.atlas;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import ninjaphenix.renderingtests.Main;
import ninjaphenix.renderingtests.Sprite;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AssetReloadListener implements IdentifiableResourceReloadListener
{
    public static final AssetReloadListener INSTANCE = new AssetReloadListener();

    public Map<String, NativeImage> chestAtlases = Maps.newConcurrentMap();

    public void uploadSprites(TextureAtlas atlas, ClientSpriteRegistryCallback.Registry registry)
    {
        chestAtlases.forEach((tier, image) -> getTextures(tier, atlas, image).forEach(sprite ->
                                                                                      {
                                                                                          System.out.println("Registering: " + sprite.getName());
                                                                                          registry.register(sprite);
                                                                                      }));
    }

    @Override
    public ResourceLocation getFabricId() { return Main.resloc("chest_atlas_listener"); }

    @Override
    public CompletableFuture<Void> reload(final PreparationBarrier barrier, final ResourceManager manager, final ProfilerFiller filler,
                                          final ProfilerFiller filler2, final Executor executor, final Executor executor2)
    {
        var dumbThings = new CompletableFuture[]
                {
                        doDumbStuff("wood", manager, executor2),
                        doDumbStuff("iron", manager, executor2),
                        doDumbStuff("gold", manager, executor2),
                        doDumbStuff("diamond", manager, executor2),
                        doDumbStuff("obsidian", manager, executor2),
                        doDumbStuff("netherite", manager, executor2)
                };
        return CompletableFuture.allOf(dumbThings).thenCompose(barrier::wait);
    }

    private CompletableFuture<Void> doDumbStuff(final String chestTier, final ResourceManager manager, final Executor executor)
    {
        return CompletableFuture.runAsync(
                () ->
                {
                    try (var resource = manager.getResource(Main.resloc("textures/block/old_" + chestTier + "_chest.png")))
                    {
                        try
                        {
                            var image = NativeImage.read(resource.getInputStream());
                            chestAtlases.put(chestTier, image);
                        }
                        catch (final IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    catch (final IOException e)
                    {
                        e.printStackTrace();
                    }
                }, executor);
    }

    private Set<Sprite> getTextures(final String chestTier, final TextureAtlas atlas, final NativeImage image)
    {
        int textureSize = image.getWidth() / 4;
        int mipLevel = Math.min(Integer.lowestOneBit(textureSize), Integer.lowestOneBit(image.getHeight() / 4));
        var values = new HashSet<Sprite>();
        final var singleTop = new NativeImage(textureSize, textureSize, false);
        singleTop.copyFrom(image); // I hope this actually works
        values.add(new Sprite(atlas,
                              new TextureAtlasSprite.Info(Main.resloc(chestTier + "_single_top"), textureSize, textureSize, AnimationMetadataSection.EMPTY),
                              mipLevel,
                              1,
                              1,
                              textureSize,
                              textureSize,
                              singleTop
        ));
        // do the rest of the textures
        return values;
    }
}