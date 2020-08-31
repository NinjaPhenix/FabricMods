package ninjaphenix.container_library.impl.common;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class Const
{
    public static final String MOD_ID = "ninjaphenix-container-library";
    public static final Identifier SCREEN_SELECT = id("screen_select");
    public static final Identifier OPEN_CONTAINER = Const.id("open_container");
    public static final Identifier OPEN_SCREEN_SELECT = id("open_screen_select");
    public static final Identifier SINGLE_CONTAINER = id("single");
    public static final Identifier SCROLLABLE_CONTAINER = id("scrollable");
    public static final Identifier PAGED_CONTAINER = id("paged");
    public static final Identifier RECEIVE_SERVER_CONTAINERS = id("receive_server_containers");

    public static Identifier id(final String path) { return new Identifier(MOD_ID, path); }

    public static TranslatableText translation(final String format, final Object... args)
    {
        return new TranslatableText(String.format(format, MOD_ID), args);
    }
}