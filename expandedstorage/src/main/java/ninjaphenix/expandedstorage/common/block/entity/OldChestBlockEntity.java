package ninjaphenix.expandedstorage.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.expandedstorage.common.ModContent;
import ninjaphenix.expandedstorage.common.Registries;

public final class OldChestBlockEntity extends StorageBlockEntity
{
    public OldChestBlockEntity(final BlockPos pos, final BlockState state) { super(ModContent.OLD_CHEST, pos, state); }

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