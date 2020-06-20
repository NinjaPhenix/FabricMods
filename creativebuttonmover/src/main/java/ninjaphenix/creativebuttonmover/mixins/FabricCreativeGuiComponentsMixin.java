package ninjaphenix.creativebuttonmover.mixins;

import net.fabricmc.fabric.impl.item.group.CreativeGuiExtensions;
import net.fabricmc.fabric.impl.item.group.FabricCreativeGuiComponents;
import ninjaphenix.creativebuttonmover.client.CreativeButtonMover;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FabricCreativeGuiComponents.ItemGroupButtonWidget.class)
public class FabricCreativeGuiComponentsMixin
{
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(int x, int y, FabricCreativeGuiComponents.Type type, CreativeGuiExtensions extensions, CallbackInfo ci)
    {
        FabricCreativeGuiComponents.ItemGroupButtonWidget widget = (FabricCreativeGuiComponents.ItemGroupButtonWidget) (Object) this;
        widget.x = x - 116 + CreativeButtonMover.x;
        widget.y = y + 10 + CreativeButtonMover.y;
    }
}