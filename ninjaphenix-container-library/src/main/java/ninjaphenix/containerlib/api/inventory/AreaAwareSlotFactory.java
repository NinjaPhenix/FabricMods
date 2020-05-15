package ninjaphenix.containerlib.api.inventory;

import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;

/**
 * Represents a method which creates (custom) Slot objects.
 *
 * @author NinjaPhenix, i509VCB
 * @since 0.1.2
 */
@FunctionalInterface
public interface AreaAwareSlotFactory
{
    /**
     * @param inventory The inventory the slot is in.
     * @param area The area where the slot resides. Refer to the container's constructor for possible values.
     * @param index The index of the slot
     * @param x The x position of the slot inside of the container
     * @param y The y position of the slot inside of the container
     * @return A new (custom) Slot object
     */
    Slot create(Inventory inventory, String area, int index, int x, int y);
}
