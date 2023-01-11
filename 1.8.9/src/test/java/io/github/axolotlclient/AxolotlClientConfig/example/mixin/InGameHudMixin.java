package io.github.axolotlclient.AxolotlClientConfig.example.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.example.Example;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasStatusBars()Z"))
    private void onHudRender(float tickDelta, CallbackInfo ci) {
        DrawableHelper.fill(50, 50, 100, 100, -162555);
        GlStateManager.color3f(1, 1, 1);
        Example.getInstance().graphicsOption.bindTexture();
        DrawableHelper.drawTexture(50, 50, 0, 0, 50, 50, 50, 50);
    }
}
