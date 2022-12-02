package io.github.axolotlclient.AxolotlclientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlclientConfig.options.GenericOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class GenericOptionWidget extends OptionWidget {

    private final GenericOption option;

    public GenericOptionWidget(int x, int y, int width, int height, GenericOption option) {
        super(x, y, width, height, Text.of(option.getLabel()), (widget)->{});
        this.option = option;
    }


    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        hovered = isMouseOver(mouseX, mouseY) || isFocused();
        if(visible){
            renderButton(stack, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.active || !this.visible) {
            return false;
        } else if (keyCode != 257 && keyCode != 32 && keyCode != 335) {
            return false;
        } else {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            onClick(getX() +1, getY() +1);
            return true;
        }
    }

    public void onClick(double mouseX, double mouseY) {
        option.get().onClick((int) mouseX, (int) mouseY);
    }

	@Override
	protected MutableText getNarrationMessage() {
		return Text.literal(option.getTranslatedName()).append(super.getNarrationMessage());
	}
}
