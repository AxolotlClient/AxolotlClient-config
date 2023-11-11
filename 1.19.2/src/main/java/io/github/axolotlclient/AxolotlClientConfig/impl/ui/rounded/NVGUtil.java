package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.VertexBuffer;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;

public class NVGUtil {

	public static void wrap(Consumer<Long> function) {
		VertexBuffer buffer = new VertexBuffer();
		buffer.bind();
		buffer.close();
		NVGMC.wrap(function);
	}
}
