package ninjaphenix.ninjatips.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import ninjaphenix.ninjatips.client.NinjaTipsClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ContainerScreen.class)
public abstract class ItemLinkingMixin extends Screen
{
    @Shadow protected Slot focusedSlot;

    private ItemLinkingMixin(Text name) { super(name); }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void chatItem(int key, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir)
    {
        if (MinecraftClient.getInstance().options.keyChat.matchesKey(key, scanCode) && Screen.hasShiftDown())
        {
            if (focusedSlot != null && focusedSlot.hasStack())
            {
                ItemStack stack = focusedSlot.getStack();
                NinjaTipsClient.chatItem(stack);
                cir.setReturnValue(true);
            }
        }
    }
}
