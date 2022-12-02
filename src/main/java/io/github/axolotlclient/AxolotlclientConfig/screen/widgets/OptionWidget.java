package io.github.axolotlclient.AxolotlclientConfig.screen.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * Class outlining some common methods for widgets in this config library.
 */

public class OptionWidget extends ButtonWidget implements Selectable {

	protected OptionWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
		super(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION);
	}

	@Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if(canHover()) {
            return mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
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

	@Override
	protected MutableText getNarrationMessage() {
		return super.getNarrationMessage();
	}
}
