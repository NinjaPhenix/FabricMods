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
    public Tier getTier(ResourceLocation tierIdentifier) { return remoteTiers.getOrDefault(tierIdentifier, null); }

    @Override
    public boolean registerTier(ResourceLocation tierIdentifier, int maxSpeed, int xzRange, int yRange)
    {
        Tier tier = new Tier(maxSpeed, xzRange, yRange);
        if (localTiers.containsKey(tierIdentifier))
        {
            LOGGER.error("[Torcherino] Tier with id {} has already been declared.", tierIdentifier);
            return false;
        }
        localTiers.put(tierIdentifier, tier);
        return true;
    }

    @Override
    public boolean blacklistBlock(ResourceLocation blockIdentifier)
    {
        if (blacklistedBlocks.contains(blockIdentifier))
        {
            LOGGER.warn("[Torcherino] Block with id {} has already been blacklisted.", blockIdentifier);
            return false;
        }
        blacklistedBlocks.add(blockIdentifier);
        return true;
    }

    @Override
    public boolean blacklistBlock(Block block)
    {
        ResourceLocation blockIdentifier = Registry.BLOCK.getKey(block);
        if (Registry.BLOCK.get(blockIdentifier) != block)
        {
            LOGGER.error("[Torcherino] Please register your block before attempting to blacklist.");
            return false;
        }
        else if (blacklistedBlocks.contains(blockIdentifier))
        {
            LOGGER.warn("[Torcherino] Block with id {} has already been blacklisted.", blockIdentifier);
            return false;
        }
        blacklistedBlocks.add(blockIdentifier);
        return true;
    }

    @Override
    public boolean blacklistBlockEntity(ResourceLocation blockEntityIdentifier)
    {
        if (blacklistedBlockEntities.contains(blockEntityIdentifier))
        {
            LOGGER.warn("[Torcherino] Block entity with id {} has already been blacklisted.", blockEntityIdentifier);
            return false;
        }
        blacklistedBlockEntities.add(blockEntityIdentifier);
        return true;
    }

    @Override
    public boolean blacklistBlockEntity(BlockEntityType<?> blockEntityType)
    {
        ResourceLocation blockEntityTypeIdentifier = Registry.BLOCK_ENTITY_TYPE.getKey(blockEntityType);
        if (blockEntityTypeIdentifier == null)
        {
            LOGGER.error("[Torcherino] Please register your block entity type before attempting to blacklist.");
            return false;
        }
        else if (blacklistedBlockEntities.contains(blockEntityTypeIdentifier))
        {
            LOGGER.warn("[Torcherino] Block entity with id {} has already been blacklisted.", blockEntityTypeIdentifier);
            return false;
        }
        blacklistedBlockEntities.add(blockEntityTypeIdentifier);
        return true;
    }

    @Override
    public boolean isBlockBlacklisted(Block block) { return blacklistedBlocks.contains(Registry.BLOCK.getKey(block)); }

    @Override
    public boolean isBlockEntityBlacklisted(BlockEntityType<?> blockEntityType)
    {
        return blacklistedBlockEntities.contains(BlockEntityType.getKey(blockEntityType));
    }

    // Internal do not use.
    public void setRemoteTiers(HashMap<ResourceLocation, Tier> tiers) { remoteTiers = tiers; }
}
