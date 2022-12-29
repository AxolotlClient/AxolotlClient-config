package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import io.github.axolotlclient.AxolotlclientConfig.commands.CommandResponse;
import org.jetbrains.annotations.NotNull;

public class DoubleOption extends NumericOption<Double> {

    public DoubleOption(String name, Double def, Double min, Double max) {
        super(name, def, min, max);
    }

    public DoubleOption(String name, ChangedListener<Double> onChange, Double def, Double min, Double max) {
        super(name, onChange, def, min, max);
    }

    public DoubleOption(String name, String tooltipKeyPrefix, Double def, Double min, Double max) {
        super(name, tooltipKeyPrefix, def, min, max);
    }

    public DoubleOption(String name, String tooltipKeyPrefix, ChangedListener<Double> onChange, Double def, Double min, Double max) {
        super(name, tooltipKeyPrefix, onChange, def, min, max);
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        option = element.getAsDouble();
    }

    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        try {
            if (args.length > 0) {
                set(Double.parseDouble(args[0]));
                return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+"!");
            }
        } catch (NumberFormatException ignored){
            return new CommandResponse(false, "Please specify the number to set "+getName()+" to!");
        }

        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");

    }
}
