package ninjaphenix.container_library.api.common.inventory;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import ninjaphenix.container_library.api.common.inventory.AbstractScreenHandler.ScreenMeta;

public interface ExtendedScreenHandlerFactoryProvider
{
    ExtendedScreenHandlerFactory createFactory(final Identifier playerPreference, final ScreenMeta meta, final BlockState state, final ServerWorld world, final BlockPos pos);
}
