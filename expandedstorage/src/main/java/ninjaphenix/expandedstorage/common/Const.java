package ninjaphenix.expandedstorage.common;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;

public class Const
{
    public static final ResourceLocation SCREEN_SELECT = id("screen_select");
    public static final ResourceLocation OPEN_SCREEN_SELECT = id("open_screen_select");
    public static final ResourceLocation SINGLE_CONTAINER = id("single");
    public static final ResourceLocation SCROLLABLE_CONTAINER = id("scrollable");
    public static final ResourceLocation PAGED_CONTAINER = id("paged");
    public static final String MOD_ID = "expandedstorage";

    public static final MutableComponent leftShiftRightClick = new TranslatableComponent("tooltip.expandedstorage.left_shift_right_click",
            new KeybindComponent("key.sneak"), new KeybindComponent("key.use")).withStyle(ChatFormatting.GOLD);
    public static final Tag<Block> WOODEN_BARRELS = TagRegistry.block(new ResourceLocation("c", "wooden_barrels"));
    public static final Tag<Block> WOODEN_CHESTS = TagRegistry.block(new ResourceLocation("c", "wooden_chests"));

    public static ResourceLocation id(final String path) { return new ResourceLocation(MOD_ID, path); }
}