package ninjaphenix.containerlib.inventory;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a method which creates (custom) Slot objects.
 *
 * @author NinjaPhenix, i509VCB
 * @since 0.1.2
 * @deprecated {@link ninjaphenix.containerlib.api.inventory.SlotFactory}
 */
@Deprecated
@FunctionalInterface
@ApiStatus.ScheduledForRemoval(inVersion = "1.16")
public interface SlotFactory extends ninjaphenix.containerlib.api.inventory.SlotFactory {}
