package io.github.axolotlclient.AxolotlclientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlclientConfig.options.GenericOption;
import io.github.axolotlclient.AxolotlclientConfig.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class GenericOptionWidget extends ButtonWidget {

    private final GenericOption option;

    public GenericOptionWidget(int x, int y, int width, int height, GenericOption option) {
        super(x, y, width, height, Text.of(option.getLabel()), (widget)->{});
        this.option = option;
    }


    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        hovered = isMouseOver(mouseX, mouseY);
        super.render(stack, mouseX, mouseY, delta);
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

    public void onClick(double mouseX, double mouseY) {
        option.get().onClick((int) mouseX, (int) mouseY);
    }
}
