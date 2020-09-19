package ninjaphenix.expandedstorage.common;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import ninjaphenix.chainmail.api.events.PlayerDisconnectCallback;
import ninjaphenix.expandedstorage.common.inventory.*;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ExpandedStorage implements ModInitializer
{
    public static final ExpandedStorage INSTANCE = new ExpandedStorage();
    private final HashMap<ResourceLocation, Tuple<ResourceLocation, Component>> screenMiscSettings = new HashMap<>();
    private final HashSet<ResourceLocation> declaredContainerTypes = new HashSet<>();
    private final HashMap<UUID, Consumer<ResourceLocation>> preferenceCallbacks = new HashMap<>();
    private final HashMap<UUID, ResourceLocation> playerPreferences = new HashMap<>();
    private final ImmutableMap<ResourceLocation, ServerScreenHandlerFactory<?>> handlerFactories =
            new ImmutableMap.Builder<ResourceLocation, ServerScreenHandlerFactory<?>>()
                    .put(Const.resloc("single"), SingleScreenHandler::new)
                    .put(Const.resloc("scrollable"), ScrollableScreenHandler::new)
                    .put(Const.resloc("paged"), PagedScreenHandler::new)
                    .build();

    private ExpandedStorage() { }

    @Override
    public void onInitialize()
    {
        ModContent.register();
        final Function<String, TranslatableComponent> nameFunc = (name) -> new TranslatableComponent(String.format("screen.%s.%s", Const.MOD_ID, name));
        declareContainerType(Const.resloc("single"), Const.resloc("textures/gui/single_button.png"), nameFunc.apply("single_screen"));
        declareContainerType(Const.resloc("scrollable"), Const.resloc("textures/gui/scrollable_button.png"), nameFunc.apply("scrollable_screen"));
        declareContainerType(Const.resloc("paged"), Const.resloc("textures/gui/paged_button.png"), nameFunc.apply("paged_screen"));
        ServerSidePacketRegistry.INSTANCE.register(Const.OPEN_SCREEN_SELECT, this::onReceiveOpenSelectScreenPacket);
        ServerSidePacketRegistry.INSTANCE.register(Const.SCREEN_SELECT, this::onReceivePlayerPreference);
        PlayerDisconnectCallback.EVENT.register(player -> setPlayerPreference(player, null));
    }

    public boolean isContainerTypeDeclared(final ResourceLocation containerTypeId) { return declaredContainerTypes.contains(containerTypeId); }

    public void setPlayerPreference(final Player player, final ResourceLocation containerTypeId)
    {
        final UUID uuid = player.getUUID();
        if (declaredContainerTypes.contains(containerTypeId))
        {
            playerPreferences.put(uuid, containerTypeId);
            if (preferenceCallbacks.containsKey(uuid)) { preferenceCallbacks.get(uuid).accept(containerTypeId); }
        }
        else
        {
            if (containerTypeId == null || !containerTypeId.equals(Const.resloc("auto"))) { playerPreferences.remove(uuid); }
            preferenceCallbacks.remove(uuid);
        }
    }

    public void openContainer(final Player player, final ExtendedScreenHandlerFactory handlerFactory)
    {
        final UUID uuid = player.getUUID();
        if (playerPreferences.containsKey(uuid) && handlerFactories.containsKey(playerPreferences.get(uuid)))
        {
            player.openMenu(handlerFactory);
        }
        else { openSelectScreen(player, (type) -> openContainer(player, handlerFactory)); }
    }

    public void openSelectScreen(final Player player, final Consumer<ResourceLocation> playerPreferenceCallback)
    {
        if (playerPreferenceCallback != null) { preferenceCallbacks.put(player.getUUID(), playerPreferenceCallback); }
        final FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeInt(declaredContainerTypes.size());
        declaredContainerTypes.forEach(buffer::writeResourceLocation);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Const.SCREEN_SELECT, buffer);
    }

    public void declareContainerType(final ResourceLocation containerTypeId, final ResourceLocation selectTextureId, final Component narrationMessage)
    {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
        {
            screenMiscSettings.put(containerTypeId, new Tuple<>(selectTextureId, narrationMessage));
        }
        declaredContainerTypes.add(containerTypeId);
    }

    public Tuple<ResourceLocation, Component> getScreenSettings(final ResourceLocation containerTypeId) { return screenMiscSettings.get(containerTypeId); }

    private void onReceivePlayerPreference(final PacketContext context, final FriendlyByteBuf buffer)
    {
        final Player player = context.getPlayer();
        final ResourceLocation containerType = buffer.readResourceLocation();
        context.getTaskQueue().submit(() -> INSTANCE.setPlayerPreference(player, containerType));
    }

    private void onReceiveOpenSelectScreenPacket(final PacketContext context, final FriendlyByteBuf rOpenBuffer)
    {
        final ServerPlayer sender = (ServerPlayer) context.getPlayer();
        final AbstractContainerMenu currentScreenHandler = sender.containerMenu;
        if (currentScreenHandler instanceof AbstractScreenHandler)
        {
            final AbstractScreenHandler<?> screenHandler = (AbstractScreenHandler<?>) currentScreenHandler;
            context.getTaskQueue().submit(() -> openSelectScreen(sender, (type) -> sender.openMenu(new ExtendedScreenHandlerFactory()
            {
                @Nullable
                @Override
                public AbstractContainerMenu createMenu(final int syncId, final Inventory inv, final Player player)
                {
                    return INSTANCE.getScreenHandler(syncId, screenHandler.ORIGIN, screenHandler.getInventory(),
                                                     player, screenHandler.getDisplayName());
                }

                @Override
                public Component getDisplayName() { return screenHandler.getDisplayName(); }

                @Override
                public void writeScreenOpeningData(final ServerPlayer player, final FriendlyByteBuf wOpenBuffer)
                {
                    wOpenBuffer.writeBlockPos(screenHandler.ORIGIN).writeInt(screenHandler.getInventory().getContainerSize());
                }
            })));
        }
        else { context.getTaskQueue().submit(() -> INSTANCE.openSelectScreen(sender, null)); }
    }

    public AbstractContainerMenu getScreenHandler(final int syncId, final BlockPos pos, final Container inventory, final Player player,
                                          final Component displayName)
    {
        final UUID uuid = player.getUUID();
        final ResourceLocation playerPreference;
        if (playerPreferences.containsKey(uuid) && handlerFactories.containsKey(playerPreference = playerPreferences.get(uuid)))
        {
            return handlerFactories.get(playerPreference).create(syncId, pos, inventory, player, displayName);
        }
        return null;
    }
}