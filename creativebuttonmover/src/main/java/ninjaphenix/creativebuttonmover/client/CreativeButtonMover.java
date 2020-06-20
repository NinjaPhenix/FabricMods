package ninjaphenix.creativebuttonmover.client;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class CreativeButtonMover
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static Integer x;
    public static Integer y;

    public static void loadValues()
    {
        //noinspection ConstantConditions
        if (false)
        {
            for (int i = 0; i < 20; i++)
            {
                FabricItemGroupBuilder.build(new Identifier("cbm", "item_group_" + i), () -> new ItemStack(Items.DIAMOND));
            }
        }

        try (FileInputStream inputStream = new FileInputStream(new File(FabricLoader.getInstance().getConfigDirectory(), "creativebuttonmover.properties")))
        {
            Properties props = new Properties();
            props.load(inputStream);
            x = Integer.valueOf((String) props.computeIfAbsent("x", (v) -> "116"));
            y = Integer.valueOf((String) props.computeIfAbsent("y", (v) -> "-10"));
        }
        catch (IOException e)
        {
            LOGGER.info("[creativebuttonmover] Failed to read values from config.");
            x = 116;
            y = -10;
        }
        saveValues();
    }

    public static void saveValues()
    {
        Properties props = new Properties();
        props.put("x", x.toString());
        props.put("y", y.toString());
        try (FileOutputStream outputStream = new FileOutputStream(new File(FabricLoader.getInstance().getConfigDirectory(), "creativebuttonmover.properties")))
        {
            props.store(outputStream, "Allows you to move the creative page buttons added by fabric-api. See mod menu entry for a way to move these in-game.");
        }
        catch (IOException e)
        {
            LOGGER.info("[creativebuttonmover] Failed to save values to config.");
        }
    }
}
