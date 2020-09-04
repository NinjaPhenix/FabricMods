package ninjaphenix.ninjatips;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.minecraft.commands.Commands.literal;

public class NbtCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean isDedicated)
    {
        dispatcher.register(literal("nbt")
                .then(literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(context ->
                {
                    final BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                    final ServerPlayer player = context.getSource().getPlayerOrException();
                    return dumpBlockNBT(pos, player);
                })))
                .then(literal("hand").executes(context ->
                {
                    final ServerPlayer player = context.getSource().getPlayerOrException();
                    final ItemStack hand = player.getItemInHand(InteractionHand.MAIN_HAND);
                    final ItemStack off_hand = player.getItemInHand(InteractionHand.OFF_HAND);
                    if (hand.isEmpty() && !off_hand.isEmpty()) { return dumpItemStackNbt(off_hand, player); }
                    return dumpItemStackNbt(hand, player);
                }).then(literal("main").executes(context ->
                {
                    final ServerPlayer player = context.getSource().getPlayerOrException();
                    final ItemStack hand = player.getItemInHand(InteractionHand.MAIN_HAND);
                    return dumpItemStackNbt(hand, player);
                })).then(literal("off").executes(context ->
                {
                    final ServerPlayer player = context.getSource().getPlayerOrException();
                    final ItemStack hand = player.getItemInHand(InteractionHand.OFF_HAND);
                    return dumpItemStackNbt(hand, player);
                }))));
    }

    private static int dumpItemStackNbt(ItemStack stack, ServerPlayer player)
    {
        final MutableComponent ninjaTips = new TextComponent("").append(new TextComponent("[Ninja Tips] ").withStyle(ChatFormatting.BLUE));
        if (stack.isEmpty())
        {
            player.sendMessage(ninjaTips.append(new TranslatableComponent("ninjatips.text.handempty")), ChatType.CHAT, Util.NIL_UUID);
            return SINGLE_SUCCESS;
        }
        if (!stack.hasTag())
        {
            player.sendMessage(ninjaTips.append(new TranslatableComponent("ninjatips.text.handnonbt")), ChatType.CHAT, Util.NIL_UUID);
            return SINGLE_SUCCESS;
        }
        player.sendMessage(ninjaTips.append(getItemText(stack, true)), ChatType.CHAT, Util.NIL_UUID);
        return SINGLE_SUCCESS;
    }

    private static Component getItemText(ItemStack stack, boolean copyText)
    {
        MutableComponent text = new TextComponent("").append(stack.getDisplayName());
        if (copyText && !stack.getTag().isEmpty())
        {
            text.append(new TextComponent(" ")
                    .append(new TranslatableComponent("ninjatips.text.clicktocopy").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.ITALIC).withStyle(
                            style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, stack.getTag().getAsString())))));
        }
        return text;
    }

    private static int dumpBlockNBT(BlockPos pos, ServerPlayer player)
    {
        final ServerLevel world = player.getLevel();
        final ItemStack stack = new ItemStack(world.getBlockState(pos).getBlock(), 1);
        final BlockEntity entity = world.getBlockEntity(pos);
        stack.setTag(entity != null ? entity.save(new CompoundTag()) : new CompoundTag());
        player.sendMessage(new TextComponent("").append(new TextComponent("[Ninja Tips] ").withStyle(ChatFormatting.BLUE)).
                append(getItemText(stack, true)), ChatType.CHAT, Util.NIL_UUID);
        return SINGLE_SUCCESS;
    }
}