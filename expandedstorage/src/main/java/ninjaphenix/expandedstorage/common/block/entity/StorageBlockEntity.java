package ninjaphenix.expandedstorage.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class StorageBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer
{
    protected Component defaultContainerName;
    protected int inventorySize;
    protected NonNullList<ItemStack> inventory;
    protected int[] SLOTS;
    protected ResourceLocation block;

    protected StorageBlockEntity(final BlockEntityType<?> blockEntityType, final BlockPos pos, final BlockState state,
                                 final ResourceLocation tier)
    {
        super(blockEntityType, pos, state);
        if (tier != null) { initialize(tier); }
    }

    protected abstract void initialize(final ResourceLocation block);

    @Override
    protected NonNullList<ItemStack> getItems() { return inventory; }

    @Override
    public void setItems(final NonNullList<ItemStack> inventory) { this.inventory = inventory; }

    @Override
    protected AbstractContainerMenu createMenu(final int i, final Inventory playerInventory) { return null; }

    @Override
    public int[] getSlotsForFace(final Direction side) { return SLOTS; }

    @Override
    public boolean canPlaceItemThroughFace(final int slot, final ItemStack stack, final Direction direction) { return canPlaceItem(slot, stack); }

    @Override
    public boolean canTakeItemThroughFace(final int slot, final ItemStack stack, final Direction direction) { return true; }

    @Override
    public int getContainerSize() { return inventorySize; }

    @Override
    protected Component getDefaultName() { return defaultContainerName; }

    @Override
    public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }

    @Override
    public void load(final CompoundTag tag)
    {
        super.load(tag);
        if (!tryLoadLootTable(tag)) { ContainerHelper.loadAllItems(tag, inventory); }
    }

    @Override
    public CompoundTag save(final CompoundTag tag)
    {
        super.save(tag);
        if (!trySaveLootTable(tag)) { ContainerHelper.saveAllItems(tag, inventory); }
        return tag;
    }
}