package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded;

import java.util.function.Consumer;

import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;
import org.lwjgl.opengl.GL11;

public class NVGUtil {

	private static int arrays;

	public static void wrap(Consumer<Long> function) {
		/*RenderSystem.assertOnRenderThread();
		if (arrays == 0) {
			arrays = GL30.glGenVertexArrays();
		}
		GL30.glBindVertexArray(arrays);*/
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		NVGMC.wrap(function);
		GL11.glPopAttrib();
	}
}
