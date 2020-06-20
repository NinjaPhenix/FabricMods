package ninjaphenix.containerlib.impl;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import ninjaphenix.containerlib.api.Constants;
import ninjaphenix.containerlib.api.ContainerLibraryAPI;
import ninjaphenix.containerlib.impl.client.ScreenMiscSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

import static ninjaphenix.containerlib.api.Constants.SCREEN_SELECT;

@ApiStatus.Internal
public class ContainerLibraryImpl implements ContainerLibraryAPI
{
    public static final ContainerLibraryImpl INSTANCE = new ContainerLibraryImpl();

    @Environment(EnvType.CLIENT)
    private final HashMap<Identifier, ScreenMiscSettings> screenMiscSettings = new HashMap<>();
    private final HashMap<Identifier, ArrayList<Consumer<?>>> screenSizeCallbacks = new HashMap<>();
    private final HashSet<Identifier> declaredContainerTypes = new HashSet<>();
    private final HashMap<UUID, Consumer<Identifier>> preferenceCallbacks = new HashMap<>();
    private final HashMap<UUID, Identifier> playerPreferences = new HashMap<>();
    private final Logger LOGGER = LogManager.getLogger("ninjaphenix-container-library-API");

    public boolean isContainerTypeDeclared(final Identifier containerTypeId)
    {
        Objects.requireNonNull(containerTypeId, "ContainerTypeImpl#isContainerTypeDeclared received null instead of an Identifier. (Container Type ID)");
        return declaredContainerTypes.contains(containerTypeId);
    }

    @ApiStatus.Internal
    public void setPlayerPreference(final PlayerEntity player, @Nullable final Identifier containerTypeId)
    {
        Objects.requireNonNull(player, "ContainerLibraryImpl#setPlayerPreference received null instead of a PlayerEntity.");
        final UUID uuid = player.getUuid();
        if (declaredContainerTypes.contains(containerTypeId))
        {
            playerPreferences.put(uuid, containerTypeId);
            if (preferenceCallbacks.containsKey(uuid))
            {
                preferenceCallbacks.get(uuid).accept(containerTypeId);
            }
        }
        else
        {
            if(containerTypeId != null && containerTypeId.equals(Constants.id("auto")))
            {
                preferenceCallbacks.remove(uuid);
            }
            else {
                playerPreferences.remove(uuid);
                preferenceCallbacks.remove(uuid);
            }
        }
    }

    @Override
    public void openContainer(final PlayerEntity player, final BlockPos pos, final Text containerName)
    {
        Objects.requireNonNull(player, "ContainerLibraryAPI#openContainer received null instead of a PlayerEntity.");
        Objects.requireNonNull(pos, "ContainerLibraryAPI#openContainer received null instead of a BlockPos.");
        Objects.requireNonNull(containerName, "ContainerLibraryAPI#declareContainerType received null instead of a Text. (Container Name)");
        final UUID uuid = player.getUuid();
        Identifier playerPreference;
        if (playerPreferences.containsKey(uuid) &&
                declaredContainerTypes.contains(playerPreference = playerPreferences.get(uuid)) /*&&
                ContainerProviderRegistry.INSTANCE.factoryExists(playerPreference)*/)
        {
            openContainer(player, playerPreference, pos, containerName);
            preferenceCallbacks.put(player.getUuid(), (type) -> openContainer(player, type, pos, containerName));
        }
        else
        {
            openSelectScreen(player, (type) -> openContainer(player, type, pos, containerName));
        }
    }

    @ApiStatus.Internal
    public void openSelectScreen(final PlayerEntity player, @Nullable final Consumer<Identifier> playerPreferenceCallback)
    {
        Objects.requireNonNull(player, "ContainerLibraryImpl#openSelectScreen received null instead of a PlayerEntity.");
        if (playerPreferenceCallback != null) { preferenceCallbacks.put(player.getUuid(), playerPreferenceCallback); }
        final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeInt(declaredContainerTypes.size());
        declaredContainerTypes.forEach(buffer::writeIdentifier);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, SCREEN_SELECT, buffer);
    }

    @Override
    public boolean declareContainerType(final Identifier containerTypeId, final Identifier selectTextureId, final Text narrationMessage)
    {
        Objects.requireNonNull(containerTypeId, "ContainerLibraryAPI#declareContainerType received null instead of an Identifier. (Container Type ID)");
        Objects.requireNonNull(selectTextureId, "ContainerLibraryAPI#declareContainerType received null instead of an Identifier. (Select Texture ID)");
        Objects.requireNonNull(narrationMessage, "ContainerLibraryAPI#declareContainerType received null instead of a Text. (Narration Message)");
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
        { screenMiscSettings.put(containerTypeId, new ScreenMiscSettings(selectTextureId, narrationMessage)); }
        return declaredContainerTypes.add(containerTypeId);
    }

    @Override
    public <T> void declareScreenSizeRegisterCallback(final Identifier containerTypeId, final Consumer<T> sizeConsumer)
    {
        Objects.requireNonNull(containerTypeId,
                "ContainerLibraryAPI#declareScreenSizeRegisterCallback received null instead of an Identifier. (Container Type ID)");
        Objects.requireNonNull(sizeConsumer, "ContainerLibraryAPI#declareScreenSizeRegisterCallback received null instead of a Consumer. (Size Consumer)");
        if(!screenSizeCallbacks.containsKey(containerTypeId)) {
            screenSizeCallbacks.put(containerTypeId, new ArrayList<>());
        }
        screenSizeCallbacks.get(containerTypeId).add(sizeConsumer);
    }

    @Override
    public <T> void declareScreenSize(final Identifier containerTypeId, final T screenSize)
    {
        Objects.requireNonNull(containerTypeId, "ContainerLibraryAPI#declareScreenSize received null instead of an Identifier. (Container Type ID)");
        Objects.requireNonNull(screenSize, "ContainerLibraryAPI#declareScreenSize received null instead of an GenericType. (Screen Size)");
        if(!screenSizeCallbacks.containsKey(containerTypeId)) {
            LOGGER.warn("[ninjaphenix-container-library-API] Failed to register new screen size for " + containerTypeId +
                    ", it either does not exist or the screen size is being registered too early. If you are a modder, consider using the ContainerLibraryExtension entry point.");
        }
        else {
            screenSizeCallbacks.get(containerTypeId).forEach((Consumer consumer) ->
            {
                consumer.accept(screenSize);
            });
        }
    }

    @Environment(EnvType.CLIENT)
    public ScreenMiscSettings getScreenSettings(final Identifier containerTypeId)
    {
        Objects.requireNonNull(containerTypeId, "ContainerLibraryImpl#getScreenSettings received null instead of an Identifier. (Container Type ID)");
        return screenMiscSettings.get(containerTypeId);
    }

    private void openContainer(final PlayerEntity player, final Identifier type, final BlockPos pos, final Text containerName)
    {
        ContainerProviderRegistry.INSTANCE.openContainer(type, player, buf ->
        {
            buf.writeBlockPos(pos);
            buf.writeText(containerName);
        });
    }
}
