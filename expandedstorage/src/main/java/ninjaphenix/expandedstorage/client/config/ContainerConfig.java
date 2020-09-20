package ninjaphenix.expandedstorage.client.config;

import blue.endless.jankson.Comment;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.common.Const;

@SuppressWarnings("CanBeFinal")
public final class ContainerConfig
{
    @Comment("\nPrefered container type, set to expandedstorage:auto to reuse selection screen.")
    public ResourceLocation preferred_container_type = Const.resloc("auto");

    @Comment("\nOnly allows scrolling in scrollable screen whilst hovering over the scrollbar region.")
    public Boolean restrictive_scrolling = Boolean.FALSE;
}