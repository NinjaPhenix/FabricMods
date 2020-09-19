package ninjaphenix.expandedstorage.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class Const
{
    public static final ResourceLocation SCREEN_SELECT = resloc("screen_select");
    public static final ResourceLocation OPEN_SCREEN_SELECT = resloc("open_screen_select");
    public static final String MOD_ID = "expandedstorage";

    public static final MutableComponent leftShiftRightClick = new TranslatableComponent("tooltip.expandedstorage.left_shift_right_click",
            new KeybindComponent("key.sneak"), new KeybindComponent("key.use")).withStyle(ChatFormatting.GOLD);

    public static ResourceLocation resloc(final String path) { return new ResourceLocation(MOD_ID, path); }
}