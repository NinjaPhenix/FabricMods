package ninjaphenix.expandedstorage.block.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;

public class OldChestBlockEntity extends AbstractChestBlockEntity
{
    public OldChestBlockEntity(Identifier block) { super(ExpandedStorage.OLD_CHEST, block); }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void initialize(Identifier block)
    {
        this.block = block;
        defaultContainerName = Registries.OLD_CHEST.get(block).getContainerName();
        inventorySize = Registries.OLD_CHEST.get(block).getSlotCount();
        inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
        SLOTS = new int[inventorySize];
        for (int i = 0; i < inventorySize; i++) { SLOTS[i] = i; }
    }
}
