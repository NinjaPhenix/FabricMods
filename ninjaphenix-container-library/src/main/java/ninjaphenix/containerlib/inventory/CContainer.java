package ninjaphenix.containerlib.inventory;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.Text;

public class CContainer extends Container
{
    public final PlayerInventory PLAYER_INVENTORY;
    public final int WIDTH;
    public final int HEIGHT;
    private final Text DISPLAY_NAME;
    private final Inventory INVENTORY;

    public CContainer(ContainerType<?> type, int syncId, Inventory inventory, PlayerEntity player, Text displayName, int width, int height)
    {
        super(type, syncId);
        INVENTORY = inventory;
        PLAYER_INVENTORY = player.inventory;
        DISPLAY_NAME = displayName;
        WIDTH = width;
        HEIGHT = height;
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
