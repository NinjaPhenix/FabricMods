package ninjaphenix.chainmail.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import java.util.Arrays;

/**
 * @since 0.0.1
 */
@FunctionalInterface
public interface PlayerDisconnectCallback
{
    Event<PlayerDisconnectCallback> EVENT = EventFactory.createArrayBacked(PlayerDisconnectCallback.class, listeners ->
            (player) -> Arrays.stream(listeners).forEach(listener -> listener.onPlayerDisconnected(player)));

    void onPlayerDisconnected(ServerPlayer player);
}