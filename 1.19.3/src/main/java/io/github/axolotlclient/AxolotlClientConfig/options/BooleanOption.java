package io.github.axolotlclient.AxolotlClientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.BooleanWidget;
import io.github.axolotlclient.AxolotlClientConfig.common.commands.CommandResponse;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            return getTooltipPrefix()+translate(disableReason);

        }
        return super.getTooltip(location);
    }

    @Override
    protected CommandResponse onCommandExecution(String arg) {
        if(arg.length()>0){
            if(getForceDisabled()){
                return new CommandResponse(false, "Could not set "+getName()+" to "+arg+".\nReason: "+disableReason);
            }
            switch (arg) {
                case "toggle" -> {
                    toggle();
                    return new CommandResponse(true, "Successfully toggled " + getName() + "!");
                }
                case "true" -> {
                    set(true);
                    return new CommandResponse(true, "Successfully set " + getName() + " to true!");
                }
                case "false" -> {
                    set(false);
                    return new CommandResponse(true, "Successfully set " + getName() + " to false!");
                }
            }

            return new CommandResponse(false, "Please specify either toggle, true or false!");
        }

        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");
    }

    @Override
    public ClickableWidget getWidget(int x, int y, int width, int height) {
        return new BooleanWidget(x, y, 34, height, this);
    }
}
