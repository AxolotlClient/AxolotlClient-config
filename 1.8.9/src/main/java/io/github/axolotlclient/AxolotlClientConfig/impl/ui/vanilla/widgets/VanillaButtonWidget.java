package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.render.TextRenderer;

public class VanillaButtonWidget extends ButtonWidget {

	public VanillaButtonWidget(int x, int y, int width, int height, String message, PressAction action) {
		super(x, y, width, height, message, action);
	}

	@Override
	protected void drawWidget(int mouseX, int mouseY, float delta) {

		TextRenderer textRenderer = client.textRenderer;
		client.getTextureManager().bind(WIDGETS_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int k = active ? (hovered ? 2 : 1) : 0;
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.blendFunc(770, 771);

		drawTexture(this.getX(), this.getY(), 0, 46 + k * 20, this.getWidth() / 2, this.getHeight());
		drawTexture(this.getX() + this.getWidth() / 2, this.getY(), 200 - this.getWidth() / 2, 46 + k * 20, this.getWidth() / 2, this.getHeight());
		int l = 14737632;
		if (!this.active) {
			l = 10526880;
		} else if (this.hovered) {
			l = 16777120;
		}

		DrawUtil.drawCenteredString(textRenderer, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 8) / 2, l, true);
	}
}
