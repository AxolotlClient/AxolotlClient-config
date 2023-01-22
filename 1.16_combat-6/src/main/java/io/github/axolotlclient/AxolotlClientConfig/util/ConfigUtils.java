package io.github.axolotlclient.AxolotlClientConfig.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL11;

/**
 * Holds commonly used methods of this config lib.
 * It should not be needed to use these methods out of other mods.
 */

@ApiStatus.Internal
public class ConfigUtils {

    public static int toGlCoordsX(int x){
        Window window = MinecraftClient.getInstance().getWindow();
        return (int) (x * window.getScaleFactor());
    }

    public static int toGlCoordsY(int y){
        Window window = MinecraftClient.getInstance().getWindow();
        int scale = (int) window.getScaleFactor();
        return window.getHeight() - y * scale - scale;
    }

    public static void sendChatMessage(Text msg){
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(msg);
    }

    public static void applyScissor(int x, int y, int width, int height){
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        Window window = MinecraftClient.getInstance().getWindow();
        int scale = (int) window.getScaleFactor();
        GL11.glScissor(x * scale, (window.getScaledHeight() - height - y) * scale, width * scale, height * scale);
    }

    public static String[] copyArrayWithoutFirstEntry(String[] strings) {
        String[] strings2 = new String[strings.length - 1];
        System.arraycopy(strings, 1, strings2, 0, strings.length - 1);
        return strings2;
    }
}
