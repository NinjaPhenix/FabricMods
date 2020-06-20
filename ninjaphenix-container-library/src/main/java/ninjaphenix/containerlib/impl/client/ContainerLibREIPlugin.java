package ninjaphenix.containerlib.impl.client;

import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import ninjaphenix.containerlib.impl.client.screen.ScrollableScreen;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public final class ContainerLibREIPlugin implements REIPluginV0
{
    @Override
    public SemanticVersion getMinimumVersion() throws VersionParsingException { return SemanticVersion.parse("3.0-pre"); }

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
