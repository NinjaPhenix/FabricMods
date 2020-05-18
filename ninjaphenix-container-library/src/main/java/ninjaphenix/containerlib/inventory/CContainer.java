package ninjaphenix.containerlib.inventory;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import ninjaphenix.containerlib.ScreenSizing;

import java.util.HashMap;

public class CContainer extends Container
{
    private static final HashMap<Integer, ScreenSizing> SIZES;

    static
    {
        SIZES = new HashMap<>();
        SIZES.put(27, new ScreenSizing.Builder().fixedParams(9, 3, null).build());
        SIZES.put(54, new ScreenSizing.Builder().fixedParams(9, 6, null).build());
        SIZES.put(81, new ScreenSizing.Builder().fixedParams(9, 9, null).build());
        SIZES.put(108, new ScreenSizing.Builder().fixedParams(12, 9, null).scrollParams(9, 6, null, 12).pagedParams(9, 6, null, 2).build());
        SIZES.put(162, new ScreenSizing.Builder().fixedParams(18, 9, null).scrollParams(9, 6, null, 18).pagedParams(9, 6, null, 3).build());
        SIZES.put(216, new ScreenSizing.Builder().fixedParams(24, 9, null).scrollParams(12, 6, null, 18).pagedParams(12, 6, null, 3).build());
        /*

        def Factors(x: int):
	        r = []
	        for i in range(1, math.ceil(x / 2)):
		        if (x / i).is_integer():
			        r.append((i, int(x/i)))
	        return r

         */
    }

    public final PlayerInventory PLAYER_INVENTORY;
    public final ScreenSizing SIZING;
    private final Text DISPLAY_NAME;
    private final Inventory INVENTORY;

    public CContainer(ContainerType<?> type, int syncId, Inventory inventory, PlayerEntity player, Text displayName)
    {
        super(type, syncId);
        INVENTORY = inventory;
        PLAYER_INVENTORY = player.inventory;
        DISPLAY_NAME = displayName;
        SIZING = SIZES.get(inventory.getInvSize());
        for (int i = 0; i < inventory.getInvSize(); i++) { this.addSlot(new Slot(inventory, i, 0, 0)); }
        for (int i = 0; i < 36; i++) { this.addSlot(new Slot(PLAYER_INVENTORY, i, 0, 0)); }
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
}
