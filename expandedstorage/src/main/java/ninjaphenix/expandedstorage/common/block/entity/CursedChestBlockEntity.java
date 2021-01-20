package ninjaphenix.expandedstorage.common.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import ninjaphenix.expandedstorage.common.ModContent;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.ChestBlock;
import ninjaphenix.expandedstorage.common.block.CursedChestBlock;
import ninjaphenix.expandedstorage.common.inventory.AbstractScreenHandler;
import ninjaphenix.expandedstorage.common.inventory.DoubleSidedInventory;

@EnvironmentInterfaces({@EnvironmentInterface(value = EnvType.CLIENT, itf = LidBlockEntity.class)})
public final class CursedChestBlockEntity extends StorageBlockEntity implements LidBlockEntity, TickableBlockEntity
{
    private float animationAngle, lastAnimationAngle;
    private int viewerCount, ticksOpen;

    public CursedChestBlockEntity(final ResourceLocation block) { super(ModContent.CHEST, block); }

    public static int countViewers(final Level world, final WorldlyContainer instance, final int x, final int y, final int z)
    {
        return world.getEntitiesOfClass(Player.class, new AABB(x - 5, y - 5, z - 5, x + 6, y + 6, z + 6)).stream()
                .filter(player -> player.containerMenu instanceof AbstractScreenHandler)
                .map(player -> ((AbstractScreenHandler<?>) player.containerMenu).getInventory())
                .filter(inventory -> inventory == instance ||
                        inventory instanceof DoubleSidedInventory && ((DoubleSidedInventory) inventory).isPart(instance))
                .mapToInt(inv -> 1).sum();
    }

    private static int tickViewerCount(final Level world, final CursedChestBlockEntity instance, final int ticksOpen, final int x,
                                       final int y, final int z, final int viewCount)
    {
        if (!world.isClientSide && viewCount != 0 && (ticksOpen + x + y + z) % 200 == 0)
        {
            return countViewers(world, instance, x, y, z);
        }
        return viewCount;
    }

    public ResourceLocation getBlock() { return block; }

    @Environment(EnvType.CLIENT)
    public void setBlock(final ResourceLocation block) { this.block = block; }

    @Override
    @SuppressWarnings({"ConstantConditions"})
    protected void initialize(final ResourceLocation block)
    {
        this.block = block;
        defaultContainerName = Registries.CHEST.get(block).CONTAINER_NAME;
        inventorySize = Registries.CHEST.get(block).SLOT_COUNT;
        inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
        SLOTS = new int[inventorySize];
        for (int i = 0; i < inventorySize; i++) { SLOTS[i] = i; }
    }

    @Override
    public boolean triggerEvent(final int actionId, final int value)
    {
        if (actionId == 1)
        {
            viewerCount = value;
            return true;
        }
        else { return super.triggerEvent(actionId, value); }
    }

    @Override
    public float getOpenNess(final float f) { return Mth.lerp(f, lastAnimationAngle, animationAngle); }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void tick()
    {
        viewerCount = tickViewerCount(level, this, ++ticksOpen, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), viewerCount);
        lastAnimationAngle = animationAngle;
        if (viewerCount > 0 && animationAngle == 0.0F) { playSound(SoundEvents.CHEST_OPEN); }
        if (viewerCount == 0 && animationAngle > 0.0F || viewerCount > 0 && animationAngle < 1.0F)
        {
            animationAngle = Mth.clamp(animationAngle + (viewerCount > 0 ? 0.1F : -0.1F), 0, 1);
            if (animationAngle < 0.5F && lastAnimationAngle >= 0.5F) { playSound(SoundEvents.CHEST_CLOSE); }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void playSound(final SoundEvent soundEvent)
    {
        final BlockState state = getBlockState();
        final DoubleBlockCombiner.BlockType mergeType = ChestBlock.getBlockType(state);
        final Vec3 soundPos;
        if (mergeType == DoubleBlockCombiner.BlockType.SINGLE) { soundPos = Vec3.atCenterOf(worldPosition); }
        else if (mergeType == DoubleBlockCombiner.BlockType.FIRST)
        {
            soundPos = Vec3.atCenterOf(worldPosition).add(Vec3.atLowerCornerOf(ChestBlock.getDirectionToAttached(state).getNormal()).scale(0.5D));
        }
        else { return; }
        level.playSound(null, soundPos.x(), soundPos.y(), soundPos.z(), soundEvent, SoundSource.BLOCKS, 0.5F,
                        level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void startOpen(final Player player)
    {
        if (player.isSpectator()) { return; }
        if (viewerCount < 0) { viewerCount = 0; }
        viewerCount++;
        onInvOpenOrClose();
    }

    @Override
    public void stopOpen(final Player player)
    {
        if (player.isSpectator()) { return; }
        viewerCount--;
        onInvOpenOrClose();
    }

    @SuppressWarnings("ConstantConditions")
    private void onInvOpenOrClose()
    {
        final Block block = getBlockState().getBlock();
        if (block instanceof CursedChestBlock)
        {
            level.blockEvent(worldPosition, block, 1, viewerCount);
            level.updateNeighborsAt(worldPosition, block);
        }
    }
}