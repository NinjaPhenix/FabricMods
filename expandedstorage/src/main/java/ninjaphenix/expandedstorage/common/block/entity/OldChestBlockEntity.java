package ninjaphenix.expandedstorage.common.block.entity;

import ninjaphenix.expandedstorage.common.Registries;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import ninjaphenix.expandedstorage.common.ModContent;

public final class OldChestBlockEntity extends StorageBlockEntity
{
    public OldChestBlockEntity(final ResourceLocation block) { super(ModContent.OLD_CHEST, block); }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void initialize(final ResourceLocation block)
    {
        this.block = block;
        defaultContainerName = Registries.OLD_CHEST.get(block).CONTAINER_NAME;
        inventorySize = Registries.OLD_CHEST.get(block).SLOT_COUNT;
        inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
        SLOTS = new int[inventorySize];
        for (int i = 0; i < inventorySize; i++) { SLOTS[i] = i; }
    }
}