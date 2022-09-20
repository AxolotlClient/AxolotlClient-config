package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import io.github.axolotlclient.AxolotlclientConfig.util.clientCommands.CommandResponse;
import org.jetbrains.annotations.NotNull;

public class IntegerOption extends NumericOption<Integer> {


    public IntegerOption(String name, Integer def, Integer min, Integer max) {
        super(name, def, min, max);
    }

    public IntegerOption(String name, ChangedListener<Integer> onChange, Integer def, Integer min, Integer max) {
        super(name, onChange, def, min, max);
    }

    public IntegerOption(String name, String tooltipKeyPrefix, Integer def, Integer min, Integer max) {
        super(name, tooltipKeyPrefix, def, min, max);
    }

    public IntegerOption(String name, String tooltipKeyPrefix, ChangedListener<Integer> onChange, Integer def, Integer min, Integer max) {
        super(name, tooltipKeyPrefix, onChange, def, min, max);
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        option = element.getAsInt();
    }

    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        try {
            if (args.length > 0) {
                set(Integer.parseInt(args[0]));
                return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+"!");
            }
        } catch (NumberFormatException ignored){
            return new CommandResponse(false, "Please specify the number to set "+getName()+" to!");
        }

        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");
    }
}
