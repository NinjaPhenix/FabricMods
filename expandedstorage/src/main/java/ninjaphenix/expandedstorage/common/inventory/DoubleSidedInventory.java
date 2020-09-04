package ninjaphenix.expandedstorage.common.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class DoubleSidedInventory implements WorldlyContainer
{
    private final WorldlyContainer first;
    private final WorldlyContainer second;

    public DoubleSidedInventory(final WorldlyContainer first, final WorldlyContainer second)
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public int[] getSlotsForFace(final Direction direction)
    {
        final int firstSize = first.getContainerSize();
        final int[] firstSlots = first.getSlotsForFace(direction);
        final int[] secondSlots = second.getSlotsForFace(direction);
        final int[] combined = new int[firstSlots.length + secondSlots.length];
        int index = 0;
        for (final int slot : firstSlots) { combined[index++] = slot; }
        for (final int slot : secondSlots) { combined[index++] = slot + firstSize; }
        return combined;
    }

    @Override
    public boolean canPlaceItemThroughFace(final int slot, final ItemStack stack, final Direction direction)
    {
        if (slot >= first.getContainerSize()) { return second.canPlaceItemThroughFace(slot - first.getContainerSize(), stack, direction); }
        return first.canPlaceItemThroughFace(slot, stack, direction);
    }

    @Override
    public boolean canTakeItemThroughFace(final int slot, final ItemStack stack, final Direction direction)
    {
        if (slot >= first.getContainerSize()) { return second.canTakeItemThroughFace(slot - first.getContainerSize(), stack, direction); }
        return first.canTakeItemThroughFace(slot, stack, direction);
    }

    @Override
    public int getContainerSize() { return first.getContainerSize() + second.getContainerSize(); }

    @Override
    public boolean isEmpty() { return first.isEmpty() && second.isEmpty(); }

    @Override
    public boolean stillValid(final Player player) { return first.stillValid(player) && second.stillValid(player); }

    @Override
    public void clearContent()
    {
        first.clearContent();
        second.clearContent();
    }

    @Override
    public void setChanged()
    {
        first.setChanged();
        second.setChanged();
    }

    @Override
    public void startOpen(final Player player)
    {
        first.startOpen(player);
        second.startOpen(player);
    }

    @Override
    public void stopOpen(final Player player)
    {
        first.stopOpen(player);
        second.stopOpen(player);
    }

    public boolean isPart(final WorldlyContainer inventory) { return first == inventory || second == inventory; }

    public int getMaxStackSize() { return first.getMaxStackSize(); }

    @Override
    public ItemStack getItem(final int slot)
    {
        if (slot >= first.getContainerSize()) { return second.getItem(slot - first.getContainerSize()); }
        return first.getItem(slot);
    }

    @Override
    public ItemStack removeItem(final int slot, final int amount)
    {
        if (slot >= first.getContainerSize()) { return second.removeItem(slot - first.getContainerSize(), amount); }
        return first.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(final int slot)
    {
        if (slot >= first.getContainerSize()) { return second.removeItemNoUpdate(slot - first.getContainerSize()); }
        return first.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(final int slot, final ItemStack stack)
    {
        if (slot >= first.getContainerSize()) { second.setItem(slot - first.getContainerSize(), stack); }
        else { first.setItem(slot, stack); }
    }

    @Override
    public boolean canPlaceItem(final int slot, final ItemStack stack)
    {
        if (slot >= first.getContainerSize()) { return second.canPlaceItem(slot - first.getContainerSize(), stack); }
        return first.canPlaceItem(slot, stack);
    }
}