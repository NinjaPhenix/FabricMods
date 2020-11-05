package torcherino.config;

import blue.endless.jankson.Comment;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.chainmail.api.config.JanksonConfigParser;
import org.apache.logging.log4j.MarkerManager;
import torcherino.api.TorcherinoAPI;

public class Config
{
    public static Config INSTANCE;

    @Comment("\nDefines how much faster randoms ticks are applied compared to what they should be.\nValid Range: 1 to 4096")
    public final Integer random_tick_rate = 4;

    @Comment("Log torcherino placement (Intended for server use)") public final Boolean log_placement = FabricLoader.getInstance().getEnvironmentType() ==
            EnvType.SERVER;

    @Comment("\nAdd a block by identifier to the blacklist.\nExamples: \"minecraft:dirt\", \"minecraft:furnace\"")
    private final ResourceLocation[] blacklisted_blocks = new ResourceLocation[]{};

    @Comment("\nAdd a block entity by identifier to the blacklist.\nExamples: \"minecraft:furnace\", \"minecraft:mob_spawner\"")
    private final ResourceLocation[] blacklisted_blockentities = new ResourceLocation[]{};

    @Comment("\nAllows new custom torcherino tiers to be added.\nThis also allows for each tier to have their own max max_speed and ranges.")
    private final Tier[] tiers = new Tier[]{ new Tier("normal", 4, 4, 1), new Tier("compressed", 36, 4, 1), new Tier("double_compressed", 324, 4, 1) };

    @Comment("\nWhen set to ONLINE, Torcherino's only run if the player is currently online\nIf set to RESTART then Torcherino's will run for anyone who has logged in since the server started.\nAnything else then Torcherino's will act like previous versions.")
    public String online_mode = "";


    public static void initialize()
    {
        final JanksonConfigParser parser = new JanksonConfigParser.Builder()
                .deSerializer(JsonPrimitive.class, ResourceLocation.class, (it, marshaller) -> new ResourceLocation(it.asString()),
                        ((identifier, marshaller) -> marshaller.serialize(identifier.toString())))
                .deSerializer(JsonObject.class, Tier.class, (it, marshaller) -> {
                    String name = it.get(String.class, "name");
                    Integer max_speed = it.get(Integer.class, "max_speed");
                    Integer xz_range = it.get(Integer.class, "xz_range");
                    Integer y_range = it.get(Integer.class, "y_range");
                    return new Tier(name, max_speed < 1 ? 1 : max_speed, xz_range < 0 ? 0 : xz_range, y_range < 0 ? 0 : y_range);
                }, (tier, marshaller) -> {
                    final JsonObject rv = new JsonObject();
                    rv.put("name", new JsonPrimitive(tier.name));
                    rv.put("max_speed", new JsonPrimitive(tier.max_speed));
                    rv.put("xz_range", new JsonPrimitive(tier.xz_range));
                    rv.put("y_range", new JsonPrimitive(tier.y_range));
                    return rv;
                }).build();
        INSTANCE = parser.load(Config.class, Config::new, FabricLoader.getInstance().getConfigDir().resolve("sci4me/Torcherino.cfg"),
                new MarkerManager.Log4jMarker("torcherino"));
        INSTANCE.onConfigLoaded();
    }

    private void onConfigLoaded()
    {
        online_mode = online_mode.toUpperCase();
        if (!(online_mode.equals("ONLINE") || online_mode.equals("RESTART"))) { online_mode = ""; }
        for (Tier tier : tiers) { TorcherinoAPI.INSTANCE.registerTier(new ResourceLocation("torcherino", tier.name), tier.max_speed, tier.xz_range, tier.y_range); }
        for (ResourceLocation resloc : blacklisted_blocks) { TorcherinoAPI.INSTANCE.blacklistBlock(resloc); }
        for (ResourceLocation resloc : blacklisted_blockentities) { TorcherinoAPI.INSTANCE.blacklistBlockEntity(resloc); }
    }

    static class Tier
    {
        final String name;
        final int max_speed;
        final int xz_range;
        final int y_range;

        Tier(final String name, final int max_speed, final int xz_range, final int y_range)
        {
            this.name = name;
            this.max_speed = max_speed;
            this.xz_range = xz_range;
            this.y_range = y_range;
        }
    }
}
