package ninjaphenix.renderingtests;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.renderingtests.atlas.AtlasInitializer;
import ninjaphenix.renderingtests.barrel.BarrelInitializer;

public class Main implements ModInitializer
{
    public static final Main INSTANCE = new Main();
    public static final String MOD_ID = "renderingtests";

    public static ResourceLocation resloc(String path) { return new ResourceLocation(MOD_ID, path); }

    @Override
    public void onInitialize()
    {
        final Initializer[] tests = new Initializer[]
                {
                        AtlasInitializer.INSTANCE,
                        //BarrelInitializer.INSTANCE
                };
        for (final Initializer test : tests) { test.onCommon(); }
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) { for (final Initializer test : tests) { test.onClient(); } }
    }
}