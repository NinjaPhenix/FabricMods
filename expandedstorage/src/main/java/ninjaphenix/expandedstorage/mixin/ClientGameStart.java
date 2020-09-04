package ninjaphenix.expandedstorage.mixin;

import net.minecraft.client.Game;
import ninjaphenix.expandedstorage.client.ExpandedStorageClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Game.class)
public class ClientGameStart
{
    @Inject(method = "onStartGameSession()V", at = @At("TAIL"))
    private void expandedstorage_onStartGameSession(final CallbackInfo ci) { ExpandedStorageClient.sendPreferencesToServer(); }
}