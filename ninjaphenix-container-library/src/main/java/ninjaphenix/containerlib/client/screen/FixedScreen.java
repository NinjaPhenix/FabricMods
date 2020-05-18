package ninjaphenix.containerlib.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.ContainerProvider;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Slot;
import net.minecraft.util.Identifier;
import ninjaphenix.containerlib.ScreenSizing;
import ninjaphenix.containerlib.inventory.CContainer;

@SuppressWarnings("ConstantConditions")
public class FixedScreen<T extends CContainer> extends ContainerScreen<T> implements ContainerProvider<T>
{
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private final ScreenSizing.ScreenSize SIZE;

    public FixedScreen(T container) { this(container, container.SIZING.FIXED); }

    protected FixedScreen(T container, ScreenSizing.ScreenSize screenSize)
    {
        super(container, container.PLAYER_INVENTORY, container.getDisplayName());
        SIZE = screenSize;
    }

    @Override
    protected void init()
    {
        containerScreenInit();
        // Inventory
        for (int col = 0; col < SIZE.WIDTH; col++)
        {
            for (int row = 0; row < SIZE.HEIGHT; row++)
            {
                final Slot slot = container.getSlot(row * SIZE.WIDTH + col);
                slot.xPosition = col * 18;
                slot.yPosition = row * 18;
            }
        }
        final int invEnd = SIZE.WIDTH * SIZE.HEIGHT;
        // Player Hotbar
        for (int index = 0; index < 9; index++)
        {
            final Slot slot = container.slots.get(invEnd + index);
            slot.xPosition = index * 18;
            slot.yPosition = 18 * (4 + SIZE.HEIGHT);
        }
        // Player Inventory
        for (int col = 0; col < 9; col++)
        {
            for (int row = 0; row < 3; row++)
            {
                final Slot slot = container.slots.get(invEnd + 9 + col + row * 9);
                slot.xPosition = col * 18;
                slot.yPosition = (row + SIZE.HEIGHT) * 18;
            }
        }
    }

    protected void containerScreenInit()
    {
        System.out.println(super.getClass().toString());
        super.init();
    }

    @Override
    protected void drawBackground(final float delta, final int mouseX, final int mouseY)
    {
        renderBackground();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(TEXTURE);
        //minecraft.getTextureManager().bindTexture(SIZE.TEXTURE);
        final int left = (width - containerWidth) / 2;
        final int top = (height - containerHeight) / 2;
        blit(left, top, 0, 0, containerWidth, 3 * 18 + 17);
        blit(left, top + 3 * 18 + 17, 0, 126, containerWidth, 96);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY)
    {
        font.draw(title.asFormattedString(), 8, 6, 4210752);
        font.draw(playerInventory.getDisplayName().asFormattedString(), 8, this.containerHeight - 96 + 2, 4210752);
    }
}
