package torcherino;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
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

@SuppressWarnings("SpellCheckingInspection")
public class Torcherino implements ModInitializer, TorcherinoInitializer
{
    public static final String MOD_ID = "torcherino";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final HashSet<String> allowedUuids = new HashSet<>();
    public static ArrayList<DefaultParticleType> particles = new ArrayList<>();

    public static boolean hasIsOnline(String uuid) { return allowedUuids.contains(uuid); }

    @Override
    public void onInitialize()
    {
        Config.initialize();
        TorcherinoAPI.INSTANCE.getTiers().forEach((id, tier) ->
        {
            if (!id.getNamespace().equals(MOD_ID)) { return; }
            String path = id.getPath() + "_flame";
            if (path.equals("normal_flame")) { path = "flame"; }
            particles.add(Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, path), new torcherino.particle.DefaultParticleType(false)));
        });
        ModBlocks.INSTANCE.initialize();
        ServerSidePacketRegistry.INSTANCE.register(new Identifier(Torcherino.MOD_ID, "utv"), (PacketContext context, PacketByteBuf buffer) ->
        {
            World world = context.getPlayer().getEntityWorld();
            BlockPos pos = buffer.readBlockPos();
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
            allowedUuids.add(player.getUuidAsString());
            ImmutableMap<Identifier, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
            PacketByteBuf packetBuffer = new PacketByteBuf(Unpooled.buffer());
            packetBuffer.writeInt(tiers.size());
            tiers.forEach((id, tier) ->
            {
                packetBuffer.writeIdentifier(id);
                packetBuffer.writeInt(tier.getMaxSpeed());
                packetBuffer.writeInt(tier.getXZRange());
                packetBuffer.writeInt(tier.getYRange());
            });
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new Identifier(Torcherino.MOD_ID, "tts"), packetBuffer);
        });
        PlayerDisconnectCallback.EVENT.register(player ->
        {
            if (Config.INSTANCE.online_mode.equals("ONLINE")) { allowedUuids.remove(player.getUuidAsString()); }
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
            TorcherinoAPI.INSTANCE.blacklistBlockEntity(new Identifier("computercraft", "turtle_normal"));
            TorcherinoAPI.INSTANCE.blacklistBlockEntity(new Identifier("computercraft", "turtle_advanced"));
        }
    }
}
