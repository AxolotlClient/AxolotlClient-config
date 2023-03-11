package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import java.util.List;
import java.util.Objects;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.screen.OptionsScreenBuilder;
import io.github.axolotlclient.AxolotlClientConfig.screen.overlay.ColorSelectionWidget;
import io.github.axolotlclient.AxolotlClientConfig.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ColorOptionWidget extends ButtonWidget implements OptionWidget, ParentElement {

    private final ColorOption option;

    public final TextFieldWidget textField;
    private final ButtonWidget openPicker;

    private Element focused;

    /** Pipette icon in KDE plasma icon theme 'BeautyLine' by Sajjad Abdollahzadeh <sajjad606@gmail.com>
     * <a href="https://store.kde.org/p/1425426">KDE Store Link</a>
     * @license GPL-3
     */
    protected Identifier pipette = new Identifier("axolotlclient", "textures/gui/pipette.png");

    public ColorOptionWidget(int x, int y, ColorOption option) {
        super(x, y, 150, 20, Text.of(""), buttonWidget -> {}, DEFAULT_NARRATION);
        this.option=option;
        textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x, y, 128, 19, getMessage());
        textField.write(option.get().toString());

        openPicker = new ButtonWidget(x+128, y, 21, 21, Text.of(""), buttonWidget -> {
            if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder){
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).setOverlay(
                        new ColorSelectionWidget(option, (OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen));
            }
        }, DEFAULT_NARRATION){
	        @Override
	        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                DrawUtil.fill(matrices, getX(), getY(), getX()+getWidth(), getY()+getHeight(), option.get().getAsInt());
                DrawUtil.outlineRect(matrices, getX(), getY(), getWidth(), getHeight(), ColorOptionWidget.this.isFocused() ? -1 :-6250336 );

                RenderSystem.setShaderTexture(0, pipette);
                drawTexture(matrices, getX(), getY(), 0, 0, 20, 20, 21, 21);
            }
        };

        textField.setChangedListener(s -> option.set(Color.parse(s)));
    }

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        textField.setY(getY());
        textField.setX(getX());
        textField.render(matrices, mouseX, mouseY, delta);

        openPicker.setY(getY() - 1);
        openPicker.setX(getX() + 128);
        openPicker.render(matrices, mouseX, mouseY, delta);

        if(MinecraftClient.getInstance().currentScreen instanceof ColorSelectionWidget &&
                !Objects.equals(textField.getText(), option.get().toString())){
            textField.setText(option.get().toString());
        }
    }

    @Override
    public List<? extends Element> children() {
        return List.of(textField, openPicker);
    }

    @Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return ParentElement.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return ParentElement.super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean dragging) {

    }

    public void tick(){
        textField.tick();
    }

    @Override
    public void unfocus() {
        children().forEach(element -> element.setFocused(false));
    }

    @Nullable
    @Override
    public Element getFocused() {
        return focused;
    }

    @Override
    public void setFocusedChild(@Nullable Element child) {
        if(focused != null) {
            focused.setFocused(false);
        }
        focused = child;
        if(child != null){
            child.setFocused(true);
        }
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
    public boolean isHoveredOrFocused() {
        return canHover() && super.isHoveredOrFocused();
    }
}
