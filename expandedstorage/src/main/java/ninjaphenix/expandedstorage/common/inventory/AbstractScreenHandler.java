package ninjaphenix.expandedstorage.common.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import ninjaphenix.expandedstorage.common.Const;
import ninjaphenix.expandedstorage.common.inventory.screen.ScreenMeta;

public abstract class AbstractScreenHandler<T extends ScreenMeta> extends AbstractContainerMenu
{
    public final BlockPos ORIGIN;
    public final T SCREEN_META;
    protected final Container INVENTORY;
    private final Component DISPLAY_NAME;

    public AbstractScreenHandler(final MenuType<?> type, final int syncId, final BlockPos pos, final Container inventory,
                                 final Player player, final Component displayName, final T meta)
    {
        super(type, syncId);
        ORIGIN = pos;
        INVENTORY = inventory;
        DISPLAY_NAME = displayName;
        SCREEN_META = meta;
        inventory.startOpen(player);
    }

    @Override
    public boolean stillValid(final Player player) { return INVENTORY.stillValid(player); }

    public Component getDisplayName() { return DISPLAY_NAME.plainCopy(); }

    @Override
    public ItemStack quickMoveStack(final Player player, final int slotIndex)
    {
        ItemStack stack = ItemStack.EMPTY;
        final Slot slot = slots.get(slotIndex);
        if (slot.hasItem())
        {
            final ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();
            if (slotIndex < INVENTORY.getContainerSize())
            {
                if (!moveItemStackTo(slotStack, INVENTORY.getContainerSize(), slots.size(), true)) { return ItemStack.EMPTY; }
            }
            else if (!moveItemStackTo(slotStack, 0, INVENTORY.getContainerSize(), false)) { return ItemStack.EMPTY; }
            if (slotStack.isEmpty()) { slot.set(ItemStack.EMPTY); }
            else { slot.setChanged(); }
        }
        return stack;
    }

    @Override
    public void removed(final Player player)
    {
        super.removed(player);
        INVENTORY.stopOpen(player);
    }

    public Container getInventory() { return INVENTORY; }

    public static ResourceLocation getTexture(final String type, final int width, final int height)
    {
        return new ResourceLocation(Const.MOD_ID, String.format("textures/gui/container/%s_%d_%d.png", type, width, height));
    }
}