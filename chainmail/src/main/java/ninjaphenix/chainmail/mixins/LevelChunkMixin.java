package ninjaphenix.chainmail.mixins;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import ninjaphenix.chainmail.api.blockentity.ExpandedBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin
{
    @Inject(method = "addAndRegisterBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At("TAIL"))
    private void chainmail_addBlockEntity(final BlockEntity blockEntity, final CallbackInfo ci)
    {
        if (blockEntity instanceof ExpandedBlockEntity) { ((ExpandedBlockEntity) blockEntity).onLoad(); }
    }
}