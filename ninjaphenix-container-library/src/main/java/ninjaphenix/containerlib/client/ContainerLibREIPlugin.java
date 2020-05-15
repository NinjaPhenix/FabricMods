package ninjaphenix.containerlib.client;

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
import ninjaphenix.containerlib.client.gui.screen.ingame.ScrollableScreen;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public final class ContainerLibREIPlugin implements REIPluginV0
{
    @Override
    public SemanticVersion getMinimumVersion() throws VersionParsingException { return SemanticVersion.parse("3.0-pre"); }

    @Override
    public Identifier getPluginIdentifier() { return new Identifier("ninjaphenix-container-lib", "reiplugin"); }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void registerBounds(DisplayHelper displayHelper)
    {
        // todo: update for new screen code.
        BaseBoundsHandler.getInstance().registerExclusionZones(ScrollableScreen.class, () ->
        {
            final ScrollableScreen screen = (ScrollableScreen) MinecraftClient.getInstance().currentScreen;
            final ArrayList<Rectangle> rv = new ArrayList<>(1);
            if (screen.hasScrollbar()) { rv.add(new Rectangle(screen.getLeft() + 172, screen.getTop(), 22, 132)); }
            return rv;
        });
    }
}
