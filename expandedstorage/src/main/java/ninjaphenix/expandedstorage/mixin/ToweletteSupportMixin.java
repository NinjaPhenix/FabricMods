package ninjaphenix.expandedstorage.mixin;

import ninjaphenix.expandedstorage.common.block.FluidLoggableChestBlock;
import org.spongepowered.asm.mixin.Mixin;
import virtuoel.towelette.api.Fluidloggable;

@Mixin(FluidLoggableChestBlock.class)
public final class ToweletteSupportMixin implements Fluidloggable
{
}