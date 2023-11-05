package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class NVGUtil {

	private static int arrays;

	public static void wrap(Consumer<Long> function){
		RenderSystem.assertOnRenderThread();
		if (arrays == 0) {
			arrays = GL30.glGenVertexArrays();
		}
		GL30.glBindVertexArray(arrays);
		NVGMC.wrap(function);
	}
}
