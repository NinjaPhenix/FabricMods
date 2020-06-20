package ninjaphenix.containerlib.inventory;

import net.minecraft.inventory.SidedInventory;
import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated {@link ninjaphenix.containerlib.api.inventory.DoubleSidedInventory}
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.16")
public class DoubleSidedInventory extends ninjaphenix.containerlib.api.inventory.DoubleSidedInventory
{
    public DoubleSidedInventory(SidedInventory firstInventory, SidedInventory secondInventory) { super(firstInventory, secondInventory); }
}