package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlclientConfig.screen.widgets.KeyBindWidget;
import io.github.axolotlclient.AxolotlclientConfig.util.CommandResponse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class KeyBindOption extends OptionBase<KeyBind> {

    private static final HashMap<String, Integer> keys = Util.make(() -> {
       HashMap<String, Integer> map = new HashMap<>();
        for (Field field: Arrays.stream(InputUtil.class.getFields()).filter(field ->
                Modifier.isStatic(field.getModifiers())
                        && Modifier.isPublic(field.getModifiers())
                        && Modifier.isFinal(field.getModifiers())
                        && field.getType().equals(int.class)
                        && field.getName().startsWith("KEY_")
                        && field.getName().endsWith("_CODE")).toList()) {
            try {
                map.put(field.getName().substring(4, field.getName().length() - 5), field.getInt(null));
            } catch (Exception ignored){}
        }
       return map;
    });

    private static final List<KeyBind> bindings = new ArrayList<>();

    public static List<KeyBind> getBindings(){
        return bindings;
    }

    private final KeybindListener listener;

    public KeyBindOption(String name,  KeyBind def, KeybindListener onPress) {
        super(name, def);
        registerBinding();
        listener = onPress;
    }

    public KeyBindOption(String name, String tooltipKeyPrefix, KeyBind def, KeybindListener onPress) {
        super(name, tooltipKeyPrefix, def);
        registerBinding();
        listener = onPress;
    }

    private void registerBinding(){
        bindings.add(get());
        ClientTickEvents.END.register((client)->{
            if(get().wasPressed()){
                listener.onPress(get());
            }
        });
    }

    /*
     * Basic command Functionality. Only Keyboard Keys are supported.
     */

    @Override
    protected CommandResponse onCommandExecution(String args) {
        if(args.length() > 0){
            String response = "";
            if(!args.contains(" ")){
                int code = keys.getOrDefault(args.toUpperCase(Locale.ROOT), -1);
                InputUtil.Key key = InputUtil.fromKeyCode(code, 0);
                if(key != InputUtil.UNKNOWN_KEY || args.equalsIgnoreCase("none")){
                    MinecraftClient.getInstance().options.setKeyCode(get(), key);
                    KeyBind.updateBoundKeys();
                    return new CommandResponse(true, getTranslatedName() + " is now bound to "+ Text.translatable(get().getKeyTranslationKey()).getString());
                }
                response += "§5Are you sure you entered a valid key?\n";
            }
            response += "Syntax: <option> <key name>\nThe Key names are used as defined by Minecraft's InputUtil.";
            return new CommandResponse(false, response);
        }
        return new CommandResponse(true, getTranslatedName() + " is currently bound to "+ I18n.translate(get().getKeyTranslationKey()));
    }


    @Override
    public ButtonWidget getWidget(int x, int y, int width, int height) {
        return new KeyBindWidget(x, y, width, height, this);
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        get().setBoundKey(InputUtil.fromTranslationKey(element.getAsString()));
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(get().getKeyTranslationKey());
    }

    public interface KeybindListener {
        void onPress(KeyBind binding);
    }
}