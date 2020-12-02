package ninjaphenix.fatxporbs.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbEntityMixin extends Entity
{
    @Shadow private int health;
    @Shadow private int value;

    public ExperienceOrbEntityMixin(final EntityType<?> type, final Level world) { super(type, world); }

    @Inject(at = @At("TAIL"), method = "tick()V", cancellable = true)
    private void tick(CallbackInfo info)
    {
        if (level.isClientSide) { return; }
        if (level.getGameTime() % 5 == 0)
        {
            final BlockPos pos = blockPosition();
            final List<ExperienceOrb> entities = level.getEntitiesOfClass(ExperienceOrb.class,
                    new AABB(pos.west(2).north(2).above(2), pos.east(2).south(2).below(2)), e -> e.isAlive() && !e.getUUID().equals(uuid));
            if (entities.isEmpty()) { return; }
            final ExperienceOrb orb = entities.get(0);
            if (orb.getValue() + value > value) {
                value = Integer.MAX_VALUE;
            }
            else {
                value += orb.getValue();
            }
            orb.remove();
            health = 0;
        }
    }
}
