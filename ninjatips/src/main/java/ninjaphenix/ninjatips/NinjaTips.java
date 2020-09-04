package ninjaphenix.ninjatips;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NinjaTips implements ModInitializer
{
    public static final Logger logger = LogManager.getLogger();
    public static final String MOD_ID = "ninjatips";
    public static final ResourceLocation ITEM_CHAT = new ResourceLocation(MOD_ID, "item_chat");

    @Override
    public void onInitialize()
    {
        CommandRegistrationCallback.EVENT.register(NbtCommand::register);
        ServerSidePacketRegistry.INSTANCE.register(ITEM_CHAT, (ctx, buf) -> {
            final ItemStack stack = buf.readItem();
            final Player player = ctx.getPlayer();
            final MutableComponent message = new TextComponent("<").append(player.getDisplayName()).append("> ");
            if (stack.isStackable()) { message.append(stack.getCount() + "x "); }
            message.append(stack.getDisplayName());
            player.getServer().getPlayerList().broadcastMessage(message, ChatType.CHAT, Util.NIL_UUID);
        });
    }
}