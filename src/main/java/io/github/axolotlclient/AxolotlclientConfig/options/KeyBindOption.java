package io.github.axolotlclient.AxolotlclientConfig.options;

import io.github.axolotlclient.AxolotlclientConfig.screen.widgets.KeyBindWidget;
import io.github.axolotlclient.AxolotlclientConfig.util.clientCommands.CommandResponse;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.KeyBinding;

import java.util.ArrayList;
import java.util.List;

public class KeyBindOption extends NoSaveOption<KeyBinding> {

    private static final List<KeyBinding> bindings = new ArrayList<>();

    public static List<KeyBinding> getBindings(){
        return bindings;
    }

    public KeyBindOption(String name, KeyBinding def) {
        super(name, def);
        bindings.add(def);
    }

    public KeyBindOption(String name, ChangedListener<KeyBinding> onChange, KeyBinding def) {
        super(name, onChange, def);
        bindings.add(def);
    }

    public KeyBindOption(String name, String tooltipKeyPrefix, KeyBinding def) {
        super(name, tooltipKeyPrefix, def);
        bindings.add(def);
    }

    public KeyBindOption(String name, String tooltipKeyPrefix, ChangedListener<KeyBinding> onChange, KeyBinding def) {
        super(name, tooltipKeyPrefix, onChange, def);
        bindings.add(def);
    }

    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        return null;
    }

    @Override
    public List<String> getCommandSuggestions() {
        return new ArrayList<>();
    }

    @Override
    public ButtonWidget getWidget(int x, int y, int width, int height) {
        return new KeyBindWidget(x, y, width, height, this);
    }
}
