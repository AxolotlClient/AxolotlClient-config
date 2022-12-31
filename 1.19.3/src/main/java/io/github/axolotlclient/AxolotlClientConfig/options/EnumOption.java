package io.github.axolotlclient.AxolotlClientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.EnumOptionWidget;
import io.github.axolotlclient.AxolotlClientConfig.common.commands.CommandResponse;
import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnumOption extends OptionBase<String> {

    private int i;

    protected String[] values;

    public EnumOption(String name, Object[] e, String def) {
        super(name, def);
        values = convertToStringArray(e);
    }

    public EnumOption(String name, String tooltipLocation, Object[] e, String def) {
        super(name, tooltipLocation, def);
        values = convertToStringArray(e);
    }

    public EnumOption(String name, ChangedListener<String> onChange, Object[] e, String def) {
        super(name, onChange, def);
        values = convertToStringArray(e);
    }

    public EnumOption(String name, String tooltipLocation, ChangedListener<String> onChange, Object[] e, String def) {
        super(name, tooltipLocation, onChange, def);
        values = convertToStringArray(e);
    }

    public EnumOption(String name, String[] e, String def) {
        super(name, def);
        values = e;
    }

    public EnumOption(String name, String tooltipLocation, String[] e, String def) {
        super(name, tooltipLocation, def);
        values = e;
    }

    public EnumOption(String name, ChangedListener<String> onChange, String[] e, String def) {
        super(name, onChange, def);
        values = e;
    }

    public EnumOption(String name, String tooltipLocation, ChangedListener<String> onChange, String[] e, String def) {
        super(name, tooltipLocation, onChange, def);
        values = e;
    }

    private String[] convertToStringArray(Object[] arr){
        List<String> l = new ArrayList<>();
        for(Object v:arr){
            l.add(v.toString());
        }
        return l.toArray(new String[0]);
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        for(int i=0; i<values.length;i++){
            String v = values[i];
            if(Objects.equals(v, element.getAsString())){
                this.i = i;
                break;
            }
        }
    }

    @Override
    public void setDefaults() {
        if(def==null){
            i=0;
            return;
        }

        for(int i=0;i< values.length; i++){
            String v = values[i];
            if(Objects.equals(v, def)){
                this.i=i;
                break;
            }
        }
    }

    public String get(){
        return values[i];
    }

    @Override
    public void set(String value) {
        for(int i=0;i< values.length; i++){
            String v = values[i];
            if(Objects.equals(v, value)){
                this.i=i;
                break;
            }
        }
    }

    public String next() {
        i++;
        if(i > values.length-1)i=0;
        changeCallback.onChanged(get());
        return get();
    }

    public String last(){
        i--;
        if(i<0)i=values.length-1;
        changeCallback.onChanged(get());
        return get();
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(values[i]);
    }

    @Override
    protected CommandResponse onCommandExecution(String arg) {
        if(arg.length()>0){
            if(arg.equals("next")){
                next();
                return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+"!");
            } else if(arg.equals("last")){
                last();
                return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+"!");
            }

            for (int i=0;i<values.length;i++){
                if(arg.equalsIgnoreCase(values[i])){
                    this.i=i;
                    changeCallback.onChanged(get());
                    return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+" (Index: "+i+")!");
                }
            }

            try {
                int value = Integer.parseInt(arg);
                if(value>values.length-1 || value < 0){
                    throw new IndexOutOfBoundsException();
                }
                i=value;
                changeCallback.onChanged(get());
                return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+" (Index: "+i+")!");
            } catch (IndexOutOfBoundsException e){
                return new CommandResponse(false, "Please specify an index within the bounds of 0<=i<"+values.length+"!");
            } catch (NumberFormatException ignored){
                return new CommandResponse(false, "Please specify either next, last or an index for a specific value!");
            }
        }
        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");
    }

    @Override
    public ClickableWidget getWidget(int x, int y, int width, int height) {
        return new EnumOptionWidget(x, y, this);
    }
}
