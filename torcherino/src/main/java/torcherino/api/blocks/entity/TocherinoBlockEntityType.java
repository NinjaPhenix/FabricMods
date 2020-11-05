package torcherino.api.blocks.entity;

import com.mojang.datafixers.types.Type;
import net.minecraft.world.level.block.state.BlockState;
import torcherino.api.TierSupplier;

import net.minecraft.world.level.block.entity.BlockEntityType;

@SuppressWarnings("SpellCheckingInspection")
public class TocherinoBlockEntityType extends BlockEntityType<TorcherinoBlockEntity>
{
    public TocherinoBlockEntityType(BlockEntitySupplier<TorcherinoBlockEntity> supplier, Type type) { super(supplier, null, type); }

    @Override
    public boolean isValid(BlockState state) { return state.getBlock() instanceof TierSupplier; }
}
