package ninjaphenix.expandedstorage.client;

import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.client.screen.AbstractScreen;
import ninjaphenix.expandedstorage.common.Const;
import java.util.Collections;

public class ExpandedStorageReiPlugin implements REIPluginV0
{
    @Override
    public ResourceLocation getPluginIdentifier() { return Const.resloc("rei_plugin"); }

    @Override
    public void registerBounds(final DisplayHelper displayHelper)
    {
        BaseBoundsHandler.getInstance().registerExclusionZones(AbstractScreen.class, () ->
        {
            final Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof AbstractScreen) { return ((AbstractScreen<?,?>) screen).getReiRectangles(); }
            return Collections.emptyList();
        });
    }
}
