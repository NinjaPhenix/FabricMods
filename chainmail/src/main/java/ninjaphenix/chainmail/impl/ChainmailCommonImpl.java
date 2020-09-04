package ninjaphenix.chainmail.impl;

import ninjaphenix.chainmail.api.ChainmailCommonApi;
import ninjaphenix.chainmail.impl.mixinhelpers.ItemGroupArrayExpander;

import java.util.function.IntFunction;
import net.minecraft.world.item.CreativeModeTab;

public class ChainmailCommonImpl implements ChainmailCommonApi
{
    public static final ChainmailCommonApi INSTANCE = new ChainmailCommonImpl();

    @Override
    public <T extends CreativeModeTab> T registerItemGroup(final IntFunction<T> itemGroup)
    {
        final CreativeModeTab buildingBlocks = CreativeModeTab.TAB_BUILDING_BLOCKS;
        if (buildingBlocks instanceof ItemGroupArrayExpander)
        {
            final int index = ((ItemGroupArrayExpander) buildingBlocks).chainmail_expandArraySize();
            final T group = itemGroup.apply(index);
            CreativeModeTab.TABS[index] = group;
            return group;
        }
        throw new RuntimeException("[chainmail-common-api] Item group mixin failed to apply. Unable to register item groups.");
    }
}