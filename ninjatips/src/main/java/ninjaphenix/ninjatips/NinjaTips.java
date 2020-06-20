package ninjaphenix.ninjatips;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NinjaTips implements ModInitializer
{
    public static final Logger logger = LogManager.getLogger();
    public static final String MOD_ID = "ninjatips";
    public static final Identifier ITEM_CHAT = new Identifier(MOD_ID, "item_chat");

    @Override
    public void onInitialize()
    {
        CommandRegistry.INSTANCE.register(false, NbtCommand::register);
        ServerSidePacketRegistry.INSTANCE.register(ITEM_CHAT, (ctx, buf) -> {
            final ItemStack stack = buf.readItemStack();
            final PlayerEntity player = ctx.getPlayer();
            final Text message = new LiteralText("<").append(player.getDisplayName()).append("> ");
            if (stack.isStackable()) { message.append(stack.getCount() + "x "); }
            message.append(stack.toHoverableText());
            player.getServer().getPlayerManager().broadcastChatMessage(message, false);
        });
    }
}
