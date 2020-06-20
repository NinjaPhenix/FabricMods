package ninjaphenix.expandedstorage.api;

import com.mojang.serialization.Lifecycle;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import ninjaphenix.expandedstorage.block.misc.CursedChestType;

/**
 * This class provides data registries for adding new chests to already defined chest types. This will likely be refactored in the future as new features and
 * chest types are added.
 *
 * @author NinjaPhenix
 * @since 4.0.0
 */
public final class Registries
{
    private static final RegistryKey<Registry<ChestTierData>> CHEST_KEY =
            RegistryKey.of(new Identifier("expandedstorageapi", "root"), new Identifier("expandedstorageapi", "chest"));
    /**
     * This registry for CursedChestBlock data storage.
     */
    public static final SimpleRegistry<ChestTierData> CHEST = new SimpleRegistry<>(CHEST_KEY, Lifecycle.experimental());
    private static final RegistryKey<Registry<TierData>> OLD_CHEST_KEY =
            RegistryKey.of(new Identifier("expandedstorageapi", "root"), new Identifier("expandedstorageapi", "old_chest"));
    /**
     * This registry is for OldChestBlock data storage.
     */
    public static final SimpleRegistry<TierData> OLD_CHEST = new SimpleRegistry<>(OLD_CHEST_KEY, Lifecycle.experimental());

    public static class ChestTierData extends TierData
    {
        private final Identifier singleTexture;
        private final Identifier vanillaTexture;
        private final Identifier tallTexture;
        private final Identifier longTexture;

        /**
         * Data representing a vanilla looking chest block.
         *
         * @param slots The amount of itemstacks this chest tier can hold.
         * @param containerName The default container name for this chest tier.
         * @param blockId The block id that represents this data.
         * @param singleTexture The blocks single texture.
         * @param vanillaTexture The blocks vanilla texture ( Vanilla double chests ).
         * @param tallTexture The blocks tall texture.
         * @param longTexture The blocks long texture.
         */
        public ChestTierData(int slots, Text containerName, Identifier blockId, Identifier singleTexture, Identifier vanillaTexture,
                Identifier tallTexture, Identifier longTexture)
        {
            super(slots, containerName, blockId);
            this.singleTexture = singleTexture;
            this.vanillaTexture = vanillaTexture;
            this.tallTexture = tallTexture;
            this.longTexture = longTexture;
        }

        /**
         * @param type The chest type to receive the texture for.
         * @return The texture relevant to the type.
         */
        public Identifier getChestTexture(CursedChestType type)
        {
            if (type == CursedChestType.BOTTOM || type == CursedChestType.TOP) { return tallTexture; }
            else if (type == CursedChestType.LEFT || type == CursedChestType.RIGHT) { return vanillaTexture; }
            else if (type == CursedChestType.FRONT || type == CursedChestType.BACK) { return longTexture; }
            return singleTexture;
        }
    }

    public static class TierData
    {
        private final int slots;
        private final Text containerName;
        private final Identifier blockId;

        /**
         * Data representing minimalist chest blocks such as OldChestBlock's.
         *
         * @param slots The amount of itemstacks this chest tier can hold.
         * @param containerName The default container name for this chest tier.
         * @param blockId The block id that represents this data.
         */
        public TierData(int slots, Text containerName, Identifier blockId)
        {
            this.slots = slots;
            this.containerName = containerName;
            this.blockId = blockId;
        }

        /**
         * @return The amount of slots the chest tier contains.
         */
        public int getSlotCount() { return slots; }

        /**
         * @return The default container name for the chest tier.
         */
        public Text getContainerName() { return containerName; }

        /**
         * @return The block that represents this data instance.
         */
        public Identifier getBlockId() { return blockId; }
    }
}
