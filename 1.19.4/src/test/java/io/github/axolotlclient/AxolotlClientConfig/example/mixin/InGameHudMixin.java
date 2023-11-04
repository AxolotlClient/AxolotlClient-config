package io.github.axolotlclient.AxolotlClientConfig.example.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private void onHudRender(MatrixStack matrices, float tickDelta, CallbackInfo ci){
        /*DrawableHelper.fill(matrices, 50, 50, 100, 100, -162555);
        Example.getInstance().graphicsOption.bindTexture();
        DrawableHelper.drawTexture(matrices, 50, 50, 0, 0, 50, 50, 50, 50);*/
    }
}
