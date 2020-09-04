package torcherino;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import torcherino.api.Tier;
import torcherino.api.TierSupplier;
import torcherino.api.TorcherinoAPI;
import torcherino.api.blocks.entity.TorcherinoBlockEntity;
import torcherino.api.impl.TorcherinoImpl;
import torcherino.client.screen.TorcherinoScreen;

import java.util.HashMap;

import static torcherino.Torcherino.MOD_ID;

public class TorcherinoClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        // Open Torcherino Screen
        ClientSidePacketRegistry.INSTANCE.register(new ResourceLocation(MOD_ID, "ots"), (PacketContext context, FriendlyByteBuf buffer) ->
        {
            Level world = Minecraft.getInstance().level;
            BlockPos pos = buffer.readBlockPos();
            Component title = buffer.readComponent();
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
                    Minecraft.getInstance().setScreen(new TorcherinoScreen(title, xRange, zRange, yRange, speed, redstoneMode, pos,
                            ((TierSupplier) blockEntity).getTier()));
                }
            });
        });
        // Torcherino Tier Sync
        ClientSidePacketRegistry.INSTANCE.register(new ResourceLocation(MOD_ID, "tts"), (PacketContext context, FriendlyByteBuf buffer) ->
        {
            HashMap<ResourceLocation, Tier> tiers = new HashMap<>();
            int count = buffer.readInt();
            for (int i = 0; i < count; i++)
            {
                ResourceLocation id = buffer.readResourceLocation();
                int maxSpeed = buffer.readInt();
                int xzRange = buffer.readInt();
                int yRange = buffer.readInt();
                tiers.put(id, new Tier(maxSpeed, xzRange, yRange));
            }
            context.getTaskQueue().execute(() -> ((TorcherinoImpl) TorcherinoAPI.INSTANCE).setRemoteTiers(tiers));
        });
    }
}
