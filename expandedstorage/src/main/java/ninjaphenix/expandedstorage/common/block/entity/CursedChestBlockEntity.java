package ninjaphenix.expandedstorage.common.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import ninjaphenix.expandedstorage.common.ModContent;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.ChestBlock;
import ninjaphenix.expandedstorage.common.inventory.AbstractScreenHandler;
import ninjaphenix.expandedstorage.common.inventory.DoubleSidedInventory;

@EnvironmentInterfaces({@EnvironmentInterface(value = EnvType.CLIENT, itf = LidBlockEntity.class)})
public final class CursedChestBlockEntity extends StorageBlockEntity implements LidBlockEntity
{
    private final ChestLidController chestLidController;
    private final ContainerOpenersCounter openersCounter;

    public CursedChestBlockEntity(final BlockPos pos, final BlockState state, final ResourceLocation tier)
    {
        super(ModContent.CHEST, pos, state, tier);
        openersCounter = new ContainerOpenersCounter()
        {
            @Override
            protected void onOpen(final Level level, final BlockPos pos, final BlockState state)
            {
                CursedChestBlockEntity.playSound(level, pos, state, SoundEvents.CHEST_OPEN);
            }

            @Override
            protected void onClose(final Level level, final BlockPos pos, final BlockState state)
            {
                CursedChestBlockEntity.playSound(level, pos, state, SoundEvents.CHEST_CLOSE);
            }

            @Override
            protected void openerCountChanged(final Level level, final BlockPos pos, final BlockState state, int i, int j)
            {
                CursedChestBlockEntity.this.signalOpenCount(level, pos, state, i, j);
            }

            @Override
            protected boolean isOwnContainer(Player player)
            {
                if (!(player.containerMenu instanceof AbstractScreenHandler)) { return false; }
                else
                {
                    final Container container = ((AbstractScreenHandler<?>) player.containerMenu).getInventory();
                    return container == CursedChestBlockEntity.this ||
                            container instanceof DoubleSidedInventory && ((DoubleSidedInventory) container).isPart(CursedChestBlockEntity.this);
                }
            }
        };
        chestLidController = new ChestLidController();
    }

    public ResourceLocation getBlock() { return block; }

    public static void lidAnimateTick(final Level level, final BlockPos pos, final BlockState state, final CursedChestBlockEntity blockEntity)
    {
        blockEntity.chestLidController.tickLid();
    }

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
    public boolean triggerEvent(int i, int j)
    {
        if (i == 1)
        {
            chestLidController.shouldBeOpen(j > 0);
            return true;
        }
        return super.triggerEvent(i, j);
    }

    @Override
    public float getOpenNess(final float f) { return chestLidController.getOpenness(f); }

    private static void playSound(final Level level, final BlockPos pos, final BlockState state, final SoundEvent soundEvent)
    {
        final DoubleBlockCombiner.BlockType mergeType = ChestBlock.getBlockType(state);
        final Vec3 soundPos;
        if (mergeType == DoubleBlockCombiner.BlockType.SINGLE) { soundPos = Vec3.atCenterOf(pos); }
        else if (mergeType == DoubleBlockCombiner.BlockType.FIRST)
        {
            soundPos = Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(ChestBlock.getDirectionToAttached(state).getNormal()).scale(0.5D));
        }
        else { return; }
        level.playSound(null, soundPos.x(), soundPos.y(), soundPos.z(), soundEvent, SoundSource.BLOCKS, 0.5F,
                        level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void startOpen(final Player player)
    {
        if (!player.isSpectator()) { openersCounter.incrementOpeners(player, getLevel(), getBlockPos(), getBlockState()); }
    }

    @Override
    public void stopOpen(final Player player)
    {
        if (!player.isSpectator()) { openersCounter.decrementOpeners(player, getLevel(), getBlockPos(), getBlockState()); }
    }

    protected void signalOpenCount(final Level level, final BlockPos pos, final BlockState state, int i, int j)
    {
        level.blockEvent(pos, state.getBlock(), 1, j);
    }

    public void recheckOpen() { openersCounter.recheckOpeners(getLevel(), getBlockPos(), getBlockState()); }
}