package io.github.axolotlclient.AxolotlclientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlclientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlclientConfig.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class EnumOptionWidget extends OptionWidget {

    EnumOption option;

    public EnumOptionWidget(int x, int y, EnumOption option) {
        super(x, y, 150, 20, Text.translatable(option.get()), buttonWidget -> buttonWidget.setMessage(Text.translatable(option.next())));
        this.option=option;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        hovered = isMouseOver(mouseX, mouseY) || isFocused();
        if(visible){
            renderButton(stack, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button==1) {
            setMessage(Text.translatable(option.last()));
        } else {
            setMessage(Text.translatable(option.next()));
        }
		playDownSound(MinecraftClient.getInstance().getSoundManager());
        return true;
    }

	@Override
	protected MutableText getNarrationMessage() {
		return Text.literal(option.getTranslatedName()).append(super.getNarrationMessage());
	}
}
