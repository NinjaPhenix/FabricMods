package ninjaphenix.creativebuttonmover.client;

import net.minecraft.util.Identifier;

public class Config
{
    public static Config INSTANCE;

    public Button PrevButton = new Button(167, 4, 12, 12, new Identifier("creativebuttonmover", "textures/gui/creativebuttons/prev/default.png"));
    public Button NextButton = new Button(179, 4, 12, 12, new Identifier("creativebuttonmover", "textures/gui/creativebuttons/next/default.png"));

    public static class Button
    {
        public Integer x;
        public Integer y;
        public Integer width;
        public Integer height;
        public Identifier texture;

        Button(Integer x, Integer y, Integer width, Integer height, Identifier texture)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.texture = texture;
        }
    }
}