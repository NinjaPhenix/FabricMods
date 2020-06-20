package ninjaphenix.expandedstorage.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import ninjaphenix.expandedstorage.block.misc.Nameable;

public abstract class AbstractChestBlockEntity extends LootableContainerBlockEntity implements SidedInventory, Nameable
{
    protected Text defaultContainerName;
    protected int inventorySize;
    protected DefaultedList<ItemStack> inventory;
    protected int[] SLOTS;

    protected Identifier block;

    protected AbstractChestBlockEntity(BlockEntityType type, Identifier block)
    {
        super(type);
        if (block != null) { this.initialize(block); }
    }

    protected void initialize(Identifier block) { }


    public Identifier getBlock() { return block; }

    @Environment(EnvType.CLIENT)
    public void setBlock(Identifier block) { this.block = block; }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() { return inventory; }

    @Override
    public void setInvStackList(DefaultedList<ItemStack> inventory) { this.inventory = inventory; }

    @Override
    protected Container createContainer(int i, PlayerInventory playerInventory) { return null; }

    @Override
    public int[] getInvAvailableSlots(Direction side) { return SLOTS; }

    @Override
    public boolean canInsertInvStack(int slot, ItemStack stack, Direction direction) { return this.isValidInvStack(slot, stack); }

    @Override
    public boolean canExtractInvStack(int slot, ItemStack stack, Direction direction) { return true; }

    @Override
    public int getInvSize() { return inventorySize; }

    @Override
    protected Text getContainerName() { return defaultContainerName; }

    @Override
    public boolean isInvEmpty()
    {
        for (ItemStack stack : inventory)
        { if (!stack.isEmpty()) { return false; } }
        return true;
    }

    @Override
    public void fromTag(CompoundTag tag)
    {
        super.fromTag(tag);
        Identifier id = new Identifier(tag.getString("type"));
        this.initialize(id);
        if (!deserializeLootTable(tag)) { Inventories.fromTag(tag, inventory); }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag)
    {
        super.toTag(tag);
        tag.putString("type", block.toString());
        if (!serializeLootTable(tag)) { Inventories.toTag(tag, inventory); }
        return tag;
    }

    @Override
    public CompoundTag toInitialChunkDataTag()
    {
        CompoundTag initialChunkTag = super.toTag(new CompoundTag());
        initialChunkTag.putString("type", block.toString());
        return initialChunkTag;
    }
}
