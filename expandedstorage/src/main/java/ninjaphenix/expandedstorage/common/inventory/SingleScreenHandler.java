package ninjaphenix.expandedstorage.common.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import ninjaphenix.expandedstorage.common.ModContent;
import ninjaphenix.expandedstorage.common.inventory.screen.SingleScreenMeta;

public final class SingleScreenHandler extends AbstractScreenHandler<SingleScreenMeta>
{
    // todo: replace with datapack extension
    private static final ImmutableMap<Integer, SingleScreenMeta> SIZES = ImmutableMap.<Integer, SingleScreenMeta>builder()
            .put(27, new SingleScreenMeta(9, 3, 27, getTexture("shared", 9, 3), 208, 192)) // Wood
            .put(54, new SingleScreenMeta(9, 6, 54, getTexture("shared", 9, 6), 208, 240)) // Iron / Large Wood
            .put(81, new SingleScreenMeta(9, 9, 81, getTexture("shared", 9, 9), 208, 304)) // Gold
            .put(108, new SingleScreenMeta(12, 9, 108, getTexture("shared", 12, 9), 256, 304)) // Diamond / Large Iron
            .put(135, new SingleScreenMeta(15, 9, 135, getTexture("shared", 15, 9), 320, 304)) // Netherite
            .put(162, new SingleScreenMeta(18, 9, 162, getTexture("shared", 18, 9), 368, 304)) // Large Gold
            .put(216, new SingleScreenMeta(18, 12, 216, getTexture("shared", 18, 12), 368, 352)) // Large Diamond
            .put(270, new SingleScreenMeta(18, 15, 270, getTexture("shared", 18, 15), 368, 416)) // Large Netherite
            .build();

    public SingleScreenHandler(final int syncId, final BlockPos pos, final Container inventory, final Player player,
                               final Component displayName)
    {
        super(ModContent.SINGLE_HANDLER_TYPE, syncId, pos, inventory, player, displayName, getNearestSize(inventory.getContainerSize()));
        for (int i = 0; i < inventory.getContainerSize(); i++)
        {
            final int x = i % SCREEN_META.WIDTH;
            final int y = (i - x) / SCREEN_META.WIDTH;
            addSlot(new Slot(inventory, i, x * 18 + 8, y * 18 + 18));
        }
        final Container playerInventory = player.inventory;
        final int left = (SCREEN_META.WIDTH * 18 + 14) / 2 - 80;
        final int top = 18 + 14 + (SCREEN_META.HEIGHT * 18);
        for (int x = 0; x < 9; x++)
        {
            for (int y = 0; y < 3; y++) { addSlot(new Slot(playerInventory, y * 9 + x + 9, left + 18 * x, top + y * 18)); }
        }
        for (int i = 0; i < 9; i++) { addSlot(new Slot(playerInventory, i, left + 18 * i, top + 58)); }
    }

    private static SingleScreenMeta getNearestSize(final int invSize)
    {
        final SingleScreenMeta exactMeta = SIZES.get(invSize);
        if (exactMeta != null) { return exactMeta; }
        final List<Integer> keys = SIZES.keySet().asList();
        final int index = Collections.binarySearch(keys, invSize);
        final int largestKey = keys.get(Math.abs(index) - 1);
        final SingleScreenMeta nearestMeta = SIZES.get(largestKey);
        if (nearestMeta != null && largestKey > invSize && largestKey - invSize <= nearestMeta.WIDTH) { return nearestMeta; }
        throw new RuntimeException("No screen can show an inventory of size " + invSize + ".");
    }


    public static final class Factory implements ScreenHandlerRegistry.ExtendedClientHandlerFactory<SingleScreenHandler>
    {
        @Override
        public SingleScreenHandler create(final int syncId, final Inventory playerInventory, final FriendlyByteBuf buffer)
        {
            if (buffer == null) { return null; }
            return new SingleScreenHandler(syncId, buffer.readBlockPos(), new SimpleContainer(buffer.readInt()), playerInventory.player,
                                           null);
        }
    }
}