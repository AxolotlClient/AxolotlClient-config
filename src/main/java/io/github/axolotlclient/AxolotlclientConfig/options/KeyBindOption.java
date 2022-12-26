package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.AxolotlclientConfig.screen.widgets.KeyBindWidget;
import io.github.axolotlclient.AxolotlclientConfig.commands.CommandResponse;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.*;

public class KeyBindOption extends OptionBase<KeyBinding> {

    private static final List<KeyBinding> bindings = new ArrayList<>();

    public static List<KeyBinding> getBindings(){
        return bindings;
    }

    private final KeybindListener listener;

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

    private void registerBinding(){
        bindings.add(get());
        ClientTickEvents.END_CLIENT_TICK.register((client)->{
            if(get().wasPressed()){
                listener.onPress(get());
            }
        });
    }

    /*
     * Basic command Functionality. Only Keyboard Keys are supported.
     */
    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        if(args.length > 0){
            String response = "";
            if(args.length == 1){
                int key = Keyboard.getKeyIndex(args[0].toUpperCase(Locale.ROOT));
                if(key != Keyboard.KEY_NONE || args[0].equalsIgnoreCase("none")){
                    MinecraftClient.getInstance().options.setKeyBindingCode(get(), key);
                    KeyBinding.updateKeysByCode();
                    return new CommandResponse(true, getTranslatedName() + " is now bound to "+ GameOptions.getFormattedNameForKeyCode(get().getCode()));
                }
                response += "§5Are you sure you entered a valid key?\n";
            }
            response += "Syntax: <option> <key name>\nThe Key names are used as defined by LWJGL.";
            return new CommandResponse(false, response);
        }
        return new CommandResponse(true, getTranslatedName() + " is currently bound to "+ GameOptions.getFormattedNameForKeyCode(get().getCode()));
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        get().setCode(Keyboard.getKeyIndex(element.getAsString()));
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(Keyboard.getKeyName(get().getCode()));
    }

    @Override
    public List<String> getCommandSuggestions(String[] args) {
        if(args.length == 0) {
            return Collections.singletonList("none");
        }
        return Collections.emptyList();
    }

    @Override
    public ButtonWidget getWidget(int x, int y, int width, int height) {
        return new KeyBindWidget(x, y, width, height, this);
    }

    public interface KeybindListener {
        void onPress(KeyBinding binding);
    }
}
