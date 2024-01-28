package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidgetStateTextures;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BooleanWidget extends ButtonWidget implements OptionWidget {

	private static final ClickableWidgetStateTextures TEXTURES = new ClickableWidgetStateTextures(
		new Identifier("widget/button"), new Identifier("widget/button_disabled"), new Identifier("widget/button_highlighted")
	);
	public final BooleanOption option;

	public BooleanWidget(int x, int y, int width, int height, BooleanOption option) {
		super(x, y, width, height, Text.of(""), buttonWidget -> option.toggle(), DEFAULT_NARRATION);
		this.active = true;
		this.option = option;
	}

	public Text getMessage() {
		return option.get() ? CommonTexts.ON : CommonTexts.OFF;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		this.hovered = isMouseOver(mouseX, mouseY) || isFocused();

		renderBg(graphics, TEXTURES.getTexture(false, isHoveredOrFocused()));
		if (!option.getForceDisabled()) {
			renderSwitch(graphics, TEXTURES.getTexture(this.active, isHoveredOrFocused()));
		} else if (isFocused()) {
			DrawUtil.outlineRect(graphics, getX(), getY(), width - 1, height, -1);
		}

		int color = option.get() ? 0x55FF55 : 0xFF5555;
		graphics.drawCenteredShadowedText(MinecraftClient.getInstance().textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, color);

	}

	private void renderSwitch(GuiGraphics graphics, Identifier texture) {
		int x = option.get() ? this.getX() + width - 8 : this.getX();
		graphics.drawGuiTexture(texture, x, this.getY(), 8, this.height);
		/*graphics.drawTexture(texture, x, this.getY() + height / 2, 0, 20 - height / 2, 4, this.height / 2);
		graphics.drawTexture(texture, x + 4, this.getY(), 200 - 4, 0, 4, this.height);
		graphics.drawTexture(texture, x + 4, this.getY() + height / 2, 200 - 4, 20 - height / 2, 4, this.height / 2);*/
	}

	private void renderBg(GuiGraphics graphics, Identifier texture) {
		graphics.drawGuiTexture(texture, getX(), getY(), this.width, this.height);
		/*graphics.drawTexture(texture, this.getX(), this.getY(), 0, 46, this.width / 2, this.height / 2);
		graphics.drawTexture(texture, this.getX(), this.getY() + height / 2, 0, 20 - height / 2, this.width / 2, this.height / 2);
		graphics.drawTexture(texture, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46, this.width / 2, this.height);
		graphics.drawTexture(texture, this.getX() + this.width / 2, this.getY() + height / 2, 200 - this.width / 2, 20 - height / 2, this.width / 2, this.height / 2);*/
	}

	@Override
	protected MutableText getNarrationMessage() {
		return Text.literal(option.getTranslatedName()).append(". ").append(super.getNarrationMessage());
	}

	@Override
	public void updateNarration(NarrationMessageBuilder builder) {
		if (option.getForceDisabled()) {
			builder.put(NarrationPart.USAGE, option.getStrippedTooltip());
		}
		super.updateNarration(builder);
	}

	@Override
	public boolean isHoveredOrFocused() {
		return canHover() && super.isHoveredOrFocused();
	}
}
