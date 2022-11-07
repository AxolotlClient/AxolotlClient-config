package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.AxolotlclientConfig.screen.widgets.StringOptionWidget;
import io.github.axolotlclient.AxolotlclientConfig.util.clientCommands.CommandResponse;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class StringOption extends OptionBase<String> {

    public StringOption(String name, String tooltipLocation, String def){
        super(name, tooltipLocation, def);
    }

    public StringOption(String name, String def){
        super(name, def);
    }

    public StringOption(String name, ChangedListener<String> onChange, String def){
        super(name, onChange, def);
    }

    public StringOption(String name, String tooltipLocation, ChangedListener<String> onChange, String def){
        super(name, tooltipLocation, onChange, def);
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        this.option=element.getAsString();
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(option == null?def:option);
    }

    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        if(args.length>0){
            StringBuilder v = new StringBuilder();
            for(String s:args){
                v.append(s);
                v.append(" ");
            }
            set(v.toString());
            return new CommandResponse(true, "Successfully set "+getName()+" to "+v+"!");
        }
        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");
    }

    @Override
    public List<String> getCommandSuggestions() {
        return Collections.singletonList(String.valueOf(def));
    }

    @Override
    public ButtonWidget getWidget(int x, int y, int width, int height) {
        return new StringOptionWidget(0, x, y, this);
    }
}
