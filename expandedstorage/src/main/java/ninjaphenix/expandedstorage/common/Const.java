package ninjaphenix.expandedstorage.common;

import net.minecraft.ChatFormatting;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class Const
{
    public static final ResourceLocation SCREEN_SELECT = resloc("screen_select");
    public static final ResourceLocation OPEN_SCREEN_SELECT = resloc("open_screen_select");
    public static final String MOD_ID = "expandedstorage";

    public static final MutableComponent LSRC = new TranslatableComponent(
            "tooltip.expandedstorage.left_shift_right_click",
            new KeybindComponent("key.sneak"),
            new KeybindComponent("key.use")).withStyle(ChatFormatting.GOLD);

    public static final ModelLayerLocation SINGLE_LAYER = new ModelLayerLocation(Const.resloc("single_chest"), "main");
    public static final ModelLayerLocation VANILLA_LEFT_LAYER = new ModelLayerLocation(Const.resloc("vanilla_left_chest"), "main");
    public static final ModelLayerLocation VANILLA_RIGHT_LAYER = new ModelLayerLocation(Const.resloc("vanilla_right_chest"), "main");
    public static final ModelLayerLocation TALL_TOP_LAYER = new ModelLayerLocation(Const.resloc("tall_top_chest"), "main");
    public static final ModelLayerLocation TALL_BOTTOM_LAYER = new ModelLayerLocation(Const.resloc("tall_bottom_chest"), "main");
    public static final ModelLayerLocation LONG_FRONT_LAYER = new ModelLayerLocation(Const.resloc("long_front_chest"), "main");
    public static final ModelLayerLocation LONG_BACK_LAYER = new ModelLayerLocation(Const.resloc("long_back_chest"), "main");

    public static ResourceLocation resloc(final String path) { return new ResourceLocation(MOD_ID, path); }
}