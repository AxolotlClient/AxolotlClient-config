/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGFont;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Selectable;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.nanovg.NanoVG;

public class TextFieldWidget extends AbstractButtonWidget implements DrawingUtil, Selectable {
	public static final int BACKWARDS = -1;
	public static final int FORWARDS = 1;
	public static final int DEFAULT_EDITABLE_COLOR = 14737632;
	protected static final int INSERT_CURSOR_COLOR = -3092272;
	protected static final String UNDERSCORE = "_";
	protected static final int BORDER_COLOR_FOCUSED = -1;
	protected static final int BORDER_COLOR = -6250336;
	protected static final int BACKGROUND_COLOR = -16777216;
	private static final int INSERT_CURSOR_WIDTH = 1;
	private final Color highlightColor = Colors.accent2().withAlpha(100);
	protected String text = "";
	protected boolean drawsBackground = true;
	protected boolean editable = true;
	protected int firstCharacterIndex;
	protected int selectionStart;
	protected int selectionEnd;
	protected int editableColor = DEFAULT_EDITABLE_COLOR;
	protected int uneditableColor = 7368816;
	@Nullable
	protected String suggestion;
	@Nullable
	protected String hint;
	protected long focusedTime = Util.getMeasuringTimeMs();
	private int maxLength = 32;
	private boolean focusUnlocked = true;
	private boolean selecting;
	@Nullable
	private Consumer<String> changedListener;
	private Predicate<String> textPredicate = Objects::nonNull;

	public TextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
		this(textRenderer, x, y, width, height, null, text);
	}

	public TextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable net.minecraft.client.gui.widget.TextFieldWidget copyFrom, Text text) {
		super(x, y, width, height, text);
		if (copyFrom != null) {
			this.setText(copyFrom.getText());
		}
	}

	public void setChangedListener(Consumer<String> changedListener) {
		this.changedListener = changedListener;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		if (this.textPredicate.test(text)) {
			if (text.length() > this.maxLength) {
				this.text = text.substring(0, this.maxLength);
			} else {
				this.text = text;
			}

			this.setCursorToEnd(false);
			this.setSelectionEnd(this.selectionStart);
			this.onChanged(text);
		}
	}

	public String getSelectedText() {
		int i = Math.min(this.selectionStart, this.selectionEnd);
		int j = Math.max(this.selectionStart, this.selectionEnd);
		return this.text.substring(i, j);
	}

	public void setTextPredicate(Predicate<String> textPredicate) {
		this.textPredicate = textPredicate;
	}

	public void write(String text) {
		int i = Math.min(this.selectionStart, this.selectionEnd);
		int j = Math.max(this.selectionStart, this.selectionEnd);
		int k = this.maxLength - this.text.length() - (i - j);
		String string = SharedConstants.stripInvalidChars(text);
		int l = string.length();
		if (k < l) {
			string = string.substring(0, k);
			l = k;
		}

		String string2 = new StringBuilder(this.text).replace(i, j, string).toString();
		if (this.textPredicate.test(string2)) {
			this.text = string2;
			this.setSelectionStart(i + l);
			this.setSelectionEnd(this.selectionStart);
			this.onChanged(this.text);
		}
	}

	private void onChanged(String newText) {
		if (this.changedListener != null) {
			this.changedListener.accept(newText);
		}
	}

	private void erase(int offset) {
		if (Screen.hasControlDown()) {
			this.eraseWords(offset);
		} else {
			this.eraseCharacters(offset);
		}
	}

	public void eraseWords(int wordOffset) {
		if (!this.text.isEmpty()) {
			if (this.selectionEnd != this.selectionStart) {
				this.write("");
			} else {
				this.eraseCharacters(this.getWordSkipPosition(wordOffset) - this.selectionStart);
			}
		}
	}

	public void eraseCharacters(int characterOffset) {
		if (!this.text.isEmpty()) {
			if (this.selectionEnd != this.selectionStart) {
				this.write("");
			} else {
				int i = this.getCursorPosWithOffset(characterOffset);
				int j = Math.min(i, this.selectionStart);
				int k = Math.max(i, this.selectionStart);
				if (j != k) {
					String string = new StringBuilder(this.text).delete(j, k).toString();
					if (this.textPredicate.test(string)) {
						this.text = string;
						this.setCursor(j, false);
					}
				}
			}
		}
	}

	public int getWordSkipPosition(int wordOffset) {
		return this.getWordSkipPosition(wordOffset, this.getCursor());
	}

	private int getWordSkipPosition(int wordOffset, int cursorPosition) {
		return this.getWordSkipPosition(wordOffset, cursorPosition, true);
	}

	private int getWordSkipPosition(int wordOffset, int cursorPosition, boolean skipOverSpaces) {
		int i = cursorPosition;
		boolean bl = wordOffset < 0;
		int j = Math.abs(wordOffset);

		for (int k = 0; k < j; ++k) {
			if (!bl) {
				int l = this.text.length();
				i = this.text.indexOf(32, i);
				if (i == -1) {
					i = l;
				} else {
					while (skipOverSpaces && i < l && this.text.charAt(i) == ' ') {
						++i;
					}
				}
			} else {
				while (skipOverSpaces && i > 0 && this.text.charAt(i - 1) == ' ') {
					--i;
				}

				while (i > 0 && this.text.charAt(i - 1) != ' ') {
					--i;
				}
			}
		}

		return i;
	}

	public void moveCursor(int offset, boolean selecting) {
		this.setCursor(this.getCursorPosWithOffset(offset), selecting);
	}

	private int getCursorPosWithOffset(int offset) {
		return moveCursor(this.text, this.selectionStart, offset);
	}

	private int moveCursor(String string, int cursor, int delta) {
		int i = string.length();
		if (delta >= 0) {
			for (int j = 0; cursor < i && j < delta; ++j) {
				if (Character.isHighSurrogate(string.charAt(cursor++)) && cursor < i && Character.isLowSurrogate(string.charAt(cursor))) {
					++cursor;
				}
			}
		} else {
			for (int j = delta; cursor > 0 && j < 0; ++j) {
				--cursor;
				if (Character.isLowSurrogate(string.charAt(cursor)) && cursor > 0 && Character.isHighSurrogate(string.charAt(cursor - 1))) {
					--cursor;
				}
			}
		}

		return cursor;
	}

	public void setCursor(int cursor, boolean selecting) {
		this.setSelectionStart(cursor);
		if (!selecting) {
			this.setSelectionEnd(this.selectionStart);
		}

		this.onChanged(this.text);
	}

	public void setSelectionStart(int cursor) {
		this.selectionStart = MathHelper.clamp(cursor, 0, this.text.length());
	}

	public void setCursorToStart(boolean selecting) {
		this.setCursor(0, selecting);
	}

	public void setCursorToEnd(boolean selecting) {
		this.setCursor(this.text.length(), selecting);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!this.isActive()) {
			return false;
		} else if (Screen.isSelectAll(keyCode)) {
			this.setCursorToEnd(false);
			this.setSelectionEnd(0);
			return true;
		} else if (Screen.isCopy(keyCode)) {
			MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
			return true;
		} else if (Screen.isPaste(keyCode)) {
			if (this.editable) {
				this.write(MinecraftClient.getInstance().keyboard.getClipboard());
			}

			return true;
		} else if (Screen.isCut(keyCode)) {
			MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
			if (this.editable) {
				this.write("");
			}

			return true;
		} else {
			switch (keyCode) {
				case 263: {
					if (Screen.hasControlDown()) {
						this.setCursor(this.getWordSkipPosition(-1), selecting);
					} else {
						this.moveCursor(-1, selecting);
					}
					return true;
				}
				case 262: {
					if (Screen.hasControlDown()) {
						this.setCursor(this.getWordSkipPosition(1), selecting);
					} else {
						this.moveCursor(1, selecting);
					}
					return true;
				}
				case 259: {
					if (this.editable) {
						this.selecting = false;
						this.erase(-1);
						this.selecting = Screen.hasShiftDown();
					}
					return true;
				}
				case 261: {
					if (this.editable) {
						this.selecting = false;
						this.erase(1);
						this.selecting = Screen.hasShiftDown();
					}
					return true;
				}
				case 268: {
					this.setCursorToStart(selecting);
					return true;
				}
				case 269: {
					this.setCursorToEnd(selecting);
					return true;
				}
			}
		}
		return false;
	}

	public boolean isActive() {
		return this.isVisible() && this.isFocused() && this.isEditable();
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (!this.isActive()) {
			return false;
		} else if (SharedConstants.isValidChar(chr)) {
			if (this.editable) {
				this.write(Character.toString(chr));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void renderButton(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		if (this.isVisible()) {

			NVGFont font = NVGHolder.getFont();
			long ctx = NVGHolder.getContext();

			fillRoundedRect(ctx, getX(), getY() + getHeight(), getWidth(), 1, isFocused() ? Colors.accent2() : Colors.accent(), 1);
			Color i = Colors.text();// new Color(this.editable ? this.editableColor : this.uneditableColor);
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

			if (!string.isEmpty()) {
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

			if (!string.isEmpty() && bl && j < string.length()) {
				n = (int) drawString(ctx, font, string.substring(j), n, (float) m, i);
			}

			if (bl2) {
				if (bl3) {
					fillRoundedRect(ctx, o, m - 1, 1, 2 + NVGHolder.getFont().getLineHeight(), i, 2);
				} else {
					drawString(ctx, font, UNDERSCORE, o, (float) m, i);
				}
			}

			if (k != j) {
				float p = l + font.getWidth(string.substring(0, k));
				this.drawSelectionHighlight(ctx, o, m - 1, p - 1, m + 1 + NVGHolder.getFont().getLineHeight());
			}
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		this.setFocused(true);
		int i = MathHelper.floor(mouseX) - this.getX();
		if (this.drawsBackground) {
			i -= 4;
		}

		String string = NVGHolder.getFont().trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
		this.setCursor(NVGHolder.getFont().trimToWidth(string, i).length() + this.firstCharacterIndex, Screen.hasShiftDown());
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
		NanoVG.nvgRect(ctx, x1, y1, x2 - x1, y2 - y1);
		NanoVG.nvgFillColor(ctx, highlightColor.toNVG());
		NanoVG.nvgFill(ctx);
	}

	protected int getMaxLength() {
		return this.maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
		if (this.text.length() > maxLength) {
			this.text = this.text.substring(0, maxLength);
			this.onChanged(this.text);
		}
	}

	public int getCursor() {
		return this.selectionStart;
	}

	protected boolean drawsBackground() {
		return this.drawsBackground;
	}

	public void setDrawsBackground(boolean drawsBackground) {
		this.drawsBackground = drawsBackground;
	}

	public void setEditableColor(int color) {
		this.editableColor = color;
	}

	public void setUneditableColor(int color) {
		this.uneditableColor = color;
	}

	@Override
	public void setFocused(boolean focused) {
		if (this.focusUnlocked || focused) {
			super.setFocused(focused);
			if (focused) {
				this.focusedTime = Util.getMeasuringTimeMs();
			}
		}
	}

	private boolean isEditable() {
		return this.editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public int getInnerWidth() {
		return this.drawsBackground() ? this.getWidth() - 8 : this.getWidth();
	}

	public void setSelectionEnd(int index) {
		int i = this.text.length();
		this.selectionEnd = MathHelper.clamp(index, 0, i);
		if (this.firstCharacterIndex > i) {
			this.firstCharacterIndex = i;
		}

		int j = this.getInnerWidth();
		String string = trimToWidth(this.text.substring(this.firstCharacterIndex), j);
		int k = string.length() + this.firstCharacterIndex;
		if (this.selectionEnd == this.firstCharacterIndex) {
			this.firstCharacterIndex -= trimToWidth(this.text, j, true).length();
		}

		if (this.selectionEnd > k) {
			this.firstCharacterIndex += this.selectionEnd - k;
		} else if (this.selectionEnd <= this.firstCharacterIndex) {
			this.firstCharacterIndex -= this.firstCharacterIndex - this.selectionEnd;
		}

		this.firstCharacterIndex = MathHelper.clamp(this.firstCharacterIndex, 0, i);

	}

	public void setFocusUnlocked(boolean focusUnlocked) {
		this.focusUnlocked = focusUnlocked;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setSuggestion(@Nullable String suggestion) {
		this.suggestion = suggestion;
	}

	public int getCharacterX(int index) {
		return index > this.text.length() ? this.getX() : (int) (this.getX() + getStringWidth(this.text.substring(0, index)));
	}

	public void setHint(@Nullable String hint) {
		this.hint = hint;
	}

	protected float getStringWidth(String text) {
		return NVGHolder.getFont().getWidth(text);
	}

	protected String trimToWidth(String text, float width) {
		return NVGHolder.getFont().trimToWidth(text, width);
	}

	protected String trimToWidth(String text, float width, boolean backwards) {
		return NVGHolder.getFont().trimToWidth(text, width, backwards);
	}

	@Override
	public SelectionType getType() {
		return isHovered() ? SelectionType.HOVERED : (isFocused() ? SelectionType.FOCUSED : SelectionType.NONE);
	}
}
