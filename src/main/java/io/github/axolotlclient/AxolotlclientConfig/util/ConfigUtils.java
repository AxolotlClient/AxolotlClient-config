package io.github.axolotlclient.AxolotlclientConfig.util;

import com.mojang.blaze3d.glfw.Window;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

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

    public static Text formatFromCodes(String formattedString){
        MutableText text = Text.empty();
        String[] arr = formattedString.split("§");

        List<Formatting> modifiers = new ArrayList<>();
        for (String s:arr){

            Formatting formatting = Formatting.byCode(s.length()>0 ? s.charAt(0) : 0);
            if(formatting != null && formatting.isModifier()){
                modifiers.add(formatting);
            }
            MutableText part = Text.literal(s.length()>0 ? s.substring(1):"");
            if(formatting != null){
                part.formatted(formatting);

                if(!modifiers.isEmpty()){
                    modifiers.forEach(part::formatted);
                    if(formatting.equals(Formatting.RESET)) {
                        modifiers.clear();
                    }
                }
            }
            text.append(part);
        }
        return text;
    }
}
