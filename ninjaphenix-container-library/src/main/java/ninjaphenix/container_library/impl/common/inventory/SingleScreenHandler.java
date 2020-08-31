package ninjaphenix.container_library.impl.common.inventory;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import ninjaphenix.container_library.api.common.inventory.AbstractScreenHandler;
import ninjaphenix.container_library.impl.BuiltinScreenTypes;
import ninjaphenix.container_library.impl.client.NewContainerLibraryClient;
import ninjaphenix.container_library.impl.common.Const;

public final class SingleScreenHandler extends AbstractScreenHandler<SingleScreenHandler.SingleScreenMeta>
{
    public SingleScreenHandler(final int syncId, final Inventory inventory, final PlayerEntity player, final SingleScreenMeta meta)
    {
        super(BuiltinScreenTypes.SINGLE_HANDLER_TYPE, syncId, inventory, player, meta);
        for (int i = 0; i < inventory.size(); i++)
        {
            final int x = i % SCREEN_META.WIDTH;
            final int y = (i - x) / SCREEN_META.WIDTH;
            addSlot(new Slot(inventory, i, x * 18 + 8, y * 18 + 18));
        }
        final Inventory playerInventory = player.inventory;
        final int left = (SCREEN_META.WIDTH * 18 + 14) / 2 - 80;
        final int top = 18 + 14 + (SCREEN_META.HEIGHT * 18);
        for (int x = 0; x < 9; x++)
        {
            for (int y = 0; y < 3; y++) { addSlot(new Slot(playerInventory, y * 9 + x + 9, left + 18 * x, top + y * 18)); }
        }
        for (int i = 0; i < 9; i++) { addSlot(new Slot(playerInventory, i, left + 18 * i, top + 58)); }
    }

   public static final class Factory implements ScreenHandlerRegistry.ExtendedClientHandlerFactory<SingleScreenHandler>
   {
       @Override
       public SingleScreenHandler create(final int syncId, final PlayerInventory playerInventory, final PacketByteBuf buffer)
       {
           if (buffer == null) { return null; }
           final int inventorySize = buffer.readInt();
           return new SingleScreenHandler(syncId, new SimpleInventory(inventorySize), playerInventory.player,
                                              NewContainerLibraryClient.INSTANCE.getScreenSize(Const.SCROLLABLE_CONTAINER, inventorySize));
       }
   }

    public static final class SingleScreenMeta extends AbstractScreenHandler.ScreenMeta
    {
        public final int BLANK_SLOTS;

        public SingleScreenMeta(final int width, final int height, final int totalSlots, final Identifier texture, final int textureWidth,
                                final int textureHeight)
        {
            super(width, height, totalSlots, texture, textureWidth, textureHeight);
            BLANK_SLOTS = width * height - totalSlots;
        }
    }
}