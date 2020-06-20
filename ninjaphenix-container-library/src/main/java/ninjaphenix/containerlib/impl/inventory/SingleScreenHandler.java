package ninjaphenix.containerlib.impl.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import ninjaphenix.containerlib.api.screen.SingleScreenMeta;
import ninjaphenix.containerlib.api.inventory.AbstractContainer;
import ninjaphenix.containerlib.api.inventory.AreaAwareSlotFactory;

import java.util.Arrays;
import java.util.HashMap;

public final class SingleScreenHandler extends AbstractContainer<SingleScreenMeta>
{
    private static final HashMap<Integer, SingleScreenMeta> SIZES = new HashMap<>();

    public static void onScreenSizeRegistered(SingleScreenMeta meta) { SIZES.put(meta.TOTAL_SLOTS, meta); }

    public SingleScreenHandler(ScreenHandlerType<?> type, int syncId, BlockPos pos, Inventory inventory,
            PlayerEntity player, Text displayName, AreaAwareSlotFactory slotFactory)
    {
        super(type, syncId, pos, inventory, player, displayName, getNearestSize(inventory.size()));
        for (int i = 0; i < inventory.size(); i++)
        {
            final int x = i % SCREEN_META.WIDTH;
            final int y = (i - x) / SCREEN_META.WIDTH;
            this.addSlot(slotFactory.create(inventory, "inventory", i, x * 18 + 8, y * 18 + 18));
        }
        final int left = (SCREEN_META.WIDTH * 18 + 14) / 2 - 80;
        final int top = 18 + 14 + (SCREEN_META.HEIGHT * 18);
        for (int x = 0; x < 9; x++)
        {
            for (int y = 0; y < 3; y++)
            {
                this.addSlot(slotFactory.create(PLAYER_INVENTORY, "player_inventory", y * 9 + x + 9, left + 18 * x, top + y * 18));
            }
        }
        for (int i = 0; i < 9; i++) { this.addSlot(slotFactory.create(PLAYER_INVENTORY, "player_hotbar", i, left + 18 * i, top + 58)); }
    }

    private static SingleScreenMeta getNearestSize(int invSize)
    {
        SingleScreenMeta val = SIZES.get(invSize);
        if (val != null) { return val; }
        final Integer[] keys = SIZES.keySet().toArray(new Integer[]{});
        Arrays.sort(keys);
        final int largestKey = keys[Math.abs(Arrays.binarySearch(keys, invSize)) - 1];
        val = SIZES.get(largestKey);
        if (largestKey > invSize && largestKey - invSize <= val.WIDTH) { return SIZES.get(largestKey); }
        throw new RuntimeException("No screen can show an inventory of size " + invSize + "."); // make this more obvious?
    }


}
