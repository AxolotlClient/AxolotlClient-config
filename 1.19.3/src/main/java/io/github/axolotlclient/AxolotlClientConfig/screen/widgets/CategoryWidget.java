package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigConfig;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class CategoryWidget extends OptionWidget {

    public OptionCategory category;

    public BooleanWidget enabledButton;

    public CategoryWidget(OptionCategory category, int x, int y, int width, int height) {
        super(x, y, width, 20, Text.of(category.getTranslatedName()).copy().append("..."),
	        buttonWidget -> {});
        this.category=category;

        if (AxolotlClientConfigConfig.showQuickToggles.get()) {
            category.getOptions().forEach(option -> {
                if (option.getName().contains("enabled") && option instanceof BooleanOption) {
                    enabledButton = new BooleanWidget((x + width) - 35, y + 3, 30, height - 6, (BooleanOption) option);
                }
            });
        }

    }

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {

        if(enabledButton!=null && enabledButton.isMouseOver(mouseX, mouseY)) {
            this.hovered = false;
        }
        return super.isMouseOver(mouseX, mouseY);
    }

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
	        RenderSystem.setShaderTexture(0, ClickableWidget.WIDGETS_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height && !(enabledButton!=null && enabledButton.isHoveredOrFocused()) || isFocused();
            int i = this.getYImage(this.hovered);
            this.drawTexture(matrices, this.getX(), this.getY(), 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexture(matrices, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.renderBackground(matrices, MinecraftClient.getInstance(), mouseX, mouseY);
            int j = 14737632;
            if (!this.active) {
                j = 10526880;
            } else if (this.hovered) {
                j = 16777120;
            }

            drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, this.getMessage(), this.getX() + this.width / 2 - (enabledButton!=null?enabledButton.getWidth()/2:0), this.getY() + (this.height - 8) / 2, j);
        }

        if(enabledButton != null){
            enabledButton.setY(getY() + 2);
            enabledButton.render(matrices, mouseX, mouseY, delta);
        }
    }

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if(enabledButton!=null &&
                enabledButton.isHoveredOrFocused()) {
	        playDownSound(MinecraftClient.getInstance().getSoundManager());
            enabledButton.option.toggle();
			return true;
        } else {
            if (MinecraftClient.getInstance().currentScreen != null) {
                MinecraftClient.getInstance().setScreen(new OptionsScreenBuilder(MinecraftClient.getInstance().currentScreen, category,
                        ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).modid));
            }
        }
		return super.mouseClicked(mouseX, mouseY, button);
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
}
