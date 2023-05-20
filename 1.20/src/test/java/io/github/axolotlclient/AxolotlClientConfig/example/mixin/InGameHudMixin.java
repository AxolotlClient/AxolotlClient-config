package io.github.axolotlclient.AxolotlClientConfig.example.mixin;

import io.github.axolotlclient.AxolotlClientConfig.example.Example;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void onHudRender(GuiGraphics graphics, float tickDelta, CallbackInfo ci) {
		graphics.fill(50, 50, 100, 100, -162555);
		graphics.drawTexture(Example.getInstance().graphicsOption.getTexture(), 50, 50, 0, 0, 50, 50, 50, 50);
	}
}
