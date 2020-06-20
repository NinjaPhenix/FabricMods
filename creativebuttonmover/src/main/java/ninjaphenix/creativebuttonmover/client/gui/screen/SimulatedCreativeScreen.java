package ninjaphenix.creativebuttonmover.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import ninjaphenix.creativebuttonmover.client.CreativeButtonMover;

@SuppressWarnings("ConstantConditions")
public class SimulatedCreativeScreen extends Screen
{
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/container/creative_inventory/tab_item_search.png");
    private static final Identifier TAB_TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    private static final Identifier BUTTON_TEXTURE = new Identifier("fabric", "textures/gui/creative_buttons.png");
    private static final TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
    private static final int containerHeight = 136;
    private static final int containerWidth = 195;
    private Screen returnTo;
    private int left;
    private int top;
    private int xpos;
    private int ypos;
    private boolean dragging;

    public SimulatedCreativeScreen(Screen parent)
    {
        super(new LiteralText(""));
        xpos = CreativeButtonMover.x;
        ypos = CreativeButtonMover.y;
        returnTo = parent;
    }

    @Override
    protected void init()
    {
        super.init();
        this.addButton(new ButtonWidget(width / 2 - 50, height / 2 + 96, 100, 20, "Close", (widget) -> onClose()));
    }

    @Override
    public boolean mouseClicked(double x, double y, int int_1)
    {
        dragging = inPageButtonArea(x, y);
        if (dragging) { return true; }
        return super.mouseClicked(x, y, int_1);
    }

    @Override
    public boolean mouseReleased(double x, double y, int int_1)
    {
        dragging = false;
        return super.mouseReleased(x, y, int_1);
    }

    @Override
    public boolean mouseDragged(double x, double y, int int_1, double double_3, double double_4)
    {
        if (dragging)
        {
            xpos = (int) x - left - 11;
            ypos = (int) y - top - 6;
            return true;
        }
        return super.mouseDragged(x, y, int_1, double_3, double_4);
    }

    private boolean inPageButtonArea(double x, double y)
    {
        final int l = left + xpos;
        final int t = top + ypos;
        return x >= l && x <= l + 19 && y <= t + 11 && y >= t;
    }

    @Override
    public void render(int int_1, int int_2, float float_1)
    {
        this.renderBackground();
        super.render(int_1, int_2, float_1);
        left = (this.width - containerWidth) / 2;
        top = (this.height - containerHeight) / 2;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableAlphaTest();
        for (int i = 1; i < 12; i++)
        {
            textureManager.bindTexture(TAB_TEXTURE);
            renderItemGroup(ItemGroup.GROUPS[i]);
        }
        textureManager.bindTexture(BACKGROUND_TEXTURE);
        blit(left, top, 0, 0, containerWidth, containerHeight);
        textureManager.bindTexture(TAB_TEXTURE);
        renderItemGroup(ItemGroup.GROUPS[0]);
        RenderSystem.disableAlphaTest();
        textureManager.bindTexture(BUTTON_TEXTURE);
        setBlitOffset(200);
        blit(left + xpos, top + ypos, 0, 0, 22, 10);
        setBlitOffset(0);
        font.drawWithShadow("Page Button Mover", width / 2f - font.getStringWidth("Page Button Mover") / 2f, top - 40, 5636095);
    }

    private void renderItemGroup(ItemGroup itemGroup_1)
    {
        final int column = itemGroup_1.getColumn();
        int offY = 0;
        int x = left + 28 * column;
        int y = top;
        if (itemGroup_1.getIndex() == 0) { offY += 32; }
        if (itemGroup_1.isSpecial()) { x = left + containerWidth - 28 * (6 - column); }
        else if (column > 0) { x += column; }
        if (itemGroup_1.isTopRow()) { y -= 28; }
        else
        {
            offY += 64;
            y += containerHeight - 4;
        }
        this.blit(x, y, column * 28, offY, 28, 32);
        if (minecraft.world != null)
        {
            x += 6;
            y += 8 + (itemGroup_1.isTopRow() ? 1 : -1);
            RenderSystem.enableRescaleNormal();
            ItemStack itemStack_1 = itemGroup_1.getIcon();
            this.itemRenderer.renderGuiItem(itemStack_1, x, y);
            this.itemRenderer.renderGuiItemOverlay(this.font, itemStack_1, x, y);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 262)
        {
            xpos++;
            return true;
        }
        else if (keyCode == 263)
        {
            xpos--;
            return true;
        }
        else if (keyCode == 264)
        {
            ypos++;
            return true;
        }
        else if (keyCode == 265)
        {
            ypos--;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose()
    {
        CreativeButtonMover.x = xpos;
        CreativeButtonMover.y = ypos;
        CreativeButtonMover.saveValues();
        this.minecraft.openScreen(returnTo);
    }
}
