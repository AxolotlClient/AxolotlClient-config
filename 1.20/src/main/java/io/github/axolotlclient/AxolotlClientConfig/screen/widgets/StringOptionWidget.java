package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlClientConfig.options.StringOption;
import io.github.axolotlclient.AxolotlClientConfig.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class StringOptionWidget extends TextFieldWidget implements OptionWidget {

	public final StringOption option;

	public StringOptionWidget(int x, int y, StringOption option) {
		super(MinecraftClient.getInstance().textRenderer, x, y, 150, 20, Text.literal(option.get()));
		setMaxLength(512);
		setText(option.get());
		this.option = option;
		setChangedListener(option::set);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		if (MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
			((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isPickerOpen()) {
			this.hovered = false;
			this.setFocused(false);
			return false;
		}
		return super.isMouseOver(mouseX, mouseY);
	}

	@Override
	protected MutableText getNarrationMessage() {
		return Text.literal(option.getTranslatedName()).append(super.getNarrationMessage());
	}

	@Override
	public void updateNarration(NarrationMessageBuilder builder) {
		super.updateNarration(builder);
		builder.put(NarrationPart.TITLE, Text.translatable("narration.value").getString() + option.get());
	}

	@Override
	public boolean isHoveredOrFocused() {
		return canHover() && super.isHoveredOrFocused();
	}

	@Override
	public void unfocus() {
		setFocused(false);
	}
}
