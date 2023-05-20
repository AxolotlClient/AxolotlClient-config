package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlClientConfig.options.EnumOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class EnumOptionWidget extends ButtonWidget implements OptionWidget {

	EnumOption option;

	public EnumOptionWidget(int x, int y, EnumOption option) {
		super(x, y, 150, 20, Text.translatable(option.get()), buttonWidget -> buttonWidget.setMessage(Text.translatable(option.next())), DEFAULT_NARRATION);
		this.option = option;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		hovered = isMouseOver(mouseX, mouseY) || isFocused();
		if (visible) {
			drawWidget(graphics, mouseX, mouseY, delta);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isHovered()) {
			if (button == 1) {
				setMessage(Text.translatable(option.last()));
			} else {
				setMessage(Text.translatable(option.next()));
			}
			playDownSound(MinecraftClient.getInstance().getSoundManager());
			return true;
		}
		return false;
	}

	@Override
	protected MutableText getNarrationMessage() {
		return Text.literal(option.getTranslatedName()).append(super.getNarrationMessage());
	}

	@Override
	public boolean isHoveredOrFocused() {
		return canHover() && super.isHoveredOrFocused();
	}
}
