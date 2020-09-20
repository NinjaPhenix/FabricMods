package ninjaphenix.renderingtests;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import ninjaphenix.renderingtests.barrel.BarrelInitializer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Main implements ModInitializer
{
    public static final Main INSTANCE = new Main();
    public static final String MOD_ID = "renderingtests";

    @Override
    public void onInitialize()
    {
        final Set<Initializer> tests = initializedSet(
                set ->
                {
                    set.add(BarrelInitializer.INSTANCE);
                });
        tests.forEach(Initializer::onCommon);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) { tests.forEach(Initializer::onClient); }
    }

    private <T> Set<T> initializedSet(final Consumer<HashSet<T>> initializer)
    {
        final HashSet<T> temporarySet = new HashSet<>();
        initializer.accept(temporarySet);
        return Collections.unmodifiableSet(temporarySet);
    }
}