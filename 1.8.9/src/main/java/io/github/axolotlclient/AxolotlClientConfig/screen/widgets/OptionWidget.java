package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlClientConfig.options.ConfigPart;
import io.github.axolotlclient.AxolotlClientConfig.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;

/**
 * ButtonWidgets in this Minecraft version do not usually handle interactions themselves.
 * This class is here to change that.
 * Therefore, all Widgets used with this library should extend this class.
 * <p>
 *     Other Widgets will still work, but will have no way to interact with.
 */

public class OptionWidget extends ButtonWidget implements ConfigPart {
    public OptionWidget(int id, int x, int y, String message) {
        super(id, x, y, message);
    }

    public OptionWidget(int id, int x, int y, int width, int height, String message) {
        super(id, x, y, width, height, message);
    }

    protected boolean focused;

    @Override
    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
        if(canHover()) {
            return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        }
        return false;
    }

    protected boolean canHover(){
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isPickerOpen()){
            this.hovered = false;
            return false;
        }
        return true;
    }

    public void mouseClicked(int mouseX, int mouseY, int button){

    }

    public boolean keyPressed(char character, int code){
        return false;
    }

    public void setFocused(boolean focus){
        focused = focus;
    }

    public boolean getFocused(){
        return focused;
    }

    public void unfocus(){
        setFocused(false);
    }
}
