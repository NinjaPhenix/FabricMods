package ninjaphenix.containerlib.inventory;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import ninjaphenix.containerlib.ContainerSizing;

import java.util.HashMap;

public class CContainer extends Container
{
    private static final HashMap<Integer, ContainerSizing> SIZES;

    static
    {
        SIZES = new HashMap<>();
        SIZES.put(27, new ContainerSizing(9, 3));
        SIZES.put(54, new ContainerSizing(9, 6));
        SIZES.put(81, new ContainerSizing(9, 9));
        SIZES.put(108, new ContainerSizing(12, 9, 9, 6, 12, 9, 6, 2));
        SIZES.put(162, new ContainerSizing(18, 9, 9, 6, 18, 9, 6, 3));
        SIZES.put(216, new ContainerSizing(24, 9, 12, 6, 18, 12, 6, 3));
        /*

        def Factors(x: int):
	        r = []
	        for i in range(1, math.ceil(x / 2)):
		        if (x / i).is_integer():
			        r.append((i, int(x/i)))
	        return r

         */
    }

    public final PlayerInventory PLAYER_INVENTORY;
    public final ContainerSizing SIZING;
    private final Text DISPLAY_NAME;
    private final Inventory INVENTORY;

    public CContainer(ContainerType<?> type, int syncId, Inventory inventory, PlayerEntity player, Text displayName)
    {
        super(type, syncId);
        INVENTORY = inventory;
        PLAYER_INVENTORY = player.inventory;
        DISPLAY_NAME = displayName;
        SIZING = SIZES.get(inventory.getInvSize());

        player.sendMessage(new LiteralText("Opening container: ").append(SIZING.FIXED_WIDTH + " ").append(SIZING.FIXED_HEIGHT + ""));
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return INVENTORY.canPlayerUseInv(player);
    }

    public Text getDisplayName()
    {
        return DISPLAY_NAME.deepCopy();
    }
}
