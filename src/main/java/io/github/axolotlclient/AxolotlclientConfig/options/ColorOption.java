package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.screen.widgets.ColorOptionWidget;
import io.github.axolotlclient.AxolotlclientConfig.util.clientCommands.CommandResponse;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

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
        this(name, null, def);
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
            set(Color.parse(element.getAsJsonObject().get("color").getAsString()));
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
    protected CommandResponse onCommandExecution(String[] args) {
        if(args.length>0){

            if(args[0].equals("chroma")){
                String name = args[1];
                if(name.equals("true") || name.equals("on")){
                    chroma = true;
                } else if (name.equals("false") || name.equals("off")){
                    chroma = false;
                }
                return new CommandResponse(true, "Successfully set Chroma for "+getName() + " to "+name);
            }

            Color newColor = Color.parse(args[0]);
            if(newColor== Color.ERROR){
                return new CommandResponse(false, "Please enter a valid Color in Hex format!");
            } else {
                set(newColor);
                return new CommandResponse(true, "Successfully set "+getName() + " to "+args[0]);
            }

        }

        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'. Chroma: "+chroma+".");
    }

    @Override
    public List<String> getCommandSuggestions() {
        return Lists.newArrayList("#FFFFFFFF", "chroma");
    }

    @Override
    public ButtonWidget getWidget(int x, int y, int width, int height) {
        return new ColorOptionWidget(0, x, y, this);
    }
}
