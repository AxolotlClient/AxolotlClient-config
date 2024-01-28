package io.github.axolotlclient.AxolotlClientConfig.util;

import com.mojang.blaze3d.glfw.Window;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL11;

/**
 * Holds commonly used methods of this config lib.
 * It should not be needed to use these methods out of other mods.
 */

@ApiStatus.Internal
public class ConfigUtils {

	public static int toGlCoordsX(int x) {
		Window window = MinecraftClient.getInstance().getWindow();
		return (int) (x * window.getScaleFactor());
	}

	public static int toGlCoordsY(int y) {
		Window window = MinecraftClient.getInstance().getWindow();
		int scale = (int) window.getScaleFactor();
		return window.getHeight() - y * scale - scale;
	}

	public static void sendChatMessage(Text msg) {
		MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(msg);
	}
}
