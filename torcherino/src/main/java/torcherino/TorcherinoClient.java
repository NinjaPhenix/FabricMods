package torcherino;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import torcherino.api.Tier;
import torcherino.api.TierSupplier;
import torcherino.api.TorcherinoAPI;
import torcherino.api.blocks.entity.TorcherinoBlockEntity;
import torcherino.api.impl.TorcherinoImpl;
import torcherino.client.screen.TorcherinoScreen;

import java.util.HashMap;

import static torcherino.Torcherino.MOD_ID;

@SuppressWarnings("SpellCheckingInspection")
public class TorcherinoClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        // Open Torcherino Screen
        ClientSidePacketRegistry.INSTANCE.register(new Identifier(MOD_ID, "ots"), (PacketContext context, PacketByteBuf buffer) ->
        {
            World world = MinecraftClient.getInstance().world;
            BlockPos pos = buffer.readBlockPos();
            Text title = buffer.readText();
            int xRange = buffer.readInt();
            int zRange = buffer.readInt();
            int yRange = buffer.readInt();
            int speed = buffer.readInt();
            int redstoneMode = buffer.readInt();
            context.getTaskQueue().execute(() ->
            {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof TorcherinoBlockEntity)
                {
                    MinecraftClient.getInstance().openScreen(new TorcherinoScreen(title, xRange, zRange, yRange, speed, redstoneMode, pos,
                            ((TierSupplier) blockEntity).getTier()));
                }
            });
        });
        // Torcherino Tier Sync
        ClientSidePacketRegistry.INSTANCE.register(new Identifier(MOD_ID, "tts"), (PacketContext context, PacketByteBuf buffer) ->
        {
            HashMap<Identifier, Tier> tiers = new HashMap<>();
            int count = buffer.readInt();
            for (int i = 0; i < count; i++)
            {
                Identifier id = buffer.readIdentifier();
                int maxSpeed = buffer.readInt();
                int xzRange = buffer.readInt();
                int yRange = buffer.readInt();
                tiers.put(id, new Tier(maxSpeed, xzRange, yRange));
            }
            context.getTaskQueue().execute(() -> ((TorcherinoImpl) TorcherinoAPI.INSTANCE).setRemoteTiers(tiers));
        });
    }
}
