package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigConfig;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.screen.OptionsScreenBuilder;
import io.github.axolotlclient.AxolotlClientConfig.util.ConfigUtils;
import io.github.axolotlclient.AxolotlClientConfig.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class CategoryWidget extends ButtonWidget implements OptionWidget {

    public OptionCategory category;

    public BooleanWidget enabledButton;

    public CategoryWidget(OptionCategory category, int x, int y, int width, int height) {
        super(x, y, width, 20, Text.of(category.getTranslatedName()).copy().append("..."),
                buttonWidget -> {}, DEFAULT_NARRATION);
        this.category=category;

        if (AxolotlClientConfigConfig.showQuickToggles.get()) {
            category.getOptions().stream().filter(option -> option.getName().contains("enabled") && option instanceof BooleanOption).forEach(option -> {
                enabledButton = new BooleanWidget((x + width) - 35, y + 3, 30, height - 6, (BooleanOption) option);
                setMessage( getMessage());
            });
        }

    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {

        if(enabledButton!=null && enabledButton.isMouseOver(mouseX, mouseY)) {
            this.hovered = false;
            return true;
        }
        return canHover() && super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        if(enabledButton != null){
            enabledButton.setY(getY() + 2);
            enabledButton.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    protected void drawScrollableText(MatrixStack matrices, TextRenderer textRenderer, int i, int j) {
        int k = this.getX() + i;
        int l = this.getX() + this.getWidth() - i;
        if(enabledButton != null){
            l-=enabledButton.getWidth()+4;
        }
        drawScrollableText(matrices, textRenderer, this.getMessage(), k, this.getY(), l, this.getY() + this.getHeight(), j);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if(enabledButton!=null &&
                enabledButton.isHoveredOrFocused()) {
            playDownSound(MinecraftClient.getInstance().getSoundManager());
            enabledButton.option.toggle();
            return true;
        } else if (isHoveredOrFocused()) {
            if (MinecraftClient.getInstance().currentScreen != null) {
                if(!((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).getCategory().equals(category)) {
                    MinecraftClient.getInstance().setScreen(new OptionsScreenBuilder(MinecraftClient.getInstance().currentScreen, category,
                            ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).modid));
                }
            }
        }
        return this.hovered && super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.active || !this.visible) {
            return false;
        } else if (keyCode != 257 && keyCode != 32 && keyCode != 335) {
            return false;
        } else {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            mouseClicked(0, 0, 0);
            return true;
        }
    }

    @Override
    protected MutableText getNarrationMessage() {
        return Text.literal(category.getTranslatedName()).append(super.getNarrationMessage());
    }

    @Override
    public boolean isHoveredOrFocused() {
        return canHover() && super.isHoveredOrFocused();
    }
}
