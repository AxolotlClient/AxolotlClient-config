package io.github.axolotlclient.AxolotlclientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlclientConfig.options.GenericOption;
import io.github.axolotlclient.AxolotlclientConfig.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;

public class GenericOptionWidget extends ButtonWidget {

    private final GenericOption option;

    public GenericOptionWidget(int x, int y, int width, int height, GenericOption option) {
        super(0, x, y, width, height, option.getLabel());
        this.option = option;
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {
        hovered = isMouseOver(client, mouseX, mouseY);
        super.render(client, mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
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
        return 1;
    }

    protected boolean canHover(){
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isPickerOpen()){
            this.hovered = false;
            return false;
        }
        return true;
    }

    public void onClick(int mouseX, int mouseY) {
        option.get().onClick(mouseX, mouseY);
    }
}
