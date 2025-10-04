/*
 * Copyright © 2021-2025 moehreag <moehreag@gmail.com> & Contributors
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

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGFont;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.nanovg.NanoVG;

@Environment(EnvType.CLIENT)
public class RoundedEditBox extends AbstractWidget implements DrawingUtil {
	public static final int BACKWARDS = -1;
	public static final int FORWARDS = 1;
	private static final int CURSOR_INSERT_WIDTH = 1;
	private static final String CURSOR_APPEND_CHARACTER = "_";
	public static final Style DEFAULT_HINT_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);
	public static final Style SEARCH_HINT_STYLE = Style.EMPTY.applyFormats(ChatFormatting.GRAY, ChatFormatting.ITALIC);
	private static final int CURSOR_BLINK_INTERVAL_MS = 300;
	private final Color highlightColor = Colors.accent2().withAlpha(100);
	@Getter
	private String value;
	private int maxLength;
	@Getter
	private boolean bordered;
	@Setter
	private boolean canLoseFocus;
	@Setter
	private boolean isEditable;
	private boolean centered;
	private int displayPos;
	private int cursorPos;
	private int highlightPos;
	@Nullable
	private String suggestion;
	@Setter
	@Nullable
	private Consumer<String> responder;
	@Setter
	private Predicate<String> filter;
	@Nullable
	private Component hint;
	private long focusedTime;
	private float textX;
	private float textY;

	public RoundedEditBox(int width, int height, Component message) {
		this(0, 0, width, height, message);
	}

	public RoundedEditBox(int x, int y, int width, int height, Component message) {
		this(x, y, width, height, null, message);
	}

	public RoundedEditBox(int x, int y, int width, int height, @Nullable EditBox editBox, Component message) {
		super(x, y, width, height, message);
		this.value = "";
		this.maxLength = 32;
		this.bordered = true;
		this.canLoseFocus = true;
		this.isEditable = true;
		this.centered = false;
		this.filter = Objects::nonNull;
		this.focusedTime = Util.getMillis();
		if (editBox != null) {
			this.setValue(editBox.getValue());
		}

		this.updateTextPosition();
	}

	protected @NotNull MutableComponent createNarrationMessage() {
		Component component = this.getMessage();
		return Component.translatable("gui.narrate.editBox", component, this.value);
	}

	public void setValue(String text) {
		if (this.filter.test(text)) {
			if (text.length() > this.maxLength) {
				this.value = text.substring(0, this.maxLength);
			} else {
				this.value = text;
			}

			this.moveCursorToEnd(false);
			this.setHighlightPos(this.cursorPos);
			this.onValueChange(text);
		}
	}

	public String getHighlighted() {
		int i = Math.min(this.cursorPos, this.highlightPos);
		int j = Math.max(this.cursorPos, this.highlightPos);
		return this.value.substring(i, j);
	}

	public void setX(int x) {
		super.setX(x);
		this.updateTextPosition();
	}

	public void setY(int y) {
		super.setY(y);
		this.updateTextPosition();
	}

	public void insertText(String textToWrite) {
		int i = Math.min(this.cursorPos, this.highlightPos);
		int j = Math.max(this.cursorPos, this.highlightPos);
		int k = this.maxLength - this.value.length() - (i - j);
		if (k > 0) {
			String string = StringUtil.filterText(textToWrite);
			int l = string.length();
			if (k < l) {
				if (Character.isHighSurrogate(string.charAt(k - 1))) {
					--k;
				}

				string = string.substring(0, k);
				l = k;
			}

			String string2 = (new StringBuilder(this.value)).replace(i, j, string).toString();
			if (this.filter.test(string2)) {
				this.value = string2;
				this.setCursorPosition(i + l);
				this.setHighlightPos(this.cursorPos);
				this.onValueChange(this.value);
			}
		}
	}

	private void onValueChange(String newText) {
		if (this.responder != null) {
			this.responder.accept(newText);
		}

		this.updateTextPosition();
	}

	private void deleteText(int i, boolean bl) {
		if (bl) {
			this.deleteWords(i);
		} else {
			this.deleteChars(i);
		}

	}

	public void deleteWords(int num) {
		if (!this.value.isEmpty()) {
			if (this.highlightPos != this.cursorPos) {
				this.insertText("");
			} else {
				this.deleteCharsToPos(this.getWordPosition(num));
			}
		}
	}

	public void deleteChars(int num) {
		this.deleteCharsToPos(this.getCursorPos(num));
	}

	public void deleteCharsToPos(int num) {
		if (!this.value.isEmpty()) {
			if (this.highlightPos != this.cursorPos) {
				this.insertText("");
			} else {
				int left = Math.min(num, this.cursorPos);
				int right = Math.max(num, this.cursorPos);
				if (left != right) {
					String string = new StringBuilder(this.value).delete(left, right).toString();
					if (this.filter.test(string)) {
						this.value = string;
						this.moveCursorTo(left, false);
					}
				}
			}
		}
	}

	public int getWordPosition(int direction) {
		return this.getWordPosition(direction, this.getCursorPosition());
	}

	private int getWordPosition(int direction, int pos) {
		return this.getWordPosition(direction, pos, true);
	}

	private int getWordPosition(int numWords, int pos, boolean skipConsecutiveSpaces) {
		int i = pos;
		boolean bl = numWords < 0;
		int j = Math.abs(numWords);

		for (int k = 0; k < j; ++k) {
			if (!bl) {
				int l = this.value.length();
				i = this.value.indexOf(32, i);
				if (i == -1) {
					i = l;
				} else {
					while (skipConsecutiveSpaces && i < l && this.value.charAt(i) == ' ') {
						++i;
					}
				}
			} else {
				while (skipConsecutiveSpaces && i > 0 && this.value.charAt(i - 1) == ' ') {
					--i;
				}

				while (i > 0 && this.value.charAt(i - 1) != ' ') {
					--i;
				}
			}
		}

		return i;
	}

	public void moveCursor(int delta, boolean select) {
		this.moveCursorTo(this.getCursorPos(delta), select);
	}

	private int getCursorPos(int delta) {
		return Util.offsetByCodepoints(this.value, this.cursorPos, delta);
	}

	public void moveCursorTo(int delta, boolean select) {
		this.setCursorPosition(delta);
		if (!select) {
			this.setHighlightPos(this.cursorPos);
		}

		this.onValueChange(this.value);
	}

	public void setCursorPosition(int pos) {
		this.cursorPos = Mth.clamp(pos, 0, this.value.length());
		this.scrollTo(this.cursorPos);
	}

	public void moveCursorToStart(boolean select) {
		this.moveCursorTo(0, select);
	}

	public void moveCursorToEnd(boolean select) {
		this.moveCursorTo(this.value.length(), select);
	}

	public boolean keyPressed(KeyEvent keyEvent) {
		if (this.isActive() && this.isFocused()) {
			return switch (keyEvent.key()) {
				case 259 -> {
					if (this.isEditable) {
						this.deleteText(-1, keyEvent.hasControlDown());
					}

					yield true;
				}
				case 261 -> {
					if (this.isEditable) {
						this.deleteText(FORWARDS, keyEvent.hasControlDown());
					}

					yield true;
				}
				case 262 -> {
					if (keyEvent.hasControlDown()) {
						this.moveCursorTo(this.getWordPosition(FORWARDS), keyEvent.hasShiftDown());
					} else {
						this.moveCursor(FORWARDS, keyEvent.hasShiftDown());
					}

					yield true;
				}
				case 263 -> {
					if (keyEvent.hasControlDown()) {
						this.moveCursorTo(this.getWordPosition(BACKWARDS), keyEvent.hasShiftDown());
					} else {
						this.moveCursor(BACKWARDS, keyEvent.hasShiftDown());
					}

					yield true;
				}
				case 268 -> {
					this.moveCursorToStart(keyEvent.hasShiftDown());
					yield true;
				}
				case 269 -> {
					this.moveCursorToEnd(keyEvent.hasShiftDown());
					yield true;
				}
				default -> {
					if (keyEvent.isSelectAll()) {
						this.moveCursorToEnd(false);
						this.setHighlightPos(0);
						yield true;
					} else if (keyEvent.isCopy()) {
						Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
						yield true;
					} else if (keyEvent.isPaste()) {
						if (this.isEditable()) {
							this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
						}

						yield true;
					} else {
						if (keyEvent.isCut()) {
							Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
							if (this.isEditable()) {
								this.insertText("");
							}

							yield true;
						}

						yield false;
					}
				}
			};
		}
		return false;
	}

	public boolean canConsumeInput() {
		return this.isActive() && this.isFocused() && this.isEditable();
	}

	public boolean charTyped(CharacterEvent characterEvent) {
		if (!this.canConsumeInput()) {
			return false;
		} else if (characterEvent.isAllowedChatCharacter()) {
			if (this.isEditable) {
				this.insertText(characterEvent.codepointAsString());
			}

			return true;
		} else {
			return false;
		}
	}

	private int findClickedPositionInText(MouseButtonEvent mouseButtonEvent) {
		var i = Math.min(Mth.floor(mouseButtonEvent.x()) - this.textX, this.getInnerWidth());
		String string = this.value.substring(this.displayPos);
		return this.displayPos + NVGHolder.getFont().trimToWidth(string, i).length();
	}

	private void selectWord(MouseButtonEvent mouseButtonEvent) {
		int i = this.findClickedPositionInText(mouseButtonEvent);
		int j = this.getWordPosition(-1, i);
		int k = this.getWordPosition(1, i);
		this.moveCursorTo(j, false);
		this.moveCursorTo(k, true);
	}

	public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
		if (bl) {
			this.selectWord(mouseButtonEvent);
		} else {
			this.moveCursorTo(this.findClickedPositionInText(mouseButtonEvent), mouseButtonEvent.hasShiftDown());
		}

	}

	protected void onDrag(MouseButtonEvent mouseButtonEvent, double d, double e) {
		this.moveCursorTo(this.findClickedPositionInText(mouseButtonEvent), true);
	}

	public void playDownSound(SoundManager handler) {
	}

	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if (this.isVisible()) {
			NVGFont font = NVGHolder.getFont();
			long ctx = NVGHolder.getContext();

			fillRoundedRect(ctx, getX(), getY() + getHeight(), getWidth(), 1, isFocused() ? Colors.accent2() : Colors.accent(), 1);

			Color i = Colors.text();
			int j = this.cursorPos - this.displayPos;
			String string = font.trimToWidth(this.value.substring(this.displayPos), this.getInnerWidth());
			boolean bl = j >= 0 && j <= string.length();
			boolean bl2 = this.isFocused() && (Util.getMillis() - this.focusedTime) / CURSOR_BLINK_INTERVAL_MS % 2L == 0L && bl;
			float k = this.textX;
			int l = Mth.clamp(this.highlightPos - this.displayPos, 0, string.length());
			if (!string.isEmpty()) {
				String string2 = bl ? string.substring(0, j) : string;
				k = drawString(ctx, font, string2, k, this.textY, i) + 1;
			}

			boolean bl3 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
			float m = k;
			if (!bl) {
				m = j > 0 ? this.textX + this.width : this.textX;
			} else if (bl3) {
				m = k - 1;
				--k;
			}

			if (!string.isEmpty() && bl && j < string.length()) {
				drawString(ctx, font, string.substring(j), k, this.textY, i);
			}

			if (this.hint != null && string.isEmpty() && !this.isFocused()) {
				drawString(ctx, font, DrawUtil.getFormattedString(this.hint), k, this.textY, i);
			}

			if (!bl3 && this.suggestion != null) {
				drawString(ctx, font, this.suggestion, m - 1, this.textY, Colors.GRAY);
			}

			if (l != j) {
				var n = this.textX + font.getWidth(string.substring(0, l));
				NanoVG.nvgBeginPath(ctx);
				float minX = Math.min(m, this.getX() + this.width);
				float minY = this.textY - 1;
				NanoVG.nvgRect(ctx, minX, minY, Math.min(n - 1, this.getX() + this.width) - minX, (this.textY + 1 + font.getLineHeight()) - minY);
				NanoVG.nvgFillColor(ctx, highlightColor.toNVG());
				NanoVG.nvgFill(ctx);
			}

			if (bl2) {
				if (bl3) {
					fillRoundedRect(ctx, m, textY - CURSOR_INSERT_WIDTH, CURSOR_INSERT_WIDTH, 2 + font.getLineHeight(), i, 2);
				} else {
					drawString(ctx, font, CURSOR_APPEND_CHARACTER, m, this.textY, i);
				}
			}

			if (this.isHovered()) {
				guiGraphics.requestCursor(this.isEditable() ? CursorTypes.IBEAM : CursorTypes.NOT_ALLOWED);
			}

		}
	}

	private void updateTextPosition() {
		if (NVGHolder.getFont() != null) {
			String string = NVGHolder.getFont().trimToWidth(this.value.substring(this.displayPos), this.getInnerWidth());
			this.textX = this.getX() + (this.isCentered() ? (this.getWidth() - NVGHolder.getFont().getWidth(string)) / 2 : (this.bordered ? 4 : 0));
			this.textY = this.bordered ? this.getY() + (this.height - 8) / 2f : this.getY();
		}
	}

	public void setMaxLength(int length) {
		this.maxLength = length;
		if (this.value.length() > length) {
			this.value = this.value.substring(0, length);
			this.onValueChange(this.value);
		}

	}

	private int getMaxLength() {
		return this.maxLength;
	}

	public int getCursorPosition() {
		return this.cursorPos;
	}

	public void setBordered(boolean enableBackgroundDrawing) {
		this.bordered = enableBackgroundDrawing;
		this.updateTextPosition();
	}

	public void setFocused(boolean focused) {
		if (this.canLoseFocus || focused) {
			super.setFocused(focused);
			if (focused) {
				this.focusedTime = Util.getMillis();
			}

		}
	}

	private boolean isEditable() {
		return this.isEditable;
	}

	private boolean isCentered() {
		return this.centered;
	}

	public void setCentered(boolean centered) {
		this.centered = centered;
		this.updateTextPosition();
	}

	public int getInnerWidth() {
		return this.isBordered() ? this.width - 8 : this.width;
	}

	public void setHighlightPos(int position) {
		this.highlightPos = Mth.clamp(position, 0, this.value.length());
		this.scrollTo(this.highlightPos);
	}

	private void scrollTo(int position) {
		var font = NVGHolder.getFont();
		if (font != null) {
			this.displayPos = Math.min(this.displayPos, this.value.length());
			int i = this.getInnerWidth();
			String string = font.trimToWidth(this.value.substring(this.displayPos), i);
			int j = string.length() + this.displayPos;
			if (position == this.displayPos) {
				this.displayPos -= font.trimToWidth(this.value, i, true).length();
			}

			if (position > j) {
				this.displayPos += position - j;
			} else if (position <= this.displayPos) {
				this.displayPos -= this.displayPos - position;
			}

			this.displayPos = Mth.clamp(this.displayPos, 0, this.value.length());
		}
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean isVisible) {
		this.visible = isVisible;
	}

	public void setSuggestion(@Nullable String suggestion) {
		this.suggestion = suggestion;
	}

	public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
		narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
	}

	public void setHint(Component hint) {
		this.hint = hint.getStyle().equals(Style.EMPTY) ? hint.copy().withStyle(DEFAULT_HINT_STYLE) : hint;
	}
}

