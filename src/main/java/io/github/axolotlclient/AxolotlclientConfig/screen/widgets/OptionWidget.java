package io.github.axolotlclient.AxolotlclientConfig.screen.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * Class outlining some common methods for widgets in this config library.
 */

public class OptionWidget extends ButtonWidget implements Selectable {

    public OptionWidget(int x, int y, int width, int height, Text text, PressAction pressAction) {
        super(x, y, width, height, text, pressAction);
    }

    public OptionWidget(int i, int j, int k, int l, Text text, PressAction pressAction, TooltipSupplier tooltipSupplier) {
        super(i, j, k, l, text, pressAction, tooltipSupplier);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if(canHover()) {
            return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        }
        return false;
    }

    @Override
    protected int getYImage(boolean isHovered) {
        if(canHover()) {
            return super.getYImage(isHovered);
        }
        return active ? 1 : 0;
    }

    protected boolean canHover(){
        if(MinecraftClient.getInstance().currentScreen instanceof ColorSelectionWidget){
            this.hovered = false;
            return false;
        }
        return true;
    }

    public void unfocus(){
        setFocused(false);
    }

    public void setFocused(boolean focused){
        super.setFocused(focused);
    }

    public void tick(){

    }
}
