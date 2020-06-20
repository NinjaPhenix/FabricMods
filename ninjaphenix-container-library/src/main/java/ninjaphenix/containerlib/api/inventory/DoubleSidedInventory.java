package ninjaphenix.containerlib.api.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class DoubleSidedInventory implements SidedInventory
{
    private final SidedInventory first;
    private final SidedInventory second;

    public DoubleSidedInventory(SidedInventory firstInventory, SidedInventory secondInventory)
    {
        if (firstInventory == null) { firstInventory = secondInventory; }
        if (secondInventory == null) { secondInventory = firstInventory; }
        first = firstInventory;
        second = secondInventory;
    }

    @Override
    public int[] getAvailableSlots(Direction direction)
    {
        int[] firstSlots = first.getAvailableSlots(direction);
        int[] secondSlots = second.getAvailableSlots(direction);
        int[] combined = new int[firstSlots.length + secondSlots.length];
        int index = 0;
        for (int slot : firstSlots) { combined[index++] = slot; }
        for (int slot : secondSlots) { combined[index++] = slot + first.size(); }
        return combined;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction direction)
    {
        if (slot >= first.size()) { return second.canInsert(slot - first.size(), stack, direction); }
        return first.canInsert(slot, stack, direction);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction direction)
    {
        if (slot >= first.size()) { return second.canExtract(slot - first.size(), stack, direction); }
        return first.canExtract(slot, stack, direction);
    }

    @Override
    public int size() { return first.size() + second.size(); }

    @Override
    public boolean isEmpty() { return first.isEmpty() && second.isEmpty(); }

    @Override
    public boolean canPlayerUse(PlayerEntity player) { return first.canPlayerUse(player) && second.canPlayerUse(player); }

    @Override
    public void clear() { first.clear(); second.clear(); }

    @Override
    public void markDirty() { first.markDirty(); second.markDirty(); }

    @Override
    public void onOpen(PlayerEntity player) { first.onOpen(player); second.onOpen(player); }

    @Override
    public void onClose(PlayerEntity player) { first.onClose(player); second.onClose(player); }

    public boolean isPart(SidedInventory inventory) { return first == inventory || second == inventory; }

    public int getMaxCountPerStack() { return first.getMaxCountPerStack(); }

    @Override
    public ItemStack getStack(int slot)
    {
        if (slot >= first.size()) { return second.getStack(slot - first.size()); }
        return first.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount)
    {
        if (slot >= first.size()) { return second.removeStack(slot - first.size(), amount); }
        return first.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot)
    {
        if (slot >= first.size()) { return second.removeStack(slot - first.size()); }
        return first.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack)
    {
        if (slot >= first.size()) { second.setStack(slot - first.size(), stack); }
        else { first.setStack(slot, stack); }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack)
    {
        if (slot >= first.size()) { return second.isValid(slot - first.size(), stack); }
        return first.isValid(slot, stack);
    }
}
