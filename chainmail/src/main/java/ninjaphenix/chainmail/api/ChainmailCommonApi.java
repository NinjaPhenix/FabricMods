package ninjaphenix.chainmail.api;

import ninjaphenix.chainmail.impl.ChainmailCommonImpl;

import java.util.function.IntFunction;
import net.minecraft.world.item.CreativeModeTab;

public interface ChainmailCommonApi
{
    ChainmailCommonApi INSTANCE = ChainmailCommonImpl.INSTANCE;
    <T extends CreativeModeTab> T registerItemGroup(final IntFunction<T> itemGroup);
}