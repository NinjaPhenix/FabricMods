package ninjaphenix.containerlib.client.config;

import blue.endless.jankson.Comment;
import ninjaphenix.containerlib.client.screen.ScreenType;

public class Config
{
    @Comment("\nEnables auto focus of the search bar as soon as screen is opened.")
    public final Boolean auto_focus_searchbar = Boolean.FALSE;

    @Comment("\nScreen Type, can be FIXED, SCROLLING, or PAGED")
    public final ScreenType screen_type = ScreenType.SCROLLING;
}
