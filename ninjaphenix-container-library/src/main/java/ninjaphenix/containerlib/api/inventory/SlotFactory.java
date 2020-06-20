package ninjaphenix.containerlib.api.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

/**
 * Represents a method which creates (custom) Slot objects.
 *
 * @author NinjaPhenix, i509VCB
 * @apiNote Consider using {@link AreaAwareSlotFactory} instead.
 * @since 0.1.2
 */
@FunctionalInterface
public interface SlotFactory
{
    /**
     * @param inventory The inventory the slot is in.
     * @param index The index of the slot
     * @param x The x position of the slot inside of the container
     * @param y The y position of the slot inside of the container
     * @return A new (custom) Slot object
     */
    Slot create(Inventory inventory, int index, int x, int y);
}
