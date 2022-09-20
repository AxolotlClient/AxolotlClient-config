package io.github.axolotlclient.config.mixin;

import io.github.axolotlclient.config.util.ConfigUtils;
import io.github.axolotlclient.config.util.clientCommands.ClientCommands;
import net.minecraft.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void clientCommands(String string, CallbackInfo ci){
        if(string.startsWith("/")) {
            String commandName = string.substring(1);
            ClientCommands.getInstance().getCommands().forEach((str, command) -> {

                if(command.getCommandName().equals(commandName.split(" ")[0])){
                    String[] args = ConfigUtils.copyArrayWithoutFirstEntry(commandName.split(" "));
                    command.execute(args.length > 1? args:new String[]{""});
                    ci.cancel();
                }
            });
        }
    }
}
