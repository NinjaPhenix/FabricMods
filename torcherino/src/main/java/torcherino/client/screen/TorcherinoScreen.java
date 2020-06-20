package torcherino.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.client.screen.widgets.FixedSliderWidget;
import torcherino.client.screen.widgets.StateButtonWidget;

@Environment(EnvType.CLIENT)
@SuppressWarnings("SpellCheckingInspection")
public class TorcherinoScreen extends Screen
{
    private static final Identifier SCREEN_TEXTURE = new Identifier(Torcherino.MOD_ID, "textures/screens/torcherino.png");
    private static final int screenWidth = 245;
    private static final int screenHeight = 123;

    private final BlockPos blockPos;
    private final Tier tier;
    private final String cached_title;
    private int xRange, zRange, yRange, speed, redstoneMode, left, top;

    public TorcherinoScreen(Text title, int xRange, int zRange, int yRange, int speed, int redstoneMode, BlockPos pos, Identifier tierID)
    {
        super(title);
        this.tier = TorcherinoAPI.INSTANCE.getTier(tierID);
        this.blockPos = pos;
        this.xRange = xRange;
        this.zRange = zRange;
        this.yRange = yRange;
        this.speed = speed == 0 ? 1 : speed;
        this.redstoneMode = redstoneMode;
        this.cached_title = title.asString();
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    protected void init()
    {
        left = (width - screenWidth) / 2;
        top = (height - screenHeight) / 2;
        addButton(new FixedSliderWidget(left + 8, top + 20, 205, (double) (speed - 1) / (tier.getMaxSpeed() - 1), tier.getMaxSpeed())
        {
            @Override
            protected void updateMessage() { setMessage(new TranslatableText("gui.torcherino.speed", 100 * TorcherinoScreen.this.speed).asString()); }

            @Override
            protected void applyValue()
            {
                TorcherinoScreen.this.speed = 1 + (int) Math.round(value * (TorcherinoScreen.this.tier.getMaxSpeed() - 1));
                value = (double) (speed - 1) / (tier.getMaxSpeed() - 1);
            }
        });
        addButton(new FixedSliderWidget(left + 8, top + 45, 205, (double) xRange / tier.getXZRange(), tier.getXZRange())
        {
            @Override
            protected void updateMessage() { setMessage(new TranslatableText("gui.torcherino.x_range", TorcherinoScreen.this.xRange * 2 + 1).asString()); }

            @Override
            protected void applyValue()
            {
                TorcherinoScreen.this.xRange = (int) Math.round(value * TorcherinoScreen.this.tier.getXZRange());
                value = (double) xRange / tier.getXZRange();
            }
        });
        this.addButton(new FixedSliderWidget(left + 8, top + 70, 205, (double) zRange / tier.getXZRange(), tier.getXZRange())
        {
            @Override
            protected void updateMessage() { setMessage(new TranslatableText("gui.torcherino.z_range", TorcherinoScreen.this.zRange * 2 + 1).asString()); }

            @Override
            protected void applyValue()
            {
                TorcherinoScreen.this.zRange = (int) Math.round(value * TorcherinoScreen.this.tier.getXZRange());
                value = (double) zRange / tier.getXZRange();
            }
        });
        this.addButton(new FixedSliderWidget(left + 8, top + 95, 205, (double) yRange / tier.getYRange(), tier.getYRange())
        {
            @Override
            protected void updateMessage() { setMessage(new TranslatableText("gui.torcherino.y_range", TorcherinoScreen.this.yRange * 2 + 1).asString()); }

            @Override
            protected void applyValue()
            {
                TorcherinoScreen.this.yRange = (int) Math.round(value * TorcherinoScreen.this.tier.getYRange());
                value = (double) yRange / tier.getYRange();
            }
        });
        this.addButton(new StateButtonWidget(this, left + 217, top + 20)
        {
            ItemStack buttonIcon;

            @Override
            protected void initialize()
            {
                setButtonMessage();
                setButtonIcon();
            }

            private void setButtonMessage()
            {
                String translationKey;
                switch (TorcherinoScreen.this.redstoneMode)
                {
                    case 0:
                        translationKey = "gui.torcherino.mode.normal";
                        break;
                    case 1:
                        translationKey = "gui.torcherino.mode.inverted";
                        break;
                    case 2:
                        translationKey = "gui.torcherino.mode.ignore";
                        break;
                    case 3:
                        translationKey = "gui.torcherino.mode.off";
                        break;
                    default:
                        translationKey = "gui.torcherino.mode.error";
                        break;
                }
                setNarrationMessage(new TranslatableText("gui.torcherino.mode", new TranslatableText(translationKey)).asString());
            }

            private void setButtonIcon()
            {
                switch (TorcherinoScreen.this.redstoneMode)
                {
                    case 0:
                        this.buttonIcon = new ItemStack(Items.REDSTONE);
                        break;
                    case 1:
                        this.buttonIcon = new ItemStack(Items.REDSTONE_TORCH);
                        break;
                    case 2:
                        this.buttonIcon = new ItemStack(Items.GUNPOWDER);
                        break;
                    case 3:
                        this.buttonIcon = new ItemStack(Items.REDSTONE_LAMP);
                        break;
                    default:
                        this.buttonIcon = new ItemStack(Items.FURNACE);
                        break;
                }
            }

            @Override
            protected void nextState()
            {
                TorcherinoScreen.this.redstoneMode = (TorcherinoScreen.this.redstoneMode + 1) % 4;
                initialize();
            }

            @Override
            protected ItemStack getButtonIcon() { return buttonIcon; }
        });
    }

    @Override
    public void render(int x, int y, float partialTicks)
    {
        fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        minecraft.getTextureManager().bindTexture(SCREEN_TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        blit(left, top, 0, 0, screenWidth, screenHeight);
        font.draw(cached_title, (width - font.getStringWidth(cached_title)) / 2.0f, top + 6, 4210752);
        super.render(x, y, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256 || minecraft.options.keyInventory.matchesKey(keyCode, 0))
        {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose()
    {
        PacketByteBuf packetBuffer = new PacketByteBuf(Unpooled.buffer());
        packetBuffer.writeBlockPos(blockPos);
        packetBuffer.writeInt(xRange);
        packetBuffer.writeInt(zRange);
        packetBuffer.writeInt(yRange);
        packetBuffer.writeInt(speed);
        packetBuffer.writeInt(redstoneMode);
        ClientSidePacketRegistry.INSTANCE.sendToServer(new Identifier(Torcherino.MOD_ID, "utv"), packetBuffer);
        super.onClose();
    }
}
