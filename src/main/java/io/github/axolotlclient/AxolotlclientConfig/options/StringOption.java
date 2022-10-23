package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.AxolotlclientConfig.util.CommandResponse;
import org.jetbrains.annotations.NotNull;

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
    protected CommandResponse onCommandExecution(String arg) {
        if(arg.length()>0){

            set(arg);
            return new CommandResponse(true, "Successfully set "+getName()+" to "+arg+"!");
        }
        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");
    }
}
