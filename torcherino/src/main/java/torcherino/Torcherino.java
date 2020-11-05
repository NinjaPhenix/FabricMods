package torcherino;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import ninjaphenix.chainmail.api.events.PlayerConnectCallback;
import ninjaphenix.chainmail.api.events.PlayerDisconnectCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.api.blocks.entity.TorcherinoBlockEntity;
import torcherino.api.entrypoints.TorcherinoInitializer;
import torcherino.blocks.ModBlocks;
import torcherino.config.Config;

import java.util.ArrayList;
import java.util.HashSet;

public class Torcherino implements ModInitializer, TorcherinoInitializer
{
    public static final String MOD_ID = "torcherino";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final HashSet<String> allowedUuids = new HashSet<>();
    public static ArrayList<SimpleParticleType> particles = new ArrayList<>();

    public static boolean hasIsOnline(final String uuid) { return allowedUuids.contains(uuid); }

    @Override
    public void onInitialize()
    {
        Config.initialize();
        TorcherinoAPI.INSTANCE.getTiers().forEach((id, tier) ->
        {
            if (!id.getNamespace().equals(MOD_ID)) { return; }
            String path = id.getPath() + "_flame";
            if (path.equals("normal_flame")) { path = "flame"; }
            particles.add(Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(MOD_ID, path), new torcherino.particle.DefaultParticleType(false)));
        });
        ModBlocks.INSTANCE.initialize();
        ServerSidePacketRegistry.INSTANCE.register(new ResourceLocation(Torcherino.MOD_ID, "utv"), (PacketContext context, FriendlyByteBuf buffer) ->
        {
            final Level world = context.getPlayer().getCommandSenderWorld();
            final BlockPos pos = buffer.readBlockPos();
            buffer.retain();
            context.getTaskQueue().execute(() ->
            {
                try
                {
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (blockEntity instanceof TorcherinoBlockEntity) { ((TorcherinoBlockEntity) blockEntity).readClientData(buffer); }
                }
                finally
                {
                    buffer.release();
                }
            });
        });
        FabricLoader.getInstance().getEntrypoints("torcherinoInitializer", TorcherinoInitializer.class).forEach(TorcherinoInitializer::onTorcherinoInitialize);
        PlayerConnectCallback.EVENT.register(player ->
        {
            allowedUuids.add(player.getStringUUID());
            final ImmutableMap<ResourceLocation, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
            final FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
            packetBuffer.writeInt(tiers.size());
            tiers.forEach((id, tier) ->
            {
                packetBuffer.writeResourceLocation(id);
                packetBuffer.writeInt(tier.getMaxSpeed());
                packetBuffer.writeInt(tier.getXZRange());
                packetBuffer.writeInt(tier.getYRange());
            });
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new ResourceLocation(Torcherino.MOD_ID, "tts"), packetBuffer);
        });
        PlayerDisconnectCallback.EVENT.register(player ->
        {
            if (Config.INSTANCE.online_mode.equals("ONLINE")) { allowedUuids.remove(player.getStringUUID()); }
        });
    }

    @Override
    public void onTorcherinoInitialize()
    {
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.WATER);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.LAVA);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.AIR);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.CAVE_AIR);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.VOID_AIR);
        if (FabricLoader.getInstance().isModLoaded("computercraft"))
        {
            TorcherinoAPI.INSTANCE.blacklistBlockEntity(new ResourceLocation("computercraft", "turtle_normal"));
            TorcherinoAPI.INSTANCE.blacklistBlockEntity(new ResourceLocation("computercraft", "turtle_advanced"));
        }
    }
}