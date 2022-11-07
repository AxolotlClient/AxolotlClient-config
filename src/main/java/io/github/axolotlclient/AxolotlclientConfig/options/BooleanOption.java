package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.AxolotlclientConfig.screen.widgets.BooleanWidget;
import io.github.axolotlclient.AxolotlclientConfig.util.clientCommands.CommandResponse;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BooleanOption extends OptionBase<Boolean> {

    private boolean forceOff = false;
    private String disableReason;

    public BooleanOption(String name, Boolean def) {
        super(name, def);
    }

    public BooleanOption(String name, ChangedListener<Boolean> onChange, Boolean def) {
        super(name, onChange, def);
    }

    public BooleanOption(String name, String tooltipKeyPrefix, Boolean def) {
        super(name, tooltipKeyPrefix, def);
    }

    public BooleanOption(String name, String tooltipKeyPrefix, ChangedListener<Boolean> onChange, Boolean def) {
        super(name, tooltipKeyPrefix, onChange, def);
    }


    public Boolean get(){
        if(getForceDisabled()) return false;
        return super.get();
    }

    public void set(Boolean set){
        if(!getForceDisabled()) {
            super.set(set);
        }
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        if(!getForceDisabled()) {
            option = element.getAsBoolean();
        }
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(option);
    }

    public void toggle(){
        if(!getForceDisabled()) {
            set(!get());
        }
    }

    public boolean getForceDisabled(){
        return forceOff;
    }

    public void setForceOff(boolean forceOff, String reason){
        this.forceOff=forceOff;
        disableReason=reason;
    }

    @Override
    public @Nullable String getTooltip(String location) {
        if(getForceDisabled()){
            if(!I18n.translate("disableReason."+disableReason).equals("disableReason."+disableReason)){
                return getTooltipPrefix() + I18n.translate("disableReason."+disableReason);
            }
            return getTooltipPrefix() + disableReason;
        }
        return super.getTooltip(location);
    }

    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        if(args.length>0){

            String arg = args[0];
            if(forceOff){
                return new CommandResponse(false, "You cannot use this option since it's force disabled.");
            }

            switch (arg) {
                case "toggle":
                    toggle();
                    return new CommandResponse(true, "Successfully toggled " + getName() + "!");
                case "true":
                    set(true);
                    return new CommandResponse(true, "Successfully set " + getName() + " to true!");
                case "false":
                    set(false);
                    return new CommandResponse(true, "Successfully set " + getName() + " to false!");
            }

            return new CommandResponse(false, "Please specify either toggle, true or false!");
        }

        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");
    }

    public List<String> getCommandSuggestions(){
        return Arrays.asList("true", "false");
    }

    @Override
    public ButtonWidget getWidget(int x, int y, int width, int height) {
        return new BooleanWidget(0, x, y, 34, height, this);
    }
}
