package ninjaphenix.expandedstorage;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public final class ExpandedStorageMixinPlugin implements IMixinConfigPlugin
{
    private final ImmutableMap<String, Boolean> conditionalMixins = ImmutableMap.<String, Boolean>builder()
            .put("ninjaphenix.expandedstorage.mixin.ToweletteSupportMixin", FabricLoader.getInstance().isModLoaded("towelette"))
            .build();

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName)
    {
        return conditionalMixins.getOrDefault(mixinClassName, Boolean.TRUE);
    }

    @Override
    public void onLoad(final String mixinPackage) { }

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public void acceptTargets(final Set<String> myTargets, final Set<String> otherTargets) { }

    @Override
    public List<String> getMixins() { return null; }

    @Override
    public void preApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo)
    {

    }

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo)
    {

    }
}