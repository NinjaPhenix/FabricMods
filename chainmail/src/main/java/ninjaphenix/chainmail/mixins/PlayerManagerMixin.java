package ninjaphenix.chainmail.mixins;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import ninjaphenix.chainmail.api.events.PlayerConnectCallback;
import ninjaphenix.chainmail.api.events.PlayerDisconnectCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerManagerMixin
{
    @Inject(method = "placeNewPlayer(Lnet/minecraft/network/Connection;Lnet/minecraft/server/level/ServerPlayer;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;addNewPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void chainmail_onPlayerConnected(final Connection connection, final ServerPlayer player, final CallbackInfo ci)
    {
        PlayerConnectCallback.EVENT.invoker().onPlayerConnected(player);
    }

    @Inject(method = "remove(Lnet/minecraft/server/level/ServerPlayer;)V", at = @At("HEAD"))
    private void chainmail_onPlayerDisconnected(final ServerPlayer player, final CallbackInfo ci)
    {
        PlayerDisconnectCallback.EVENT.invoker().onPlayerDisconnected(player);
    }
}