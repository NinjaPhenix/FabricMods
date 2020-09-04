package ninjaphenix.expandedstorage.client;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import blue.endless.jankson.JsonPrimitive;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.BlockItem;
import ninjaphenix.chainmail.api.config.JanksonConfigParser;
import ninjaphenix.expandedstorage.client.config.ContainerConfig;
import ninjaphenix.expandedstorage.client.screen.PagedScreen;
import ninjaphenix.expandedstorage.client.screen.ScrollableScreen;
import ninjaphenix.expandedstorage.client.screen.SelectContainerScreen;
import ninjaphenix.expandedstorage.client.screen.SingleScreen;
import ninjaphenix.expandedstorage.common.Const;
import ninjaphenix.expandedstorage.common.ExpandedStorage;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.CursedChestBlock;
import ninjaphenix.expandedstorage.common.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.common.misc.CursedChestType;
import ninjaphenix.expandedstorage.common.ModContent;
import org.apache.logging.log4j.MarkerManager;

import static ninjaphenix.expandedstorage.common.ModContent.*;

public final class ExpandedStorageClient implements ClientModInitializer
{
    public static final ExpandedStorageClient INSTANCE = new ExpandedStorageClient();
    private static final CursedChestBlockEntity CURSED_CHEST_RENDER_ENTITY = new CursedChestBlockEntity(null);
    public static final ContainerConfig CONFIG = getConfigParser().load(ContainerConfig.class, ContainerConfig::new, getConfigPath(), new MarkerManager.Log4jMarker(Const.MOD_ID));

    static
    {
        if (CONFIG.preferred_container_type.getNamespace().equals("ninjaphenix-container-lib"))
        {
            setPreference(new ResourceLocation(Const.MOD_ID, CONFIG.preferred_container_type.getPath()));
        }
    }

    @Override
    public void onInitializeClient()
    {
        ClientSidePacketRegistry.INSTANCE.register(Const.SCREEN_SELECT, (context, buffer) ->
        {
            final int count = buffer.readInt();
            final HashMap<ResourceLocation, Tuple<ResourceLocation, Component>> allowed = new HashMap<>();
            for (int i = 0; i < count; i++)
            {
                final ResourceLocation containerFactoryId = buffer.readResourceLocation();
                if (ExpandedStorage.INSTANCE.isContainerTypeDeclared(containerFactoryId))
                {
                    allowed.put(containerFactoryId, ExpandedStorage.INSTANCE.getScreenSettings(containerFactoryId));
                }
            }
            Minecraft.getInstance().setScreen(new SelectContainerScreen(allowed));
        });
        ClientSpriteRegistryCallback.event(Sheets.CHEST_SHEET).register(
                (atlas, registry) -> Registries.CHEST.stream().forEach(data -> Arrays.stream(CursedChestType.values())
                        .map(data::getChestTexture).forEach(registry::register)));
        BlockEntityRendererRegistry.INSTANCE.register(ModContent.CHEST, CursedChestBlockEntityRenderer::new);
        ModContent.CHEST.validBlocks.forEach(block -> BuiltinItemRendererRegistry.INSTANCE.register(
                block.asItem(), (stack, matrices, vertexConsumers, light, overlay) ->
                {
                    CursedChestBlock renderBlock = (CursedChestBlock) ((BlockItem) stack.getItem()).getBlock();
                    CURSED_CHEST_RENDER_ENTITY.setBlock(renderBlock.TIER_ID);
                    BlockEntityRenderDispatcher.instance.renderItem(CURSED_CHEST_RENDER_ENTITY, matrices, vertexConsumers, light, overlay);
                }));
        ScreenRegistry.register(SCROLLABLE_HANDLER_TYPE, ScrollableScreen::new);
        ScreenRegistry.register(PAGED_HANDLER_TYPE, PagedScreen::new);
        ScreenRegistry.register(SINGLE_HANDLER_TYPE, SingleScreen::new);
    }

    private static JanksonConfigParser getConfigParser()
    {
        return new JanksonConfigParser.Builder().deSerializer(
                JsonPrimitive.class, ResourceLocation.class, (it, marshaller) -> new ResourceLocation(it.asString()),
                ((identifier, marshaller) -> marshaller.serialize(identifier.toString()))).build();
    }

    private static Path getConfigPath() { return FabricLoader.getInstance().getConfigDir().resolve("ninjaphenix-container-library.json"); }

    public static void sendPreferencesToServer()
    {
        ClientSidePacketRegistry.INSTANCE.sendToServer(Const.SCREEN_SELECT, new FriendlyByteBuf(Unpooled.buffer())
                .writeResourceLocation(CONFIG.preferred_container_type));
    }

    public static void sendCallbackRemoveToServer()
    {
        ClientSidePacketRegistry.INSTANCE.sendToServer(Const.SCREEN_SELECT, new FriendlyByteBuf(Unpooled.buffer())
                .writeResourceLocation(Const.id("auto")));
    }

    public static void setPreference(final ResourceLocation handlerType)
    {
        CONFIG.preferred_container_type = handlerType;
        getConfigParser().save(CONFIG, getConfigPath(), new MarkerManager.Log4jMarker(Const.MOD_ID));
    }
}