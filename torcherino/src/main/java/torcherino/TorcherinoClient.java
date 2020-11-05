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
            final Level world = Minecraft.getInstance().level;
            final BlockPos pos = buffer.readBlockPos();
            final Component title = buffer.readComponent();
            final int xRange = buffer.readInt();
            final int zRange = buffer.readInt();
            final int yRange = buffer.readInt();
            final int speed = buffer.readInt();
            final int redstoneMode = buffer.readInt();
            context.getTaskQueue().execute(() ->
            {
                final BlockEntity blockEntity = world.getBlockEntity(pos);
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
            final HashMap<ResourceLocation, Tier> tiers = new HashMap<>();
            final int count = buffer.readInt();
            for (int i = 0; i < count; i++)
            {
                final ResourceLocation id = buffer.readResourceLocation();
                final int maxSpeed = buffer.readInt();
                final int xzRange = buffer.readInt();
                final int yRange = buffer.readInt();
                tiers.put(id, new Tier(maxSpeed, xzRange, yRange));
            }
            context.getTaskQueue().execute(() -> ((TorcherinoImpl) TorcherinoAPI.INSTANCE).setRemoteTiers(tiers));
        });
    }
}