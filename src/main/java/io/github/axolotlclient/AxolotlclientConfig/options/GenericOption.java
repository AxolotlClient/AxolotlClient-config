package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.AxolotlclientConfig.screen.widgets.GenericOptionWidget;
import io.github.axolotlclient.AxolotlclientConfig.util.clientCommands.CommandResponse;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class GenericOption extends NoSaveOption<GenericOption.OnClick> {
    private final String label;

    public GenericOption(String name, String label, OnClick def) {
        super(name, def);
        this.label = label;
    }

    public GenericOption(String name, String label, ChangedListener<OnClick> onChange, OnClick def) {
        super(name, onChange, def);
        this.label = label;
    }

    public GenericOption(String name, String label, String tooltipKeyPrefix, OnClick def) {
        super(name, tooltipKeyPrefix, def);
        this.label = label;
    }

    public GenericOption(String name, String label, String tooltipKeyPrefix, ChangedListener<OnClick> onChange, OnClick def) {
        super(name, tooltipKeyPrefix, onChange, def);
        this.label = label;
    }

    public String getLabel(){
        return label;
    }

    @Override
    public OnClick get() {
        return super.getDefault();
    }

    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        get().onClick(Mouse.getX(), Mouse.getY());
        return new CommandResponse(false, "");
    }

    @Override
    public List<String> getCommandSuggestions() {
        return new ArrayList<>();
    }



    public interface OnClick {
         void onClick(int mouseX, int mouseY);
    }

    @Override
    public ButtonWidget getWidget(int x, int y, int width, int height) {
        return new GenericOptionWidget(x, y, width, height, this);
    }
}
