package io.github.axolotlclient.AxolotlclientConfig.mixin;

import io.github.axolotlclient.AxolotlclientConfig.util.ConfigUtils;
import io.github.axolotlclient.AxolotlclientConfig.util.clientCommands.ClientCommands;
import net.minecraft.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void clientCommands(String string, CallbackInfo ci){
        if(string.startsWith("/")) {
            String commandName = string.substring(1);
            ClientCommands.getInstance().getCommands().values().forEach((command) -> {
                if(command.getCommandName().equals(commandName.split(" ")[0])){
                    String[] args = commandName.replace(command.getCommandName()+" ", "").split(" ");
                    System.out.println("String: "+ commandName +" Args: "+ Arrays.toString(args));
                    command.execute(args);
                    ci.cancel();
                }
            });
        }
    }
}
