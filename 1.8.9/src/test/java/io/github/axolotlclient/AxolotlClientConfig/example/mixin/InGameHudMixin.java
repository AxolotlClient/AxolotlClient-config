package io.github.axolotlclient.AxolotlClientConfig.example.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasStatusBars()Z"))
    private void onHudRender(float tickDelta, CallbackInfo ci) {
        /*DrawableHelper.fill(50, 50, 100, 100, -162555);
        GlStateManager.color(1, 1, 1);
        Example.getInstance().graphicsOption.bindTexture();
        DrawableHelper.drawTexture(50, 50, 0, 0, 50, 50, 50, 50);

		DrawableHelper.fill(50, 50, 100, 100, -162555);
		GlStateManager.color(1, 1, 1);
		Example.getInstance().graphicsOption.bindTexture();
		DrawableHelper.drawTexture(50, 50, 0, 0, 50, 50, 50, 50);

		NVGMC.wrap(ctx -> {
			DrawUtil.getInstance().fillCircle(ctx, 200, 50, Color.SELECTOR_BLUE.getAsInt(), 30, 0, 90);
			DrawUtil.getInstance().fillCircle(ctx, 200, 50, Color.SELECTOR_RED.getAsInt(), 30, 90, 180);
			DrawUtil.getInstance().outlineCircle(ctx, 200, 50, Color.SELECTOR_BLUE.getAsInt(), 30, 1, 180, 270);
			DrawUtil.getInstance().outlineCircle(ctx, 200, 50, Color.SELECTOR_RED.getAsInt(), 30, 1, 270, 360);

			DrawUtil.getInstance().fillRoundedRect(ctx, 270, 50, 40, 40, Color.SELECTOR_GREEN.getAsInt(), 5);

			DrawUtil.getInstance().outlineRoundedRect(ctx, 270, 50, 40, 40, Color.GOLD.getAsInt(), 5, 2);

		});

		DrawUtil.drawString(MinecraftClient.getInstance().textRenderer, "aaaaaaaa", 10, 20, -1, true);
		MinecraftClient.getInstance().textRenderer.draw("test", 2, 2, -1);*/
	}
}
