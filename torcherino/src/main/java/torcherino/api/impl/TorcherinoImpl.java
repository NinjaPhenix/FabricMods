package torcherino.api.impl;

import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;

import java.util.HashMap;
import java.util.HashSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * DO NOT USE THIS CLASS DIRECTLY. Use TorcherinoAPI.INSTANCE instead.
 */
public class TorcherinoImpl implements TorcherinoAPI
{
    @Deprecated
    public static final TorcherinoImpl INSTANCE = new TorcherinoImpl();

    private final Logger LOGGER = LogManager.getLogger("torcherino-api");
    private final HashMap<ResourceLocation, Tier> localTiers;
    private final HashSet<ResourceLocation> blacklistedBlocks;
    private final HashSet<ResourceLocation> blacklistedBlockEntities;
    private HashMap<ResourceLocation, Tier> remoteTiers;

    private TorcherinoImpl()
    {
        localTiers = new HashMap<>();
        blacklistedBlocks = new HashSet<>();
        blacklistedBlockEntities = new HashSet<>();
    }

    @Override
    public ImmutableMap<ResourceLocation, Tier> getTiers() { return ImmutableMap.copyOf(localTiers); }

    @Override
    public Tier getTier(final ResourceLocation tierRl) { return remoteTiers.getOrDefault(tierRl, null); }

    @Override
    public boolean registerTier(final ResourceLocation tierRl, final int maxSpeed, final int xzRange, final int yRange)
    {
        Tier tier = new Tier(maxSpeed, xzRange, yRange);
        if (localTiers.containsKey(tierRl))
        {
            LOGGER.error("[Torcherino] Tier with id {} has already been declared.", tierRl);
            return false;
        }
        localTiers.put(tierRl, tier);
        return true;
    }

    @Override
    public boolean blacklistBlock(final ResourceLocation block)
    {
        if (blacklistedBlocks.contains(block))
        {
            LOGGER.warn("[Torcherino] Block with id {} has already been blacklisted.", block);
            return false;
        }
        blacklistedBlocks.add(block);
        return true;
    }

    @Override
    public boolean blacklistBlock(final Block block)
    {
        ResourceLocation blockRl = Registry.BLOCK.getKey(block);
        if (Registry.BLOCK.get(blockRl) != block)
        {
            LOGGER.error("[Torcherino] Please register your block before attempting to blacklist.");
            return false;
        }
        else if (blacklistedBlocks.contains(blockRl))
        {
            LOGGER.warn("[Torcherino] Block with id {} has already been blacklisted.", blockRl);
            return false;
        }
        blacklistedBlocks.add(blockRl);
        return true;
    }

    @Override
    public boolean blacklistBlockEntity(final ResourceLocation blockEntity)
    {
        if (blacklistedBlockEntities.contains(blockEntity))
        {
            LOGGER.warn("[Torcherino] Block entity with id {} has already been blacklisted.", blockEntity);
            return false;
        }
        blacklistedBlockEntities.add(blockEntity);
        return true;
    }

    @Override
    public boolean blacklistBlockEntity(final BlockEntityType<?> blockEntityType)
    {
        ResourceLocation blockEntity = Registry.BLOCK_ENTITY_TYPE.getKey(blockEntityType);
        if (blockEntity == null)
        {
            LOGGER.error("[Torcherino] Please register your block entity type before attempting to blacklist.");
            return false;
        }
        else if (blacklistedBlockEntities.contains(blockEntity))
        {
            LOGGER.warn("[Torcherino] Block entity with id {} has already been blacklisted.", blockEntity);
            return false;
        }
        blacklistedBlockEntities.add(blockEntity);
        return true;
    }

    @Override
    public boolean isBlockBlacklisted(final Block block) { return blacklistedBlocks.contains(Registry.BLOCK.getKey(block)); }

    @Override
    public boolean isBlockEntityBlacklisted(final BlockEntityType<?> blockEntityType)
    {
        return blacklistedBlockEntities.contains(BlockEntityType.getKey(blockEntityType));
    }

    // Internal do not use.
    public void setRemoteTiers(final HashMap<ResourceLocation, Tier> tiers) { remoteTiers = tiers; }
}
