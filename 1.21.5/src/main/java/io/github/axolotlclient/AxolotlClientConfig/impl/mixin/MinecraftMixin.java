package io.github.axolotlclient.AxolotlClientConfig.impl.mixin;

import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;executePendingTasks()V", remap = false))
	private void startNvgFrame(CallbackInfo ci) {
		NVGMC.startFrame();
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;blitToScreen()V", shift = At.Shift.AFTER))
	private void endNvgFrame(CallbackInfo ci) {
		NVGMC.endFrame();
	}
}
