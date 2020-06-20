package ninjaphenix.containerlib.api.inventory;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import ninjaphenix.containerlib.api.screen.ScreenMeta;

public abstract class AbstractContainer<T extends ScreenMeta> extends Container
{
    public final BlockPos ORIGIN;
    protected final Inventory INVENTORY;
    public final PlayerInventory PLAYER_INVENTORY;
    private final Text DISPLAY_NAME;
    public final T SCREEN_META;

    public AbstractContainer(ContainerType<?> type, int syncId, BlockPos pos, Inventory inventory, PlayerEntity player, Text displayName, T meta)
    {
        super(type, syncId);
        ORIGIN = pos;
        INVENTORY = inventory;
        PLAYER_INVENTORY = player.inventory;
        DISPLAY_NAME = displayName;
        SCREEN_META = meta;
        inventory.onInvOpen(player);
    }

    @Override
    public boolean canUse(PlayerEntity player) { return INVENTORY.canPlayerUseInv(player); }

    public Text getDisplayName() { return DISPLAY_NAME.deepCopy(); }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int slotIndex)
    {
        ItemStack stack = ItemStack.EMPTY;
        final Slot slot = slots.get(slotIndex);
        if (slot != null && slot.hasStack())
        {
            final ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotIndex < INVENTORY.getInvSize()) { if (!insertItem(slotStack, INVENTORY.getInvSize(), slots.size(), true)) { return ItemStack.EMPTY; } }
            else if (!insertItem(slotStack, 0, INVENTORY.getInvSize(), false)) { return ItemStack.EMPTY; }
            if (slotStack.isEmpty()) { slot.setStack(ItemStack.EMPTY); }
            else { slot.markDirty(); }
        }
        return stack;
    }

    @Override
    public void close(PlayerEntity player)
    {
        super.close(player);
        INVENTORY.onInvClose(player);
    }

    public Inventory getInventory() { return INVENTORY; }
}
