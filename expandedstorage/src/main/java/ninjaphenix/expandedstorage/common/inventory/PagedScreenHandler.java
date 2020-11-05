package ninjaphenix.expandedstorage.common.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import ninjaphenix.expandedstorage.common.ModContent;
import ninjaphenix.expandedstorage.common.inventory.screen.PagedScreenMeta;

public final class PagedScreenHandler extends AbstractScreenHandler<PagedScreenMeta>
{
    private static final ImmutableMap<Integer, PagedScreenMeta> SIZES = ImmutableMap.<Integer, PagedScreenMeta>builder()
            .put(27, new PagedScreenMeta(9, 3, 1, 27, getTexture("shared", 9, 3), 208, 192)) // Wood
            .put(54, new PagedScreenMeta(9, 6, 1, 54, getTexture("shared", 9, 6), 208, 240)) // Iron / Large Wood
            .put(81, new PagedScreenMeta(9, 9, 1, 81, getTexture("shared", 9, 9), 208, 304)) // Gold
            .put(108, new PagedScreenMeta(9, 6, 2, 108, getTexture("shared", 9, 6), 208, 240)) // Diamond / Large Iron
            .put(135, new PagedScreenMeta(9, 5, 3, 135, getTexture("shared", 9, 5), 208, 224)) // Netherite
            .put(162, new PagedScreenMeta(9, 6, 3, 162, getTexture("shared", 9, 6), 208, 240)) // Large Gold
            .put(216, new PagedScreenMeta(9, 8, 3, 216, getTexture("shared", 9, 8), 208, 288)) // Large Diamond
            .put(270, new PagedScreenMeta(10, 9, 3, 270, getTexture("shared", 10, 9), 224, 304)) // Large Netherite
            .build();

    public PagedScreenHandler(final int syncId, final BlockPos pos, final Container inventory, final Player player,
                              final Component displayName)
    {
        super(ModContent.PAGED_HANDLER_TYPE, syncId, pos, inventory, player, displayName, getNearestSize(inventory.getContainerSize()));
        resetSlotPositions(true);
        final Container playerInventory = player.getInventory();
        final int left = (SCREEN_META.WIDTH * 18 + 14) / 2 - 80;
        final int top = 18 + 14 + (SCREEN_META.HEIGHT * 18);
        for (int x = 0; x < 9; x++)
        {
            for (int y = 0; y < 3; y++) { addSlot(new Slot(playerInventory, y * 9 + x + 9, left + 18 * x, top + y * 18)); }
        }
        for (int i = 0; i < 9; i++) { addSlot(new Slot(playerInventory, i, left + 18 * i, top + 58)); }
    }

    private static PagedScreenMeta getNearestSize(final int invSize)
    {
        final PagedScreenMeta exactMeta = SIZES.get(invSize);
        if (exactMeta != null) { return exactMeta; }
        final List<Integer> keys = SIZES.keySet().asList();
        final int index = Collections.binarySearch(keys, invSize);
        final int largestKey = keys.get(Math.abs(index) - 1);
        final PagedScreenMeta nearestMeta = SIZES.get(largestKey);
        if (nearestMeta != null && largestKey > invSize && largestKey - invSize <= nearestMeta.WIDTH) { return nearestMeta; }
        throw new RuntimeException("No screen can show an inventory of size " + invSize + ".");
    }

    public void resetSlotPositions(final boolean createSlots)
    {
        for (int i = 0; i < INVENTORY.getContainerSize(); i++)
        {
            final int x = i % SCREEN_META.WIDTH;
            int y = Mth.ceil((((double) (i - x)) / SCREEN_META.WIDTH));
            if (y >= SCREEN_META.HEIGHT) { y = (18 * (y % SCREEN_META.HEIGHT)) - 2000; }
            else {y = y * 18;}
            if (createSlots) { addSlot(new Slot(INVENTORY, i, x * 18 + 8, y + 18)); }
            else { slots.get(i).y = y + 18; }
        }
    }

    public void moveSlotRange(final int min, final int max, final int yChange)
    {
        for (int i = min; i < max; i++) { slots.get(i).y += yChange; }
    }

    public static final class Factory implements ScreenHandlerRegistry.ExtendedClientHandlerFactory<PagedScreenHandler>
    {
        @Override
        public PagedScreenHandler create(final int syncId, final Inventory playerInventory, final FriendlyByteBuf buffer)
        {
            if (buffer == null) { return null; }
            return new PagedScreenHandler(syncId, buffer.readBlockPos(), new SimpleContainer(buffer.readInt()), playerInventory.player,
                                          null);
        }
    }
}