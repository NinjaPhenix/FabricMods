package ninjaphenix.container_library.impl.common.inventory;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import ninjaphenix.container_library.api.common.inventory.AbstractScreenHandler;
import ninjaphenix.container_library.impl.BuiltinScreenTypes;
import ninjaphenix.container_library.impl.client.NewContainerLibraryClient;
import ninjaphenix.container_library.impl.common.Const;

public final class PagedScreenHandler extends AbstractScreenHandler<PagedScreenHandler.PagedScreenMeta>
{
    public PagedScreenHandler(final int syncId, final Inventory inventory, final PlayerEntity player, final PagedScreenMeta meta)
    {
        super(BuiltinScreenTypes.PAGED_HANDLER_TYPE, syncId, inventory, player, meta);
        resetSlotPositions(true);
        final Inventory playerInventory = player.inventory;
        final int left = (SCREEN_META.WIDTH * 18 + 14) / 2 - 80;
        final int top = 18 + 14 + (SCREEN_META.HEIGHT * 18);
        for (int x = 0; x < 9; x++)
        {
            for (int y = 0; y < 3; y++) { addSlot(new Slot(playerInventory, y * 9 + x + 9, left + 18 * x, top + y * 18)); }
        }
        for (int i = 0; i < 9; i++) { addSlot(new Slot(playerInventory, i, left + 18 * i, top + 58)); }
    }

    public void resetSlotPositions(final boolean createSlots)
    {
        for (int i = 0; i < INVENTORY.size(); i++)
        {
            final int x = i % SCREEN_META.WIDTH;
            int y = MathHelper.ceil((((double) (i - x)) / SCREEN_META.WIDTH));
            if (y >= SCREEN_META.HEIGHT) { y = (18 * (y % SCREEN_META.HEIGHT)) - 2000; }
            else {y = y * 18;}
            if (createSlots) { addSlot(new Slot(INVENTORY, i, x * 18 + 8, y + 18)); }
            else { slots.get(i).y = y + 18; }
        }
    }

    public void moveSlotRange(final int min, final int max, final int yChange)
    {
        for (int i = min; i < max; i++) { slots.get(i).y += yChange; }
    }

    public static final class Factory implements ScreenHandlerRegistry.ExtendedClientHandlerFactory<PagedScreenHandler>
    {
        @Override
        public PagedScreenHandler create(final int syncId, final PlayerInventory playerInventory, final PacketByteBuf buffer)
        {
            if (buffer == null) { return null; }
            final int inventorySize = buffer.readInt();
            return new PagedScreenHandler(syncId, new SimpleInventory(inventorySize), playerInventory.player,
                                          NewContainerLibraryClient.INSTANCE.getScreenSize(Const.PAGED_CONTAINER, inventorySize));
        }
    }

    public static final class PagedScreenMeta extends AbstractScreenHandler.ScreenMeta
    {
        public final int BLANK_SLOTS, PAGES;

        public PagedScreenMeta(final int width, final int height, final int pages, final int totalSlots, final Identifier texture,
                               final int textureWidth, final int textureHeight)
        {
            super(width, height, totalSlots, texture, textureWidth, textureHeight);
            PAGES = pages;
            BLANK_SLOTS = pages * width * height - totalSlots;
        }
    }
}