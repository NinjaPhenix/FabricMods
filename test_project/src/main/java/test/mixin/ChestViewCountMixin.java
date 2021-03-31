package test.mixin;

import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import test.ChestViewCountAccessor;

@Mixin(ChestBlockEntity.class)
public class ChestViewCountMixin implements ChestViewCountAccessor
{
    @Shadow
    @Final
    private ContainerOpenersCounter openersCounter;

    @Override
    public int getNumberOfLookingPlayers()
    {
        return openersCounter.getOpenerCount();
    }
}
