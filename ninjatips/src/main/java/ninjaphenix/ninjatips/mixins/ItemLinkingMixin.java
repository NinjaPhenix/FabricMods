package ninjaphenix.ninjatips.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import ninjaphenix.ninjatips.client.NinjaTipsClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(AbstractContainerScreen.class)
public abstract class ItemLinkingMixin extends Screen
{
    @Shadow protected Slot hoveredSlot;

    private ItemLinkingMixin(Component name) { super(name); }

    @Inject(method = "keyPressed(III)Z", at = @At("HEAD"), cancellable = true)
    public void chatItem(int key, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir)
    {
        if (Minecraft.getInstance().options.keyChat.matches(key, scanCode) && Screen.hasShiftDown())
        {
            if (hoveredSlot != null && hoveredSlot.hasItem())
            {
                ItemStack stack = hoveredSlot.getItem();
                NinjaTipsClient.chatItem(stack);
                cir.setReturnValue(true);
            }
        }
    }
}