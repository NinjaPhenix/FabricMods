package ninjaphenix.tests.containerlib;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import ninjaphenix.containerlib.ContainerLibrary;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Main implements ModInitializer
{
    public static final Main INSTANCE = new Main();

    private Main() {}

    @Override
    public void onInitialize()
    {
        CommandRegistry.INSTANCE.register(false, this::registerOpenCommand);
    }

    private void registerOpenCommand(CommandDispatcher<ServerCommandSource> commandSource)
    {
        commandSource.register(literal("open").then(argument("pos", BlockPosArgumentType.blockPos()).executes(command ->
        {
            BlockPos pos = BlockPosArgumentType.getBlockPos(command, "pos");
            ContainerLibrary.openContainer(command.getSource().getPlayer(), pos, new LiteralText("Debug Command"));
            return Command.SINGLE_SUCCESS;
        })));
    }
}