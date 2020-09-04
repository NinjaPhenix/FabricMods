package ninjaphenix.expandedstorage.common.block.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import ninjaphenix.expandedstorage.common.ModContent;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.BarrelBlock;

public class BarrelBlockEntity extends StorageBlockEntity
{
    private int viewerCount;

    public BarrelBlockEntity(final ResourceLocation block) { super(ModContent.BARREL, block); }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void initialize(final ResourceLocation block)
    {
        this.block = block;
        defaultContainerName = Registries.BARREL.get(block).getContainerName();
        inventorySize = Registries.BARREL.get(block).getSlotCount();
        inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
        SLOTS = new int[inventorySize];
        for (int i = 0; i < inventorySize; i++) { SLOTS[i] = i; }
    }

    public void startOpen(final Player player)
    {
        if (!player.isSpectator())
        {
            if (viewerCount < 0) { viewerCount = 0; }
            ++viewerCount;
            final BlockState state = getBlockState();
            if (!state.getValue(BlockStateProperties.OPEN))
            {
                playSound(state, SoundEvents.BARREL_OPEN);
                setOpen(state, true);
            }
            scheduleBlockUpdate();
        }
    }

    private void scheduleBlockUpdate() { level.getBlockTicks().scheduleTick(getBlockPos(), getBlockState().getBlock(), 5); }

    public void tick()
    {
        viewerCount = CursedChestBlockEntity.countViewers(level, this, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        if (viewerCount > 0) { scheduleBlockUpdate(); }
        else
        {
            final BlockState state = getBlockState();
            if (!(state.getBlock() instanceof BarrelBlock))
            {
                setRemoved();
                return;
            }
            if (state.getValue(BlockStateProperties.OPEN))
            {
                playSound(state, SoundEvents.BARREL_CLOSE);
                setOpen(state, false);
            }
        }
    }

    public void stopOpen(final Player player)
    {
        if (!player.isSpectator()) { --viewerCount; }
    }

    private void setOpen(final BlockState state, final boolean open)
    {
        level.setBlock(getBlockPos(), state.setValue(BlockStateProperties.OPEN, open), 3);
    }

    private void playSound(final BlockState state, final SoundEvent sound)
    {
        final Vec3i facingVector = state.getValue(BlockStateProperties.FACING).getNormal();
        final double x = worldPosition.getX() + 0.5D + facingVector.getX() / 2.0D;
        final double y = worldPosition.getY() + 0.5D + facingVector.getY() / 2.0D;
        final double z = worldPosition.getZ() + 0.5D + facingVector.getZ() / 2.0D;
        level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }
}