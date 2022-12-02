package io.github.axolotlclient.AxolotlclientConfig.screen.widgets;

import com.mojang.blaze3d.platform.InputUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlclientConfig.screen.OptionsScreenBuilder;
import io.github.axolotlclient.AxolotlclientConfig.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class ColorOptionWidget extends OptionWidget {

    private final ColorOption option;

    public final TextFieldWidget textField;
    private final ButtonWidget openPicker;

    /** Pipette icon in KDE plasma icon theme 'BeautyLine' by Sajjad Abdollahzadeh <sajjad606@gmail.com>
     * <a href="https://store.kde.org/p/1425426">KDE Store Link</a>
     * @license GPL-3
     */
    protected Identifier pipette = new Identifier("axolotlclient", "textures/gui/pipette.png");

    public ColorOptionWidget(int x, int y, ColorOption option) {
        super(x, y, 150, 20, Text.of(""), buttonWidget -> {});
        this.option=option;
        textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x, y, 128, 19, getMessage());
        textField.write(option.get().toString());

        openPicker = new ButtonWidget(x+128, y, 21, 21, Text.of(""), buttonWidget -> {}, DEFAULT_NARRATION){
	        @Override
	        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                DrawUtil.fill(matrices, getX(), getY(), getX()+getWidth(), getY()+getHeight(), option.get().getAsInt());
                DrawUtil.outlineRect(matrices, getX(), getY(), getWidth(), getHeight(), ColorOptionWidget.this.isFocused() ? -1 :-6250336 );

                RenderSystem.setShaderTexture(0, pipette);
                drawTexture(matrices, getX(), getY(), 0, 0, 20, 20, 21, 21);
            }
        };
    }

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        textField.setY(getY());
        textField.setX(getX());
        textField.render(matrices, mouseX, mouseY, delta);

        openPicker.setY(getY() - 1);
        openPicker.setX(getX() + 128);
        openPicker.render(matrices, mouseX, mouseY, delta);

    }

    @Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(openPicker.isMouseOver(mouseX, mouseY)){
            if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder){
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).openColorPicker(option);
            }
			playDownSound(MinecraftClient.getInstance().getSoundManager());
            return true;
        } else if(textField.isMouseOver(mouseX, mouseY)) {
            textField.setTextFieldFocused(true);
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            return textField.mouseClicked(mouseX, mouseY, 0);

        } else {
			textField.setTextFieldFocused(false);
	        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder){
		        ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).closeColorPicker();
	        }
        }
		return super.mouseClicked(mouseX, mouseY, button);
    }

    public void tick(){
        if(textField.isFocused()) {
            textField.tick();
        } else {
            if(MinecraftClient.getInstance().currentScreen instanceof ColorSelectionWidget &&
                    !Objects.equals(textField.getText(), option.get().toString())){
                textField.setText(option.get().toString());
            }
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        if (keyCode == InputUtil.KEY_ENTER_CODE) {
            if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder){
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).openColorPicker(option);
            }
            return true;
        }
        if(textField.isFocused()) {
            textField.keyPressed(keyCode, scanCode, modifiers);
            option.set(Color.parse(textField.getText()));
			return true;
        }
        if (!this.active || !this.visible) {
            return false;
        } else if (keyCode != 32 && keyCode != 335) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            mouseClicked(0, 0, 0);
            return true;
        }

    }

	@Override
	public boolean charTyped(char c, int modifiers) {
		if(textField.isFocused()) {
			textField.charTyped(c, modifiers);
			option.set(Color.parse(textField.getText()));
			return true;
		}
		return false;
	}

	@Override
	protected MutableText getNarrationMessage() {
		return Text.literal(option.getTranslatedName()).append(super.getNarrationMessage());
	}

	@Override
    public void updateNarration(NarrationMessageBuilder builder) {
        super.updateNarration(builder);
        builder.put(NarrationPart.TITLE, Text.translatable("narration.value").append(option.get().toString()));
        builder.put(NarrationPart.HINT, "Press Enter to open color picker.");
    }

    @Override
    protected void onFocusedChanged(boolean newFocused) {
        textField.setTextFieldFocused(newFocused);
    }

    @Override
    public void unfocus() {
        super.unfocus();
        if(textField.isFocused()) {
            textField.changeFocus(false);
        }
    }
}
