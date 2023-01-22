package io.github.axolotlclient.AxolotlClientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.ColorOptionWidget;
import io.github.axolotlclient.AxolotlClientConfig.common.commands.CommandResponse;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.jetbrains.annotations.NotNull;

public class ColorOption extends OptionBase<Color> {

    private boolean chroma;

    public ColorOption(String name, String def){
        this(name, Color.parse(def));
    }

    public ColorOption(String name, int def){
        this(name, new Color(def));
    }

    public ColorOption(String name, String tooltipLocation, Color def){
        super(name, tooltipLocation, def);
    }

    public ColorOption(String name, Color def){
        super(name, def);
    }

    public ColorOption(String name, ChangedListener<Color> onChange, Color def){
        super(name, onChange, def);
    }

    public ColorOption(String name, String tooltipKeyPrefix, ChangedListener<Color> onChange, Color def){
        super(name, tooltipKeyPrefix, onChange, def);
    }

    public Color get(){
        return chroma ? Color.getChroma().withAlpha(super.get().getAlpha()) : super.get();
    }

    public void set(Color set, boolean chroma){
        super.set(set);
        this.chroma=chroma;
    }

    public void setChroma(boolean set){
        chroma=set;
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        try {
            chroma = element.getAsJsonObject().get("chroma").getAsBoolean();
            option = Color.parse(element.getAsJsonObject().get("color").getAsString());
        } catch (Exception ignored){
        }
    }

    @Override
    public JsonElement getJson() {
        JsonObject object = new JsonObject();
        object.add("color",new JsonPrimitive(option.toString()));
        object.add("chroma", new JsonPrimitive(chroma));
        return object;
    }

    public boolean getChroma(){
        return chroma;
    }

    @Override
    protected CommandResponse onCommandExecution(String arg) {
        if(arg.length()>0){

            if(arg.split(" ")[0].equals("chroma")){
                String name = arg.split(" ")[1];
                if(name.equals("true") || name.equals("on")){
                    chroma = true;
                } else if (name.equals("false") || name.equals("off")){
                    chroma = false;
                }
                return new CommandResponse(true, "Successfully set Chroma for "+getName() + " to "+name);
            }

            Color newColor = Color.parse(arg);
            if(newColor== Color.ERROR){
                return new CommandResponse(false, "Please enter a valid Color in Hex format!");
            } else {
                set(newColor);
                return new CommandResponse(true, "Successfully set "+getName() + " to "+arg);
            }

        }

        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'. Chroma: "+chroma+".");
    }

    @Override
    public ClickableWidget getWidget(int x, int y, int width, int height) {
        return new ColorOptionWidget(x, y, this);
    }
}
