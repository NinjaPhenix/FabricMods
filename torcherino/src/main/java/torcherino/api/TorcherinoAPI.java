package torcherino.api;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import torcherino.api.impl.TorcherinoImpl;

/**
 * @author NinjaPhenix
 * @since 1.9.51
 */
@SuppressWarnings({ "UnusedReturnValue", "unused" })
public interface TorcherinoAPI
{
    /**
     * The Implementation of the API, you should use this for all methods. e.g. TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.STONE)
     */
    @SuppressWarnings("deprecation")
    TorcherinoAPI INSTANCE = TorcherinoImpl.INSTANCE;

    /**
     * @return Immutable map of tierID -> tier
     * @since 1.9.51
     */
    ImmutableMap<ResourceLocation, Tier> getTiers();

    /**
     * Returns the tier for the given tierName.
     *
     * @param tierIdentifier The tier name to retrieve.
     * @return The tier or null if it does not exist.
     * @since 1.9.51
     */
    Tier getTier(ResourceLocation tierIdentifier);

    /**
     * @param tierIdentifier Identifier for the new tier.
     * @param maxSpeed The max speed blocks of this tier should have.
     * @param xzRange The max range horizontally blocks of this tier should have.
     * @param yRange The max range vertically blocks of this tier should have.
     * @return TRUE if the tier was registered, FALSE if tier with same name exists.
     * @since 1.9.51
     */
    boolean registerTier(ResourceLocation tierIdentifier, int maxSpeed, int xzRange, int yRange);

    /**
     * @param blockIdentifier The Identifier of the block to be blacklisted.
     * @return TRUE if added to blacklist, FALSE if already on blacklist.
     * @since 1.9.51
     */
    boolean blacklistBlock(ResourceLocation blockIdentifier);

    /**
     * @param block The block to be blacklisted.
     * @return TRUE if added to blacklist, FALSE if already on blacklist.
     * @since 1.9.51
     */
    boolean blacklistBlock(Block block);

    /**
     * @param blockEntityIdentifier The Identifier of the block entity to be blacklisted.
     * @return TRUE if added to blacklist, FALSE if already on blacklist.
     * @since 1.9.51
     */
    boolean blacklistBlockEntity(ResourceLocation blockEntityIdentifier);

    /**
     * @param blockEntityType The block entity type to be blacklisted.
     * @return TRUE if added to blacklist, FALSE if already on blacklist.
     * @since 1.9.51
     */
    boolean blacklistBlockEntity(BlockEntityType<?> blockEntityType);

    /**
     * @param block The block to check is blacklisted.
     * @return TRUE if blacklisted, FALSE otherwise.
     * @since 1.9.51
     */
    boolean isBlockBlacklisted(Block block);

    /**
     * @param blockEntityType The block entity type to check is blacklisted.
     * @return TRUE if blacklisted, FALSE otherwise.
     * @since 1.9.51
     */
    boolean isBlockEntityBlacklisted(BlockEntityType<?> blockEntityType);
}
