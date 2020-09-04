package ninjaphenix.expandedstorage.common.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface ServerScreenHandlerFactory<T extends AbstractScreenHandler<?>>
{
    T create(final int windowId, final BlockPos pos, final Container inventory, final Player player, final Component displayName);
}