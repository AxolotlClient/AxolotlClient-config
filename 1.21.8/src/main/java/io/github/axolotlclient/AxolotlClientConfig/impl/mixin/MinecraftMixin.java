/*
 * Copyright © 2021-2025 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.AxolotlClientConfig.impl.mixin;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.BlendFunction;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;blitToScreen()V", shift = At.Shift.AFTER))
	private void endNvgFrame(CallbackInfo ci) {
		NVGMC.endFrame();
		GlStateManager._enableBlend();
		var blendFunction = BlendFunction.ADDITIVE;
		GlStateManager._blendFuncSeparate(
			GlConst.toGl(blendFunction.sourceColor()),
			GlConst.toGl(blendFunction.destColor()),
			GlConst.toGl(blendFunction.sourceAlpha()),
			GlConst.toGl(blendFunction.destAlpha())
		);
	}
}
