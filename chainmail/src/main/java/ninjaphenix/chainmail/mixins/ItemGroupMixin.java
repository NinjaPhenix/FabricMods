package ninjaphenix.chainmail.mixins;

import net.minecraft.world.item.CreativeModeTab;
import ninjaphenix.chainmail.impl.mixinhelpers.ItemGroupArrayExpander;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreativeModeTab.class)
public class ItemGroupMixin implements ItemGroupArrayExpander
{
    @Shadow public static CreativeModeTab[] TABS;

    @Override
    public int chainmail_expandArraySize()
    {
        final CreativeModeTab[] temp = TABS.clone();
        TABS = new CreativeModeTab[temp.length + 1];
        System.arraycopy(temp, 0, TABS, 0, temp.length);
        return temp.length;
    }
}