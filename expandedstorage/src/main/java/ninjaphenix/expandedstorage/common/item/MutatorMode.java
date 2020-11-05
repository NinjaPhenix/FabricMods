package ninjaphenix.expandedstorage.common.item;

import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import ninjaphenix.expandedstorage.common.Const;

public enum MutatorMode
{
    MERGE(new TranslatableComponent("tooltip.expandedstorage.chest_mutator.merge"),
          src -> new TranslatableComponent("tooltip.expandedstorage.chest_mutator.merge_desc", src), 1),
    UNMERGE(new TranslatableComponent("tooltip.expandedstorage.chest_mutator.unmerge"),
            src -> new TranslatableComponent("tooltip.expandedstorage.chest_mutator.unmerge_desc", src), 2),
    ROTATE(new TranslatableComponent("tooltip.expandedstorage.chest_mutator.rotate"),
           src -> new TranslatableComponent("tooltip.expandedstorage.chest_mutator.rotate_desc", src), 0);

    public final Component title, description;
    public final byte next;

    MutatorMode(final Component title, final Function<Component, MutableComponent> description, final int next)
    {
        this.title = title;
        this.description = description.apply(Const.LSRC).withStyle(ChatFormatting.GRAY);
        this.next = (byte) next;
    }
}