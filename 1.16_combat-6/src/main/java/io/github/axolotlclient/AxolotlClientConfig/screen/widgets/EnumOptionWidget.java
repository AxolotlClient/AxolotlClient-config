package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlClientConfig.options.EnumOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

public class EnumOptionWidget extends OptionWidget {

    EnumOption option;

    public EnumOptionWidget(int x, int y, EnumOption option) {
        super(x, y, 150, 20, new TranslatableText(option.get()), buttonWidget -> buttonWidget.setMessage(new TranslatableText(option.next())));
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
            setMessage(new TranslatableText(option.last()));
        } else {
            setMessage(new TranslatableText(option.next()));
        }
		playDownSound(MinecraftClient.getInstance().getSoundManager());
        return true;
    }

	@Override
	protected MutableText getNarrationMessage() {
		return new LiteralText(option.getTranslatedName()).append(super.getNarrationMessage());
	}
}
