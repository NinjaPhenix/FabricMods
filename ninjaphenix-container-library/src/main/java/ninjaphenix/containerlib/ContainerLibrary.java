package ninjaphenix.containerlib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ninjaphenix.chainmail.api.events.PlayerDisconnectCallback;
import ninjaphenix.containerlib.api.Constants;
import ninjaphenix.containerlib.api.ContainerLibraryAPI;
import ninjaphenix.containerlib.api.ContainerLibraryExtension;
import ninjaphenix.containerlib.api.inventory.AbstractContainer;
import ninjaphenix.containerlib.api.inventory.AreaAwareSlotFactory;
import ninjaphenix.containerlib.impl.ContainerLibraryImpl;
import ninjaphenix.containerlib.impl.inventory.PagedScreenHandler;
import ninjaphenix.containerlib.impl.inventory.ScrollableScreenHandler;
import ninjaphenix.containerlib.impl.inventory.SingleScreenHandler;

import java.util.List;
import java.util.function.Function;

import static ninjaphenix.containerlib.api.Constants.*;

public final class ContainerLibrary implements ModInitializer
{
    public static final ContainerLibrary INSTANCE = new ContainerLibrary();
    private static final ContainerLibraryImpl IMPL = ContainerLibraryImpl.INSTANCE;

    private ContainerLibrary() {}

    /**
     * Open's a modded container which block implements InventoryProvider.
     *
     * @param player The Player who attempted to open the container.
     * @param pos The block pos of the container.
     * @param containerName The text that should be displayed as the container name.
     * @since 0.0.1
     * @deprecated {@link ContainerLibraryAPI#openContainer}
     */
    @Deprecated
    public static void openContainer(PlayerEntity player, BlockPos pos, Text containerName) { IMPL.openContainer(player, pos, containerName); }

    @Override
    public void onInitialize()
    {
        List<ContainerLibraryExtension> extensions = FabricLoader.getInstance().getEntrypoints(Constants.ENTRY_POINT_ID, ContainerLibraryExtension.class);
        extensions.forEach(ContainerLibraryExtension::declareScreenSizeCallbacks);
        extensions.forEach(ContainerLibraryExtension::declareScreenSizes);
        ContainerProviderRegistry.INSTANCE.registerFactory(SINGLE_CONTAINER, getContainerFactory(SingleScreenHandler::new));
        ContainerProviderRegistry.INSTANCE.registerFactory(Constants.PAGED_CONTAINER, getContainerFactory(PagedScreenHandler::new));
        ContainerProviderRegistry.INSTANCE.registerFactory(Constants.SCROLLABLE_CONTAINER, getContainerFactory(ScrollableScreenHandler::new));
        final Function<String, TranslatableText> nameFunc = (name) -> new TranslatableText(String.format("screen.%s.%s", LIBRARY_ID, name));
        IMPL.declareContainerType(SINGLE_CONTAINER, Constants.id("textures/gui/single_button.png"), nameFunc.apply("single_screen_type"));
        IMPL.declareContainerType(Constants.SCROLLABLE_CONTAINER, Constants.id("textures/gui/scrollable_button.png"), nameFunc.apply("scrollable_screen_type"));
        IMPL.declareContainerType(Constants.PAGED_CONTAINER, Constants.id("textures/gui/paged_button.png"), nameFunc.apply("paged_screen_type"));
        ServerSidePacketRegistry.INSTANCE.register(Constants.OPEN_SCREEN_SELECT, this::onReceiveOpenSelectScreenPacket);
        ServerSidePacketRegistry.INSTANCE.register(Constants.SCREEN_SELECT, this::onReceivePlayerPreference);

        PlayerDisconnectCallback.EVENT.register(player -> IMPL.setPlayerPreference(player, null));
    }

    private void onReceivePlayerPreference(PacketContext context, PacketByteBuf buffer)
    {
        context.getTaskQueue().submitAndJoin(() -> ContainerLibraryImpl.INSTANCE.setPlayerPreference(context.getPlayer(), buffer.readIdentifier()));
    }

    private void onReceiveOpenSelectScreenPacket(final PacketContext context, final PacketByteBuf buffer)
    {
        final ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
        final ScreenHandler container = player.currentScreenHandler;
        if (container instanceof AbstractContainer)
        {
            final AbstractContainer<?> abstractContainer = (AbstractContainer<?>) container;
            IMPL.openSelectScreen(player, (type) -> IMPL.openContainer(player, abstractContainer.ORIGIN, abstractContainer.getDisplayName())
            );
        }
        else
        {
            ContainerLibraryImpl.INSTANCE.openSelectScreen(player, null);
        }
    }

    private interface containerConstructor<T extends ScreenHandler>
    {
        T create(ScreenHandlerType<T> type, int syncId, BlockPos pos, Inventory inventory,
                PlayerEntity player, Text containerName, AreaAwareSlotFactory slotFactory);
    }

    private <T extends ScreenHandler> ContainerFactory<T> getContainerFactory(containerConstructor<T> newMethod)
    {
        return (syncId, identifier, player, buffer) -> {
            final BlockPos pos = buffer.readBlockPos();
            final Text name = buffer.readText();
            final World world = player.getEntityWorld();
            final BlockState state = world.getBlockState(pos);
            final Block block = state.getBlock();
            if (block instanceof InventoryProvider)
            {
                return newMethod.create(null, syncId, pos, ((InventoryProvider) block).getInventory(state, world, pos), player, name,
                        (inventory, area, index, x, y) -> new Slot(inventory, index, x, y));
            }
            return null;
        };
    }
}