package ninjaphenix.expandedstorage.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.Block;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ninjaphenix.containerlib.api.inventory.DoubleSidedInventory;
import ninjaphenix.containerlib.inventory.ScrollableContainer;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.block.CursedChestBlock;
import ninjaphenix.expandedstorage.block.misc.CursedChestType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

@EnvironmentInterfaces({ @EnvironmentInterface(value = EnvType.CLIENT, itf = ChestAnimationProgress.class) })
public class CursedChestBlockEntity extends AbstractChestBlockEntity implements ChestAnimationProgress, Tickable
{
    private float animationAngle;
    private float lastAnimationAngle;
    private int viewerCount;
    private int ticksOpen;

    public CursedChestBlockEntity(@Nullable Identifier block) { super(ExpandedStorage.CHEST, block); }

    private static int tickViewerCount(World world, CursedChestBlockEntity instance, int ticksOpen, int x, int y, int z, int viewCount)
    {
        if (!world.isClient && viewCount != 0 && (ticksOpen + x + y + z) % 200 == 0) { return countViewers(world, instance, x, y, z); }
        return viewCount;
    }

    private static int countViewers(World world, CursedChestBlockEntity instance, int x, int y, int z)
    {
        int viewers = 0;
        final List<PlayerEntity> playersInRange = world.getNonSpectatingEntities(PlayerEntity.class, new Box(x - 5, y - 5, z - 5, x + 6, y + 6, z + 6));
        for (PlayerEntity player : playersInRange)
        {
            if (player.container instanceof ScrollableContainer)
            {
                final Inventory inventory = ((ScrollableContainer) player.container).getInventory();
                if (inventory == instance || inventory instanceof DoubleSidedInventory && ((DoubleSidedInventory) inventory).isPart(instance)) {viewers++;}
            }
        }
        return viewers;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void initialize(@NonNull Identifier block)
    {
        this.block = block;
        defaultContainerName = Registries.CHEST.get(block).getContainerName();
        inventorySize = Registries.CHEST.get(block).getSlotCount();
        inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
        SLOTS = new int[inventorySize];
        for (int i = 0; i < inventorySize; i++) { SLOTS[i] = i; }
    }

    @Override
    public boolean onBlockAction(int actionId, int value)
    {
        if (actionId == 1)
        {
            viewerCount = value;
            return true;
        }
        else { return super.onBlockAction(actionId, value); }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getAnimationProgress(float f) { return MathHelper.lerp(f, lastAnimationAngle, animationAngle); }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void tick()
    {
        viewerCount = tickViewerCount(world, this, ++ticksOpen, pos.getX(), pos.getY(), pos.getZ(), viewerCount);
        lastAnimationAngle = animationAngle;
        if (viewerCount > 0 && animationAngle == 0.0F) { playSound(SoundEvents.BLOCK_CHEST_OPEN); }
        if (viewerCount == 0 && animationAngle > 0.0F || viewerCount > 0 && animationAngle < 1.0F)
        {
            float float_2 = animationAngle;
            if (viewerCount > 0) { animationAngle += 0.1F; }
            else { animationAngle -= 0.1F; }
            animationAngle = MathHelper.clamp(animationAngle, 0, 1);
            if (animationAngle < 0.5F && float_2 >= 0.5F) { playSound(SoundEvents.BLOCK_CHEST_CLOSE); }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void playSound(SoundEvent soundEvent)
    {
        CursedChestType chestType = getCachedState().get(CursedChestBlock.TYPE);
        if (!chestType.isRenderedType()) { return; }
        double zOffset = 0.5;
        if (chestType == CursedChestType.BOTTOM) { zOffset = 1; }
        BlockPos otherPos = CursedChestBlock.getPairedPos(world, pos);
        Vec3d center = new Vec3d(pos).add(new Vec3d(otherPos == null ? pos : otherPos));
        world.playSound(null, center.getX() / 2 + 0.5D, center.getY() / 2 + 0.5D, center.getZ() / 2 + zOffset, soundEvent, SoundCategory.BLOCKS, 0.5F,
                world.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void onInvOpen(PlayerEntity player)
    {
        if (player.isSpectator()) { return; }
        if (viewerCount < 0) { viewerCount = 0; }
        viewerCount++;
        onInvOpenOrClose();
    }

    @Override
    public void onInvClose(PlayerEntity player)
    {
        if (player.isSpectator()) { return; }
        viewerCount--;
        onInvOpenOrClose();
    }

    @SuppressWarnings("ConstantConditions")
    private void onInvOpenOrClose()
    {
        Block block = getCachedState().getBlock();
        if (block instanceof CursedChestBlock)
        {
            world.addBlockAction(pos, block, 1, viewerCount);
            world.updateNeighborsAlways(pos, block);
        }
    }
}
