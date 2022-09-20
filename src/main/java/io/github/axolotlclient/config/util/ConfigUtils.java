package io.github.axolotlclient.config.util;

import io.github.axolotlclient.config.util.clientCommands.ClientCommands;
import io.github.axolotlclient.config.util.clientCommands.Command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

public class ConfigUtils {

    public static int toGlCoordsX(int x){
        Window window = new Window(MinecraftClient.getInstance());
        return x * window.getScaleFactor();
    }

    public static int toGlCoordsY(int y){
        Window window = new Window(MinecraftClient.getInstance());
        int scale = window.getScaleFactor();
        return MinecraftClient.getInstance().height - y * scale - scale;
    }

    public static void sendChatMessage(Text msg){
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(msg);
    }

    public static void registerCommand(String command, Command.CommandSuggestionCallback suggestions, Command.CommandExecutionCallback onExecution){
        ClientCommands.getInstance().registerCommand(command, suggestions, onExecution);
    }

    public static void applyScissor(int x, int y, int width, int height){
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        Window window = new Window(MinecraftClient.getInstance());
        int scale = window.getScaleFactor();
        GL11.glScissor(x * scale, (int) ((window.getScaledHeight() - height - y) * scale), width * scale, height * scale);
    }

    public static String[] copyArrayWithoutFirstEntry(String[] strings) {
        String[] strings2 = new String[strings.length - 1];
        System.arraycopy(strings, 1, strings2, 0, strings.length - 1);
        return strings2;
    }
}
