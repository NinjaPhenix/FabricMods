package ninjaphenix.containerlib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ninjaphenix.containerlib.inventory.CContainer;

public final class ContainerLibrary implements ModInitializer
{
    public static final Identifier CONTAINER_ID = new Identifier("ninjaphenix-container-lib", "container");
    public static final ContainerLibrary INSTANCE = new ContainerLibrary();

    private ContainerLibrary() {}

    /**
     * Open's a modded container which block implements InventoryProvider.
     *
     * @param player The Player who attempted to open the container.
     * @param pos The block pos of the container.
     * @param containerName The text that should be displayed as the container name.
     * @since 0.0.1
     */
    public static void openContainer(PlayerEntity player, BlockPos pos, Text containerName)
    {
        ContainerProviderRegistry.INSTANCE.openContainer(CONTAINER_ID, player, (buffer) ->
        {
            buffer.writeBlockPos(pos);
            buffer.writeText(containerName);
        });
    }

    @Override
    public void onInitialize()
    {
        ContainerProviderRegistry.INSTANCE.registerFactory(CONTAINER_ID, (syncId, identifier, player, buffer) ->
        {
            final BlockPos pos = buffer.readBlockPos();
            final Text name = buffer.readText();
            final World world = player.getEntityWorld();
            final BlockState state = world.getBlockState(pos);
            final Block block = state.getBlock();
            if (block instanceof InventoryProvider)
            {
                //return new ScrollableContainer(syncId, player.inventory, ((InventoryProvider) block).getInventory(state, world, pos), name);
                return new CContainer(null, syncId, ((InventoryProvider) block).getInventory(state, world, pos), player, name, 9, 6);
            }
            else
            {
                final BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof Inventory)
                {
                    return new CContainer(null, syncId, (Inventory) entity, player, name, 9, 6);
                }
            }
            return null;
        });
    }
}