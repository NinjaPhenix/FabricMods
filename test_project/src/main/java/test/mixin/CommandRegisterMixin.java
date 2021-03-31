package test.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.gametest.framework.TestCommand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class CommandRegisterMixin {

    @Shadow
    @Final
    private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(method= "<init>(Lnet/minecraft/commands/Commands$CommandSelection;)V", at=@At("RETURN"))
    public void constructorMixin(Commands.CommandSelection commandSelection, CallbackInfo ci) {
        TestCommand.register(dispatcher);
    }
}
