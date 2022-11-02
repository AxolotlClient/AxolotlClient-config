package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.AxolotlclientConfig.util.clientCommands.CommandResponse;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class GenericOption extends OptionBase<GenericOption.OnClick> {
    private final String label;

    public GenericOption(String name, String label, OnClick onClick) {
        super(name, onClick);
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
    public void setValueFromJsonElement(JsonElement element) {

    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive("");
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
}
