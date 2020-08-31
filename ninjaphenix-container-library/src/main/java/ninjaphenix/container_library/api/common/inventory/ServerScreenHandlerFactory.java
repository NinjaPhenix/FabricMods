package ninjaphenix.container_library.api.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.Text;
import ninjaphenix.container_library.api.common.inventory.AbstractScreenHandler.ScreenMeta;

@FunctionalInterface
public interface ServerScreenHandlerFactory<T extends AbstractScreenHandler<R>, R extends ScreenMeta>
{
    T create(final int windowId, final Inventory inventory, final PlayerEntity player, final Text displayName, final R meta);
}