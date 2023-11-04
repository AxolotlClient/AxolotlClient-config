package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGFont;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.TextFieldWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen.RoundedConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.lwjgl.nanovg.NanoVG;

public class StringWidget extends TextFieldWidget {
	private final StringOption option;
	private NVGFont font;

	private final Color highlightColor = Colors.DARK_YELLOW.withAlpha(100);

	public StringWidget(int x, int y, int width, int height, StringOption option) {
		super(MinecraftClient.getInstance().textRenderer, x, y, width, height, "");

		write(option.get());

		this.option = option;
		setChangedListener(option::set);
	}

	@Override
	public void drawWidget(long ctx, int mouseX, int mouseY, float delta) {
		if (this.isVisible()) {

			if (!option.get().equals(getText())){
				setText(option.get());
			}

			if (font == null){
				font = RoundedConfigScreen.font;
			}
			fillRoundedRect(ctx, getX(), getY()+getHeight(), getWidth(), 1, isFocused() ? Colors.DARK_YELLOW : Colors.TURQUOISE, 1);
			Color i = Colors.WHITE;// new Color(this.editable ? this.editableColor : this.uneditableColor);
			int j = this.selectionStart - this.firstCharacterIndex;
			int k = this.selectionEnd - this.firstCharacterIndex;
			String string = font.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
			boolean bl = j >= 0 && j <= string.length();
			boolean bl2 = this.isFocused() && (Util.getMeasuringTimeMs() - this.focusedTime) / 300L % 2L == 0L && bl;
			int l = this.drawsBackground ? this.getX() + 4 : this.getX();
			int m = this.drawsBackground ? this.getY() + (this.getHeight() - 8) / 2 : this.getY();
			int n = l;
			if (k > string.length()) {
				k = string.length();
			}

			if (string.length() > 0) {
				String string2 = bl ? string.substring(0, j) : string;
				n = (int) drawString(ctx, font, string2, (float) l, (float) m, i);
			}

			boolean bl3 = this.selectionStart < this.text.length() || this.text.length() >= this.getMaxLength();
			int o = n;
			if (!bl) {
				o = j > 0 ? l + this.getWidth() : l;
			} else if (bl3) {
				o = n - 1;
				--n;
			}

			if (string.length() > 0 && bl && j < string.length()) {
				n = (int) drawString(ctx, font, string.substring(j), n, (float) m, i);
			}

			if (bl2) {
				if (bl3) {
					fillRoundedRect(ctx, o, m-1, 1, 2+textRenderer.fontHeight, i, 2);
				} else {
					drawString(ctx, font, UNDERSCORE, o, (float) m, i);
				}
			}

			if (k != j) {
				float p = l + font.getWidth(string.substring(0, k));
				this.drawSelectionHighlight(ctx, o, m - 1, p - 1, m + 1 + this.textRenderer.fontHeight);
			}
		}
	}

	protected void drawSelectionHighlight(long ctx, float x1, float y1, float x2, float y2) {
		if (x1 < x2) {
			float i = x1;
			x1 = x2;
			x2 = i;
		}

		if (y1 < y2) {
			float i = y1;
			y1 = y2;
			y2 = i;
		}

		if (x2 > this.getX() + this.getWidth()) {
			x2 = this.getX() + this.getWidth();
		}

		if (x1 > this.getX() + this.getWidth()) {
			x1 = this.getX() + this.getWidth();
		}

		NanoVG.nvgBeginPath(ctx);
		NanoVG.nvgRect(ctx, x1, y1, x2-x1, y2-y1);
		NanoVG.nvgFillColor(ctx, highlightColor.toNVG());
		NanoVG.nvgFill(ctx);
	}

	@Override
	protected float getStringWidth(String text) {
		if (font == null){
			return 0;
		}
		return font.getWidth(text);
	}

	@Override
	protected String trimToWidth(String text, float width) {
		if (font == null){
			return text;
		}
		return font.trimToWidth(text, width);
	}

	@Override
	protected String trimToWidth(String text, float width, boolean backwards) {
		if (font == null){
			return text;
		}
		return font.trimToWidth(text, width, backwards);
	}
}
