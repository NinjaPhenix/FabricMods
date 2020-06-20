package ninjaphenix.containerlib.impl.client;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import ninjaphenix.containerlib.impl.client.screen.ScrollableScreen;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public final class ContainerLibREIPlugin implements REIPluginV0
{
    @Override
    public Identifier getPluginIdentifier() { return new Identifier("ninjaphenix-container-lib", "reiplugin"); }

    @Override
    public void registerBounds(DisplayHelper displayHelper)
    {
        BaseBoundsHandler.getInstance().registerExclusionZones(ScrollableScreen.class, () ->
        {
            ScrollableScreen<?> self = (ScrollableScreen<?>) MinecraftClient.getInstance().currentScreen;
            ArrayList<Rectangle> rv = new ArrayList<>();
            if (self != null) /* Makes IDE shut up. */ { self.getReiRectangle().ifPresent(rv::add); }
            return rv;
        });
    }
}
