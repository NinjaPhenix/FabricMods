package ninjaphenix.containerlib.api.client.screen.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import ninjaphenix.containerlib.api.Constants;

public class ScreenTypeSelectionScreenButton extends ButtonWidget
{
    private final Identifier TEXTURE;

    public ScreenTypeSelectionScreenButton(int x, int y)
    {
        super(x, y, 12, 12, new TranslatableText("screen.ninjaphenix-container-lib.change_screen_button").asString(), button ->
        {
            MinecraftClient.getInstance().player.closeContainer();
            ClientSidePacketRegistry.INSTANCE.sendToServer(Constants.OPEN_SCREEN_SELECT, new PacketByteBuf(Unpooled.buffer()));
        });
        TEXTURE = Constants.id("textures/gui/select_screen_button.png");
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float delta)
    {
        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        blit(x, y, 0, isHovered() ? height : 0, width, height, 16, 32);
    }
}
