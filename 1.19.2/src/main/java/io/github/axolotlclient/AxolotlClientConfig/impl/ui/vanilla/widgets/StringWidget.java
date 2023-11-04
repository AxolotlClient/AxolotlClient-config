package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.MatrixStackProvider;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.TextFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.Util;

public class StringWidget extends TextFieldWidget {

	private final StringOption option;

	public StringWidget(int x, int y, int width, int height, StringOption option) {
		super(MinecraftClient.getInstance().textRenderer, x, y, width, height, "");

		write(option.get());
		this.option = option;
		setChangedListener(option::set);
	}

	@Override
	public void drawWidget(int mouseX, int mouseY, float delta) {
		if (this.isVisible()) {
			if (this.drawsBackground()) {
				int i = this.isFocused() ? BORDER_COLOR_FOCUSED : BORDER_COLOR;
				fill(MatrixStackProvider.getInstance().getStack(),
					this.getX() - 1, this.getY() - 1, this.getX() + this.getWidth() + 1, this.getY() + this.getHeight() + 1, i);
				fill(MatrixStackProvider.getInstance().getStack(),
					this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), BACKGROUND_COLOR);
			}

			if (!option.get().equals(getText())){
				setText(option.get());
			}

			int i = this.editable ? this.editableColor : this.uneditableColor;
			int j = this.selectionStart - this.firstCharacterIndex;
			int k = this.selectionEnd - this.firstCharacterIndex;
			String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
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
				n = this.textRenderer.drawWithShadow(MatrixStackProvider.getInstance().getStack(),
					string2, (float) l, (float) m, i);
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
				n = this.textRenderer.drawWithShadow(MatrixStackProvider.getInstance().getStack(),
					string.substring(j), (float) n, (float) m, i);
			}

			if (bl2) {
				if (bl3) {
					DrawableHelper.fill(MatrixStackProvider.getInstance().getStack(),
						o, m - 1, o + 1, m + 1 + this.textRenderer.fontHeight, INSERT_CURSOR_COLOR);
				} else {
					this.textRenderer.drawWithShadow(MatrixStackProvider.getInstance().getStack(),
						UNDERSCORE, (float) o, (float) m, i);
				}
			}

			if (k != j) {
				int p = l + this.textRenderer.getWidth(string.substring(0, k));
				this.drawSelectionHighlight(o, m - 1, p - 1, m + 1 + this.textRenderer.fontHeight);
			}
		}
	}

	@Override
	protected float getStringWidth(String text) {
		return textRenderer.getWidth(text);
	}

	@Override
	protected String trimToWidth(String text, float width) {
		return textRenderer.trimToWidth(text, (int) width);
	}

	@Override
	protected String trimToWidth(String text, float width, boolean backwards) {
		return textRenderer.trimToWidth(text, (int) width, backwards);
	}
}
