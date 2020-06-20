package ninjaphenix.expandedstorage.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import ninjaphenix.chainmail.api.client.render.ChainmailRendering;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.block.misc.CursedChestType;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ExpandedStorageClient implements ClientModInitializer
{
    public static final ExpandedStorageClient INSTANCE = new ExpandedStorageClient();
    public static final Identifier CHEST_TEXTURE_ATLAS = ExpandedStorage.getId("chest_textures");
    private static final CursedChestBlockEntity CURSED_CHEST_RENDER_ENTITY = new CursedChestBlockEntity(null);

    @SuppressWarnings("SameParameterValue")
    private static <T extends Registries.TierData> void iterateOurTiers(SimpleRegistry<T> registry, Consumer<T> consumer)
    {
        for (Identifier id : registry.getIds())
        {
            if (id.getNamespace().equals(ExpandedStorage.MOD_ID) && !id.getPath().equals("null"))
            { registry.getOrEmpty(id).ifPresent(consumer); }
        }
    }

    public void makeAtlas(Consumer<SpriteIdentifier> consumer)
    {
        iterateOurTiers(Registries.CHEST, (data) ->
        {
            consumer.accept(new SpriteIdentifier(CHEST_TEXTURE_ATLAS, data.getChestTexture(CursedChestType.SINGLE)));
            consumer.accept(new SpriteIdentifier(CHEST_TEXTURE_ATLAS, data.getChestTexture(CursedChestType.BOTTOM)));
            consumer.accept(new SpriteIdentifier(CHEST_TEXTURE_ATLAS, data.getChestTexture(CursedChestType.LEFT)));
            consumer.accept(new SpriteIdentifier(CHEST_TEXTURE_ATLAS, data.getChestTexture(CursedChestType.FRONT)));
        });
    }

    @Override
    public void onInitializeClient()
    {
        ClientSpriteRegistryCallback.event(CHEST_TEXTURE_ATLAS).register((atlas, registry) ->
                iterateOurTiers(Registries.CHEST, (data) ->
                {
                    registry.register(data.getChestTexture(CursedChestType.SINGLE));
                    registry.register(data.getChestTexture(CursedChestType.BOTTOM));
                    registry.register(data.getChestTexture(CursedChestType.LEFT));
                    registry.register(data.getChestTexture(CursedChestType.FRONT));
                })
        );
        BlockEntityRendererRegistry.INSTANCE.register(ExpandedStorage.CHEST, CursedChestBlockEntityRenderer::new);
        ChainmailRendering.INSTANCE.registerBlockEntityItemStackRenderer(ExpandedStorage.CHEST, (itemStack, mode, matrixStack, consumerProvider, light, overlay) ->
        {
            CURSED_CHEST_RENDER_ENTITY.setBlock(Registry.BLOCK.getId(((BlockItem) itemStack.getItem()).getBlock()));
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(CURSED_CHEST_RENDER_ENTITY, matrixStack, consumerProvider, light, overlay);
        });
    }
}
