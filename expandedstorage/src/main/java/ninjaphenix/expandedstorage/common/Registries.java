package ninjaphenix.expandedstorage.common;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.common.misc.CursedChestType;

import java.util.function.Function;

public final class Registries
{
    private static final ResourceKey<Registry<ChestTierData>> CHEST_KEY = ResourceKey.create(Const.resloc("root"), Const.resloc("chest"));
    public static final MappedRegistry<ChestTierData> CHEST = new MappedRegistry<>(CHEST_KEY, Lifecycle.experimental());
    private static final ResourceKey<Registry<TierData>> OLD_CHEST_KEY = ResourceKey.create(Const.resloc("root"), Const.resloc("old_chest"));
    public static final MappedRegistry<TierData> OLD_CHEST = new MappedRegistry<>(OLD_CHEST_KEY, Lifecycle.experimental());
    private static final ResourceKey<Registry<TierData>> BARREL_KEY = ResourceKey.create(Const.resloc("root"), Const.resloc("barrel"));
    public static final MappedRegistry<TierData> BARREL = new MappedRegistry<>(BARREL_KEY, Lifecycle.experimental());

    public static class ChestTierData extends TierData
    {
        private final ResourceLocation singleTexture, topTexture, backTexture, rightTexture, bottomTexture, frontTexture, leftTexture;

        public ChestTierData(final int slotCount, final Component containerName, final ResourceLocation resourceLocation,
                             final Function<CursedChestType, ResourceLocation> textureFunction)
        {
            super(slotCount, containerName, resourceLocation);
            singleTexture = textureFunction.apply(CursedChestType.SINGLE);
            topTexture = textureFunction.apply(CursedChestType.TOP);
            backTexture = textureFunction.apply(CursedChestType.BACK);
            rightTexture = textureFunction.apply(CursedChestType.RIGHT);
            bottomTexture = textureFunction.apply(CursedChestType.BOTTOM);
            frontTexture = textureFunction.apply(CursedChestType.FRONT);
            leftTexture = textureFunction.apply(CursedChestType.LEFT);
        }

        public ResourceLocation getChestTexture(final CursedChestType type)
        {
            switch (type)
            {
                case TOP: return topTexture;
                case BACK: return backTexture;
                case RIGHT: return rightTexture;
                case BOTTOM: return bottomTexture;
                case FRONT: return frontTexture;
                case LEFT: return leftTexture;
                default: return singleTexture;
            }
        }
    }

    public static class TierData
    {
        public final int SLOT_COUNT;
        public final Component CONTAINER_NAME;
        public final ResourceLocation RESOURCE_LOCATION;

        public TierData(final int slotCount, final Component containerName, final ResourceLocation resourceLocation)
        {
            SLOT_COUNT = slotCount;
            CONTAINER_NAME = containerName;
            RESOURCE_LOCATION = resourceLocation;
        }
    }
}