package ninjaphenix.containerlib.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import ninjaphenix.containerlib.api.inventory.AreaAwareSlotFactory;
import ninjaphenix.containerlib.api.inventory.SlotFactory;

import java.util.Arrays;

public class ScrollableContainer extends Container
{
    private final Text containerName;
    private final Inventory inventory;
    private final int rows;
    private final int realRows;
    @Environment(EnvType.CLIENT) private String searchTerm = "";
    @Environment(EnvType.CLIENT) private Integer[] unsortedToSortedSlotMap;

    /**
     * Creates a ScrollableContainer allowing for custom slots to be defined.
     *
     * @param syncId Sync ID
     * @param slotFactory The method which returns new (custom) slot objects.
     * @param playerInventory The player's inventory
     * @param inventory The block's inventory
     * @param containerName The name to be displayed inside of the container.
     */
    public ScrollableContainer(int syncId, AreaAwareSlotFactory slotFactory, PlayerInventory playerInventory, Inventory inventory, Text containerName)
    {
        super(null, syncId);
        this.inventory = inventory;
        this.containerName = containerName;
        realRows = inventory.getInvSize() / 9;
        rows = Math.min(realRows, 6);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) { unsortedToSortedSlotMap = new Integer[realRows * 9]; }
        int int_3 = (rows - 4) * 18;
        inventory.onInvOpen(playerInventory.player);
        for (int y = 0; y < realRows; ++y)
        {
            int yPos = -2000;
            if (y < rows) { yPos = 18 + y * 18; }
            for (int x = 0; x < 9; ++x)
            {
                int slot = x + 9 * y;
                if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) { unsortedToSortedSlotMap[slot] = slot; }
                addSlot(slotFactory.create(inventory, "CHEST", slot, 8 + x * 18, yPos));
            }
        }
        for (int y = 0; y < 3; ++y)
        {
            for (int x = 0; x < 9; ++x) { addSlot(slotFactory.create(playerInventory, "PLAYER", x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + int_3)); }
        }
        for (int x = 0; x < 9; ++x) { addSlot(slotFactory.create(playerInventory, "HOTBAR", x, 8 + x * 18, 161 + int_3)); }
    }

    /**
     * Creates a ScrollableContainer allowing for custom slots to be defined. (replaces all slots in container)
     *
     * @param syncId Sync ID
     * @param slotFactory The method which returns new (custom) slot objects.
     * @param playerInventory The player's inventory
     * @param inventory The block's inventory
     * @param containerName The name to be displayed inside of the container.
     */
    public ScrollableContainer(int syncId, SlotFactory slotFactory, PlayerInventory playerInventory, Inventory inventory, Text containerName)
    {
        this(syncId, ((inventory1, area, index, x, y) -> slotFactory.create(inventory1, index, x, y)), playerInventory, inventory, containerName);
    }

    /**
     * Creates a ScrollableContainer.
     *
     * @param syncId Sync ID
     * @param playerInventory The player's inventory
     * @param inventory The block's inventory
     * @param containerName The name to be displayed inside of the container.
     */
    public ScrollableContainer(int syncId, PlayerInventory playerInventory, Inventory inventory, Text containerName)
    {
        this(syncId, Slot::new, playerInventory, inventory, containerName);
    }

    public Inventory getInventory() { return inventory; }

    @Environment(EnvType.CLIENT)
    public int getRows() { return realRows; }

    @Environment(EnvType.CLIENT)
    public Text getDisplayName() { return containerName; }

    @Override
    public boolean canUse(PlayerEntity player) { return inventory.canPlayerUseInv(player); }

    @Override
    public void close(PlayerEntity player)
    {
        super.close(player);
        inventory.onInvClose(player);
    }

    @Environment(EnvType.CLIENT)
    public void setSearchTerm(String term)
    {
        searchTerm = term.toLowerCase();
        updateSlotPositions(0, true);
    }

    @Environment(EnvType.CLIENT)
    public void updateSlotPositions(int offset, boolean termChanged)
    {
        int index = 0;
        if (termChanged && !searchTerm.equals("")) { Arrays.sort(unsortedToSortedSlotMap, this::compare); }
        else if (termChanged) { Arrays.sort(unsortedToSortedSlotMap); }
        for (Integer slotID : unsortedToSortedSlotMap)
        {
            final Slot slot = slots.get(slotID);
            final int y = (index / 9) - offset;

            slot.xPosition = 8 + 18 * (index % 9);
            slot.yPosition = (y >= rows || y < 0) ? -2000 : 18 + 18 * y;
            index++;
        }
    }

    private int compare(Integer a, Integer b)
    {
        if (a == null || b == null) { return 0; }
        final ItemStack stack_a = slots.get(a).getStack();
        final ItemStack stack_b = slots.get(b).getStack();
        if (stack_a.isEmpty() && !stack_b.isEmpty()) { return 1; }
        if (!stack_a.isEmpty() && stack_b.isEmpty()) { return -1; }
        if (stack_a.isEmpty()) { return 0; /* && stack_b.isEmpty() -- unneeded*/ }
        final boolean stack_a_matches = stack_a.getName().getString().toLowerCase().contains(searchTerm);
        final boolean stack_b_matches = stack_b.getName().getString().toLowerCase().contains(searchTerm);
        return stack_a_matches && stack_b_matches ? 0 : stack_b_matches ? 1 : -1;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int slotIndex)
    {
        ItemStack stack = ItemStack.EMPTY;
        final Slot slot = slots.get(slotIndex);
        if (slot != null && slot.hasStack())
        {
            final ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotIndex < inventory.getInvSize()) { if (!insertItem(slotStack, inventory.getInvSize(), slots.size(), true)) { return ItemStack.EMPTY; } }
            else if (!insertItem(slotStack, 0, inventory.getInvSize(), false)) { return ItemStack.EMPTY; }
            if (slotStack.isEmpty()) { slot.setStack(ItemStack.EMPTY); }
            else { slot.markDirty(); }
        }
        return stack;
    }
}
