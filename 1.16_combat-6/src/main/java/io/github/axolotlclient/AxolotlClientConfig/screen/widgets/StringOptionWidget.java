package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlClientConfig.options.StringOption;
import io.github.axolotlclient.AxolotlClientConfig.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

public class StringOptionWidget extends OptionWidget {

	public final StringOption option;

    public TextFieldWidget textField;

    public StringOptionWidget(int x, int y, StringOption option){
        super(x, y, 150, 40, new LiteralText(option.get()), buttonWidget -> {});
        textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x, y, 150, 20, LiteralText.EMPTY){
	        @Override
	        public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if(isMouseOver(mouseX, mouseY)) {
					setFocusUnlocked(true);
                    return super.mouseClicked(mouseX, mouseY, button);
                } else {
                    this.setFocused(false);
					return false;
                }
            }

	        @Override
	        public boolean charTyped(char chr, int modifiers) {
		        boolean bool = super.charTyped(chr, modifiers);
		        option.set(textField.getText());
				return bool;
	        }
        };
        this.option=option;
        textField.setText(option.get());
        textField.setVisible(true);
        textField.setEditable(true);
        textField.setMaxLength(512);
    }

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		this.textField.keyPressed(keyCode, scanCode, modifiers);
		this.option.set(textField.getText());
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return textField.charTyped(chr, modifiers) || super.charTyped(chr, modifiers);
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        textField.y = (y);
        textField.setX(x);
        textField.render(matrices, mouseX, mouseY, delta);
    }

	public void tick() {
		if(textField.isFocused()) {
			textField.tick();
		}
	}

	@Override
	public void setFocused(boolean focused) {
		textField.setFocusUnlocked(focused);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return textField.mouseClicked(mouseX, mouseY, button);
	}

	@Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
            ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isPickerOpen()){
            this.hovered = false;
			this.setFocused(false);
            return false;
        }
        return super.isMouseOver(mouseX, mouseY);
    }

	@Override
	protected MutableText getNarrationMessage() {
		return new LiteralText(option.getTranslatedName()).append(super.getNarrationMessage()).append(new TranslatableText("narration.value").append(option.get()));
	}

	@Override
	protected void onFocusedChanged(boolean newFocused) {
		textField.setSelected(newFocused);
	}

	@Override
	public void unfocus() {
		textField.mouseClicked(0, 0, 0);
	}
}
