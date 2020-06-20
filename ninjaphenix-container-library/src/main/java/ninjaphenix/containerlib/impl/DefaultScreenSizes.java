package ninjaphenix.containerlib.impl;

import net.minecraft.util.Identifier;
import ninjaphenix.containerlib.api.*;
import ninjaphenix.containerlib.api.screen.PagedScreenMeta;
import ninjaphenix.containerlib.api.screen.ScrollableScreenMeta;
import ninjaphenix.containerlib.api.screen.SingleScreenMeta;
import ninjaphenix.containerlib.impl.inventory.PagedScreenHandler;
import ninjaphenix.containerlib.impl.inventory.ScrollableScreenHandler;
import ninjaphenix.containerlib.impl.inventory.SingleScreenHandler;

import static ninjaphenix.containerlib.api.Constants.*;

public final class DefaultScreenSizes implements ContainerLibraryExtension
{
    private final ContainerLibraryAPI API = ContainerLibraryAPI.INSTANCE;

    @Override
    public void declareScreenSizeCallbacks()
    {
        API.declareScreenSizeRegisterCallback(SINGLE_CONTAINER, SingleScreenHandler::onScreenSizeRegistered);
        API.declareScreenSizeRegisterCallback(PAGED_CONTAINER, PagedScreenHandler::onScreenSizeRegistered);
        API.declareScreenSizeRegisterCallback(SCROLLABLE_CONTAINER, ScrollableScreenHandler::onScreenSizeRegistered);
    }

    @Override
    public void declareScreenSizes()
    {
        /* todo: make these user configurable in the future
            Requires asset generation as well as creation of custom resource packs.
            \
            Plan: once a screen is first opened, generate the texture and add it to a generated pack
            Then use that cached texture to render the gui. Will need a way to store texture width and height
         */
        API.declareScreenSize(SINGLE_CONTAINER, new SingleScreenMeta(9, 3, 27, getTexture("shared", 9, 3), 208, 192)); // Wood
        API.declareScreenSize(SINGLE_CONTAINER, new SingleScreenMeta(9, 6, 54, getTexture("shared", 9, 6), 208, 240)); // Iron / Large Wood
        API.declareScreenSize(SINGLE_CONTAINER, new SingleScreenMeta(9, 9, 81, getTexture("shared", 9, 9), 208, 304)); // Gold
        API.declareScreenSize(SINGLE_CONTAINER, new SingleScreenMeta(12, 9, 108, getTexture("shared", 12, 9), 256, 304)); // Diamond / Large Iron
        API.declareScreenSize(SINGLE_CONTAINER, new SingleScreenMeta(15, 9, 135, getTexture("shared", 15, 9), 320, 304)); // Netherite
        API.declareScreenSize(SINGLE_CONTAINER, new SingleScreenMeta(18, 9, 162, getTexture("shared", 18, 9), 368, 304)); // Large Gold
        API.declareScreenSize(SINGLE_CONTAINER, new SingleScreenMeta(18, 12, 216, getTexture("shared", 18, 12), 368, 352)); // Large Diamond
        API.declareScreenSize(SINGLE_CONTAINER, new SingleScreenMeta(18, 15, 270, getTexture("shared", 18, 15), 368, 416)); // Large Netherite

        API.declareScreenSize(PAGED_CONTAINER, new PagedScreenMeta(9, 3, 1, 27, getTexture("shared", 9, 3), 208, 192)); // Wood
        API.declareScreenSize(PAGED_CONTAINER, new PagedScreenMeta(9, 6, 1, 54, getTexture("shared", 9, 6), 208, 240)); // Iron / Large Wood
        API.declareScreenSize(PAGED_CONTAINER, new PagedScreenMeta(9, 9, 1, 81, getTexture("shared", 9, 9), 208, 304)); // Gold
        API.declareScreenSize(PAGED_CONTAINER, new PagedScreenMeta(9, 6, 2, 108, getTexture("shared", 9, 6), 208, 240)); // Diamond / Large Iron
        API.declareScreenSize(PAGED_CONTAINER, new PagedScreenMeta(9, 5, 3, 135, getTexture("shared", 9, 5), 208, 224)); // Netherite
        API.declareScreenSize(PAGED_CONTAINER, new PagedScreenMeta(9, 6, 3, 162, getTexture("shared", 9, 6), 208, 240)); // Large Gold
        API.declareScreenSize(PAGED_CONTAINER, new PagedScreenMeta(9, 8, 3, 216, getTexture("shared", 9, 8), 208, 288)); // Large Diamond
        API.declareScreenSize(PAGED_CONTAINER, new PagedScreenMeta(10, 9, 3, 270, getTexture("shared", 10, 9), 224, 304)); // Large Netherite

        API.declareScreenSize(SCROLLABLE_CONTAINER, new ScrollableScreenMeta(9, 3, 27, getTexture("shared", 9, 3), 208, 192)); // Wood
        API.declareScreenSize(SCROLLABLE_CONTAINER, new ScrollableScreenMeta(9, 6, 54, getTexture("shared", 9, 6), 208, 240)); // Iron / Large Wood
        API.declareScreenSize(SCROLLABLE_CONTAINER, new ScrollableScreenMeta(9, 9, 81, getTexture("shared", 9, 9), 208, 304)); // Gold
        API.declareScreenSize(SCROLLABLE_CONTAINER, new ScrollableScreenMeta(9, 9, 108, getTexture("shared", 9, 9), 208, 304)); // Diamond / Large Iron
        API.declareScreenSize(SCROLLABLE_CONTAINER, new ScrollableScreenMeta(9, 9, 135, getTexture("shared", 9, 9), 208, 304)); // Netherite
        API.declareScreenSize(SCROLLABLE_CONTAINER, new ScrollableScreenMeta(9, 9, 162, getTexture("shared", 9, 9), 208, 304)); // Large Gold
        API.declareScreenSize(SCROLLABLE_CONTAINER, new ScrollableScreenMeta(9, 9, 216, getTexture("shared", 9, 9), 208, 304)); // Large Diamond
        API.declareScreenSize(SCROLLABLE_CONTAINER, new ScrollableScreenMeta(9, 9, 270, getTexture("shared", 9, 9), 208, 304)); // Large Netherite
    }

    private Identifier getTexture(String type, int width, int height)
    {
        return new Identifier("ninjaphenix-container-lib", String.format("textures/gui/container/%s_%d_%d.png", type, width, height));
    }
}
