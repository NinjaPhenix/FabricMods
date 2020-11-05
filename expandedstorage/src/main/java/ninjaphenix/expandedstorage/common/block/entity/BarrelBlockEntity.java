package ninjaphenix.expandedstorage.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import ninjaphenix.expandedstorage.common.ModContent;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.inventory.AbstractScreenHandler;

public class BarrelBlockEntity extends StorageBlockEntity
{
    private final ContainerOpenersCounter openersCounter;

    public BarrelBlockEntity(final BlockPos pos, final BlockState state)
    {
        super(ModContent.BARREL, pos, state);
        openersCounter = new ContainerOpenersCounter()
        {
            @Override
            protected void onOpen(final Level level, final BlockPos pos, final BlockState state)
            {
                BarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
                BarrelBlockEntity.this.updateBlockState(state, true);
            }

            @Override
            protected void onClose(final Level level, final BlockPos pos, final BlockState state)
            {
                BarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
                BarrelBlockEntity.this.updateBlockState(state, false);
            }

            @Override
            protected void openerCountChanged(final Level level, final BlockPos pos, final BlockState state, final int i, final int j) { }

            @Override
            protected boolean isOwnContainer(final Player player)
            {
                if (player.containerMenu instanceof AbstractScreenHandler<?>)
                {
                    return ((AbstractScreenHandler<?>) player.containerMenu).getInventory() == BarrelBlockEntity.this;
                }
                else { return false; }
            }
        };
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void initialize(final ResourceLocation block)
    {
        this.block = block;
        defaultContainerName = Registries.BARREL.get(block).CONTAINER_NAME;
        inventorySize = Registries.BARREL.get(block).SLOT_COUNT;
        inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
        SLOTS = new int[inventorySize];
        for (int i = 0; i < inventorySize; i++) { SLOTS[i] = i; }
    }

    private void playSound(final BlockState state, final SoundEvent sound)
    {
        final Vec3i facingVector = state.getValue(BlockStateProperties.FACING).getNormal();
        final double x = worldPosition.getX() + 0.5D + facingVector.getX() / 2.0D;
        final double y = worldPosition.getY() + 0.5D + facingVector.getY() / 2.0D;
        final double z = worldPosition.getZ() + 0.5D + facingVector.getZ() / 2.0D;
        level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void startOpen(final Player player)
    {
        if (!player.isSpectator()) { openersCounter.incrementOpeners(getLevel(), getBlockPos(), getBlockState()); }
    }

    @Override
    public void stopOpen(final Player player)
    {
        if (!player.isSpectator()) { openersCounter.decrementOpeners(getLevel(), getBlockPos(), getBlockState()); }
    }

    public void recheckOpen()
    {
        openersCounter.recheckOpeners(getLevel(), getBlockPos(), getBlockState());
    }

    private void updateBlockState(final BlockState state, final boolean open)
    {
        level.setBlock(getBlockPos(), state.setValue(BarrelBlock.OPEN, open), 3);
    }
}