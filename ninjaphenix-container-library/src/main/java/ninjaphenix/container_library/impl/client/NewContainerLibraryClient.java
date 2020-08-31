package ninjaphenix.container_library.impl.client;

import com.google.common.collect.ImmutableList;
import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ninjaphenix.container_library.api.client.Config;
import ninjaphenix.container_library.impl.client.screen.SelectContainerScreen;
import ninjaphenix.container_library.impl.common.Const;
import ninjaphenix.container_library.impl.common.Networking;
import ninjaphenix.container_library.impl.common.NewContainerLibrary;

import static ninjaphenix.container_library.api.common.inventory.AbstractScreenHandler.ScreenMeta;

public class NewContainerLibraryClient
{
    public static NewContainerLibraryClient INSTANCE = new NewContainerLibraryClient();
    public final Config CONFIG = new Config(); // todo: load config
    private final Map<Identifier, Pair<Identifier, Text>> containerPickButtonOptions = new HashMap<>();
    private final Map<Identifier, Pair<Identifier, Text>> supportedPickButtonOptions = new HashMap<>();
    private Set<Identifier> supportedContainerTypes;
    private Map<Identifier, Map<Integer, ScreenMeta>> screenSizes;

    private NewContainerLibraryClient() { }

    /**
     * Block at the specified pos must implement {@link net.minecraft.block.InventoryProvider} and
     */
    public void openContainer(final BlockState state, final World world, final BlockPos pos)
    {
        final Identifier preference = CONFIG.preferred_container_type;
        if (Const.id("auto").equals(preference) || !supportedContainerTypes.contains(preference))
        {
            MinecraftClient.getInstance().openScreen(new SelectContainerScreen(supportedPickButtonOptions, id -> openContainer(state, world, pos, id)));
        }
        openContainer(state, world, pos, preference);
    }

    private void openContainer(final BlockState state, final World world, final BlockPos pos, final Identifier preference)
    {
        final Block block = world.getBlockState(pos).getBlock();
        if (block instanceof InventoryProvider)
        {
            final int inventorySize = ((InventoryProvider) block).getInventory(state, world, pos).size();
            final ScreenMeta meta = getNearestValue(screenSizes.get(preference), inventorySize);
            if (meta != null)
            {
                Networking.INSTANCE.requestContainerOpen(pos, preference, meta);
            }
        }
    }

    // Server should send this when a player connects
    public void setServerSupportedContainers(final Set<Identifier> serverContainerTypes)
    {
        final Set<Identifier> clientContainerTypes = NewContainerLibrary.INSTANCE.getContainerTypes();
        clientContainerTypes.retainAll(serverContainerTypes);
        supportedContainerTypes = clientContainerTypes;
        supportedPickButtonOptions.clear();
        for (final Identifier supportedContainerType : supportedContainerTypes)
        {
            supportedPickButtonOptions.put(supportedContainerType, containerPickButtonOptions.get(supportedContainerType));
        }

    }

    // for now this will be hard coded but will make it data driven
    public void setScreenSizes(final Map<Identifier, Map<Integer, ScreenMeta>> sizes) { screenSizes = sizes; }

    private <V extends ScreenMeta> V getNearestValue(final Map<Integer, V> map, final Integer searchKey)
    {
        if (map.containsKey(searchKey)) { return map.get(searchKey); }
        final List<Integer> keys = ImmutableList.copyOf(map.keySet());
        final int index = Collections.binarySearch(keys, searchKey);
        final int largestKey = keys.get(Math.abs(index) - 1);
        final V nearestMeta = map.get(largestKey);
        if (nearestMeta != null && largestKey > searchKey && largestKey - searchKey <= nearestMeta.WIDTH) { return nearestMeta; }
        throw new RuntimeException("No screen can show an inventory of size " + searchKey + ".");
    }

    public void declareContainerPickButton(final Identifier id, final Identifier texture, final Text name)
    {
        containerPickButtonOptions.putIfAbsent(id, new Pair<>(texture, name));
    }

    public <T extends ScreenMeta> T getScreenSize(final Identifier id, final int inventorySize)
    {
        //noinspection unchecked
        return (T) screenSizes.get(id).get(inventorySize);
    }
}