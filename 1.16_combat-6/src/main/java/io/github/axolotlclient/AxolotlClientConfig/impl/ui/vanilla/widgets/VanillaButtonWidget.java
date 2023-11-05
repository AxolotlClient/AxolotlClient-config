package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Selectable;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class VanillaButtonWidget extends ButtonWidget implements Selectable {
	public VanillaButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
		super(x, y, width, height, message, onPress);
	}

	public boolean isHovered(){
		return hovered;
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.getTextureManager().bindTexture(WIDGETS_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		int i = this.getYImage(this.isHovered());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		this.drawTexture(matrices, this.getX(), this.getY(), 0, 46 + i * 20, this.width / 2, this.height);
		this.drawTexture(matrices, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
		int textColor = this.active ? 16777215 : 10526880;
		this.drawScrollableText(matrices, minecraftClient.textRenderer, textColor | MathHelper.ceil(this.alpha * 255.0F) << 24);
	}

	public void drawScrollableText(MatrixStack matrices, TextRenderer renderer, int color) {
		this.drawScrollableText(matrices, renderer, 2, color);
	}

	protected static void drawScrollableText(MatrixStack matrices, TextRenderer textRenderer, Text text, int left, int top, int right, int bottom, int color) {
		int i = textRenderer.getWidth(text);
		int j = (top + bottom - 9) / 2 + 1;
		int k = right - left;
		if (i > k) {
			int l = i - k;
			double d = (double) Util.getMeasuringTimeMs() / 1000.0;
			double e = Math.max((double)l * 0.5, 3.0);
			double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
			double g = MathHelper.lerp(f, 0.0, (double)l);
			DrawUtil.pushScissor(left, top, right-left, bottom-top);
			drawTextWithShadow(matrices, textRenderer, text, left - (int)g, j, color);
			DrawUtil.popScissor();
		} else {
			drawCenteredText(matrices, textRenderer, text, (left + right) / 2, j, color);
		}
	}

	protected void drawScrollableText(MatrixStack matrices, TextRenderer textRenderer, int xOffset, int color) {
		int i = this.getX() + xOffset;
		int j = this.getX() + this.getWidth() - xOffset;
		drawScrollableText(matrices, textRenderer, this.getMessage(), i, this.getY(), j, this.getY() + this.getHeight(), color);
	}

	@Override
	public SelectionType getType() {
		return isHovered() ? SelectionType.HOVERED : (isFocused() ? SelectionType.FOCUSED : SelectionType.NONE);
	}
}
