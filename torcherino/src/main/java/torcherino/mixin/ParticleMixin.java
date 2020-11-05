package torcherino.mixin;

import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import torcherino.Torcherino;

@Mixin(ParticleEngine.class)
public abstract class ParticleMixin
{
    @Shadow
    protected abstract <T extends ParticleOptions> void register(final ParticleType<T> particleType, final ParticleEngine.SpriteParticleRegistration<T> spriteParticleRegistration);

    @Inject(method = "registerProviders()V", at = @At("TAIL"))
    private void registerAdditionalFactories(final CallbackInfo ci)
    {
        Torcherino.particles.forEach(pt -> register(pt, FlameParticle.Provider::new));
    }
}