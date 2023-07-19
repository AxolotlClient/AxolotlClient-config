package io.github.axolotlclient.AxolotlClientConfig.example.mixin;

import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.example.Example;
import io.github.axolotlclient.AxolotlClientConfig.util.DrawUtil;
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
		DrawUtil.getInstance().drawRect(50, 50, 50, 50, -162555);
		graphics.drawTexture(Example.getInstance().graphicsOption.getTexture(), 50, 50, 0, 0, 50, 50, 50, 50);

		DrawUtil.getInstance().drawCircle(200, 50, Color.SELECTOR_BLUE.getAsInt(), 30, 0, 90);
		DrawUtil.getInstance().drawCircle(200, 50, Color.SELECTOR_RED.getAsInt(), 30, 90, 180);
		DrawUtil.getInstance().outlineCircle(200, 50, Color.SELECTOR_BLUE.getAsInt(), 30, 180, 270);
		DrawUtil.getInstance().outlineCircle(200, 50, Color.SELECTOR_RED.getAsInt(), 30, 270, 360);
	}
}
