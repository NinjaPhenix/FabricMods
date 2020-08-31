package ninjaphenix.container_library.api.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import ninjaphenix.container_library.impl.common.Const;

public abstract class AbstractScreenHandler<T extends AbstractScreenHandler.ScreenMeta> extends ScreenHandler
{
    public final T SCREEN_META;
    protected final Inventory INVENTORY;

    public AbstractScreenHandler(final ScreenHandlerType<?> type, final int syncId, final Inventory inventory,
                                 final PlayerEntity player, final T meta)
    {
        super(type, syncId);
        INVENTORY = inventory;
        SCREEN_META = meta;
        inventory.onOpen(player);
    }

    @Override
    public boolean canUse(final PlayerEntity player) { return INVENTORY.canPlayerUse(player); }

    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int slotIndex)
    {
        ItemStack stack = ItemStack.EMPTY;
        final Slot slot = slots.get(slotIndex);
        if (slot != null && slot.hasStack())
        {
            final ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotIndex < INVENTORY.size())
            {
                if (!insertItem(slotStack, INVENTORY.size(), slots.size(), true)) { return ItemStack.EMPTY; }
            }
            else if (!insertItem(slotStack, 0, INVENTORY.size(), false)) { return ItemStack.EMPTY; }
            if (slotStack.isEmpty()) { slot.setStack(ItemStack.EMPTY); }
            else { slot.markDirty(); }
        }
        return stack;
    }

    @Override
    public void close(final PlayerEntity player)
    {
        super.close(player);
        INVENTORY.onClose(player);
    }

    public Inventory getInventory() { return INVENTORY; }

    public static Identifier getTexture(final String type, final int width, final int height)
    {
        return Const.id(String.format("textures/gui/container/%s_%d_%d.png", type, width, height));
    }

    // todo: remove texture info from this class.
    public static abstract class ScreenMeta
    {
        public final int WIDTH, HEIGHT, TOTAL_SLOTS, TEXTURE_WIDTH, TEXTURE_HEIGHT;
        public final Identifier TEXTURE;

        protected ScreenMeta(final int width, final int height, final int totalSlots, final Identifier texture, final int textureWidth,
                             final int textureHeight)
        {
            WIDTH = width;
            HEIGHT = height;
            TOTAL_SLOTS = totalSlots;
            TEXTURE = texture;
            TEXTURE_WIDTH = textureWidth;
            TEXTURE_HEIGHT = textureHeight;
        }

        public void serialize(final PacketByteBuf buffer)
        {
            buffer.writeInt(WIDTH);
            buffer.writeInt(HEIGHT);
            buffer.writeInt(TOTAL_SLOTS);
        }
    }
}