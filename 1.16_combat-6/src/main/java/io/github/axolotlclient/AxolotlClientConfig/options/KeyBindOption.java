package io.github.axolotlclient.AxolotlClientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.AxolotlClientConfig.common.commands.CommandResponse;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.KeyBindWidget;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class KeyBindOption extends OptionBase<KeyBinding> {

    private static final HashMap<String, Integer> keys = Util.make(() -> {
       HashMap<String, Integer> map = new HashMap<>();
        for (Field field: Arrays.stream(GLFW.class.getFields()).filter(field ->
                Modifier.isStatic(field.getModifiers())
                        && Modifier.isPublic(field.getModifiers())
                        && Modifier.isFinal(field.getModifiers())
                        && field.getType().equals(int.class)
                        && (field.getName().startsWith("KEY_") || field.getName().startsWith("MOUSE_"))
                        && field.getName().endsWith("_CODE")).collect(Collectors.toList())) {
            try {
                map.put(field.getName().substring(4, field.getName().length() - 5), field.getInt(null));
            } catch (Exception ignored){}
        }
       return map;
    });

    private static final List<KeyBinding> bindings = new ArrayList<>();

    public static List<KeyBinding> getBindings(){
        return bindings;
    }

    private final KeybindListener listener;

	public KeyBindOption(String name, int defaultKeyCode, KeybindListener onPress) {
		this(name, new KeyBinding(name, defaultKeyCode, "category."+name), onPress);
	}

	public KeyBindOption(String name, String tooltipKeyPrefix, int defaultKeyCode, KeybindListener onPress) {
		this(name, tooltipKeyPrefix, new KeyBinding(name, defaultKeyCode, "category."+name), onPress);
	}

	public KeyBindOption(String name, ChangedListener<KeyBinding> onChange, int defaultKeyCode, KeybindListener onPress) {
		this(name, onChange, new KeyBinding(name, defaultKeyCode, "category."+name), onPress);
	}

	public KeyBindOption(String name, String tooltipKeyPrefix, ChangedListener<KeyBinding> onChange, int defaultKeyCode, KeybindListener onPress) {
		this(name, tooltipKeyPrefix, onChange, new KeyBinding(name, defaultKeyCode, "category."+name), onPress);
	}

    public KeyBindOption(String name,  KeyBinding def, KeybindListener onPress) {
        super(name, def);
        registerBinding();
        listener = onPress;
    }

    public KeyBindOption(String name, String tooltipKeyPrefix, KeyBinding def, KeybindListener onPress) {
        super(name, tooltipKeyPrefix, def);
        registerBinding();
        listener = onPress;
    }

    public KeyBindOption(String name, ChangedListener<KeyBinding> onChange, KeyBinding def, KeybindListener onPress) {
        super(name, onChange, def);
        registerBinding();
        listener = onPress;
    }

    public KeyBindOption(String name, String tooltipKeyPrefix, ChangedListener<KeyBinding> onChange, KeyBinding def, KeybindListener onPress) {
        super(name, tooltipKeyPrefix, onChange, def);
        registerBinding();
        listener = onPress;
    }

	private void registerBinding(){
        bindings.add(get());
        ClientTickEvents.END_CLIENT_TICK.register((client)->{
            if(get() != null && get().wasPressed()){
                listener.onPress(get());
            }
        });
    }

    /*
     * Basic command Functionality. Only Keyboard Keys are supported to be set this way.
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
                    KeyBinding.updateKeysByCode();
                    return new CommandResponse(true, getTranslatedName() + " is now bound to "+ new TranslatableText(get().getBoundKeyTranslationKey()).getString());
                }
                response += "§5Are you sure you entered a valid key?\n";
            }
            response += "Syntax: <option> <key name>\nThe Key names are used as defined by Minecraft's InputUtil.";
            return new CommandResponse(false, response);
        }
        return new CommandResponse(true, getTranslatedName() + " is currently bound to "+ I18n.translate(get().getBoundKeyTranslationKey()));
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
		return new JsonPrimitive(get().getBoundKeyTranslationKey());
	}

	public interface KeybindListener {
        void onPress(KeyBinding binding);
    }
}
