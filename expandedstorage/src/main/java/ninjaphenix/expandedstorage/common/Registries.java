package ninjaphenix.expandedstorage.common;

import com.mojang.serialization.Lifecycle;
import ninjaphenix.expandedstorage.common.misc.CursedChestType;

import java.util.function.Function;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class Registries
{
    private static final ResourceKey<Registry<ChestTierData>> CHEST_KEY = ResourceKey.create(Const.id("root"), Const.id("chest"));
    public static final MappedRegistry<ChestTierData> CHEST = new MappedRegistry<>(CHEST_KEY, Lifecycle.experimental());
    private static final ResourceKey<Registry<TierData>> OLD_CHEST_KEY = ResourceKey.create(Const.id("root"), Const.id("old_chest"));
    public static final MappedRegistry<TierData> OLD_CHEST = new MappedRegistry<>(OLD_CHEST_KEY, Lifecycle.experimental());
    private static final ResourceKey<Registry<TierData>> BARREL_KEY = ResourceKey.create(Const.id("root"), Const.id("barrel"));
    public static final MappedRegistry<TierData> BARREL = new MappedRegistry<>(BARREL_KEY, Lifecycle.experimental());

    public static class ChestTierData extends TierData
    {
        private final ResourceLocation singleTexture, topTexture, backTexture, rightTexture, bottomTexture, frontTexture, leftTexture;

        public ChestTierData(final int slots, final Component containerName, final ResourceLocation blockId,
                             final Function<CursedChestType, ResourceLocation> textureFunction)
        {
            super(slots, containerName, blockId);
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
            switch(type) {

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
        private final int slots;
        private final Component containerName;
        private final ResourceLocation blockId;

        public TierData(final int slots, final Component containerName, final ResourceLocation blockId)
        {
            this.slots = slots;
            this.containerName = containerName;
            this.blockId = blockId;
        }

        public int getSlotCount() { return slots; }

        public Component getContainerName() { return containerName; }

        public ResourceLocation getBlockId() { return blockId; }
    }
}