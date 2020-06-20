package ninjaphenix.creativebuttonmover.mixins;

import net.fabricmc.fabric.impl.item.group.CreativeGuiExtensions;
import net.fabricmc.fabric.impl.item.group.FabricCreativeGuiComponents;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import ninjaphenix.creativebuttonmover.client.Config;
import ninjaphenix.creativebuttonmover.client.gui.PageSwitchWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CreativeInventoryScreen.class, priority = Integer.MAX_VALUE)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler>
{
    private final CreativeGuiExtensions ext = (CreativeGuiExtensions) this;
    private PageSwitchWidget prevButton;
    private PageSwitchWidget nextButton;

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler container, PlayerInventory playerInventory, Text text)
    {
        super(container, playerInventory, text);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo ci)
    {
        children.removeIf(el -> el instanceof FabricCreativeGuiComponents.ItemGroupButtonWidget);
        buttons.removeIf(el -> el instanceof FabricCreativeGuiComponents.ItemGroupButtonWidget);
        Config.Button prev = Config.INSTANCE.PrevButton;
        Config.Button next = Config.INSTANCE.NextButton;
        prevButton = new PageSwitchWidget(x + prev.x, y + prev.y, prev.width, prev.height, prev.texture, (button) -> ext.fabric_previousPage());
        nextButton = new PageSwitchWidget(x + next.x, y + next.y, next.width, next.height, next.texture, (button) -> ext.fabric_nextPage());
        addButton(prevButton);
        addButton(nextButton);
        prevButton.setFocusedExternal(false);
        nextButton.setFocusedExternal(false);
        setFocused(null);
        tick();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci)
    {
        final boolean prevButtonVisible = ext.fabric_isButtonVisible(FabricCreativeGuiComponents.Type.PREVIOUS);
        final boolean nextButtonVisible = ext.fabric_isButtonVisible(FabricCreativeGuiComponents.Type.NEXT);
        prevButton.visible = prevButtonVisible;
        nextButton.visible = nextButtonVisible;
        prevButton.active = ext.fabric_isButtonEnabled(FabricCreativeGuiComponents.Type.PREVIOUS) && prevButtonVisible;
        if (!prevButton.active && prevButton.isFocused()) { prevButton.changeFocus(false); }
        nextButton.active = ext.fabric_isButtonEnabled(FabricCreativeGuiComponents.Type.NEXT) && nextButtonVisible;
        if (!nextButton.active && nextButton.isFocused()) { nextButton.changeFocus(false); }
    }
}