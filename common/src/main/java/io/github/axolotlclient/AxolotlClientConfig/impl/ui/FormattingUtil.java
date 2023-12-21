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

package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

public class FormattingUtil implements DrawingUtil {
	private static final FormattingUtil INSTANCE = new FormattingUtil();
	private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
	private static final Pattern PARAGRAPH_PATTERN = Pattern.compile("§");
	private final Map<Character, NVGColor> MINECRAFT_COLOR_CODES;

	static FormattingUtil getInstance() {
		return INSTANCE;
	}

	private FormattingUtil() {
		Map<Character, NVGColor> map = new HashMap<>();
		map.put('0', toNVGColor(0)); // black
		map.put('1', toNVGColor(179)); // dark blue
		map.put('2', toNVGColor(43520)); // dark green
		map.put('3', toNVGColor(43690)); // dark aqua
		map.put('4', toNVGColor(11141120)); // dark red
		map.put('5', toNVGColor(11141290)); // dark purple
		map.put('6', toNVGColor(16755200)); // gold
		map.put('7', toNVGColor(11184810)); // gray
		map.put('8', toNVGColor(5592405)); // dark gray
		map.put('9', toNVGColor(5592575)); // blue
		map.put('a', toNVGColor(5635925)); // green
		map.put('b', toNVGColor(5636095)); // aqua
		map.put('c', toNVGColor(16733525)); // red
		map.put('d', toNVGColor(16733695)); // light_purple
		map.put('e', toNVGColor(16777045)); // yellow
		map.put('f', toNVGColor(16777215)); // white
		MINECRAFT_COLOR_CODES = map;
		MINECRAFT_COLOR_CODES.values().forEach(c -> c.a(1));
	}


	public float drawStringWithFormatting(long ctx, NVGFont font, String text, float x, float y, Color color) {
		if (text.contains("§")) { // work out Minecraft's formatting codes
			Matcher matcher = FORMATTING_CODE_PATTERN.matcher(text);
			if (matcher.find()) {
				boolean boldItalicSupported = font instanceof VariantFont;
				float lineHeight = font.getLineHeight();
				String[] parts = PARAGRAPH_PATTERN.split(text);
				NVGColor textColor = color.toNVG();
				float partX = x;
				float lastPartX;
				boolean strikethrough = false, italic = false, bold = false, underlined = false;
				for (String part : parts) {
					if (part.isEmpty()) {
						continue;
					}
					char first = part.charAt(0);
					switch (first) {
						case 'm':
							strikethrough = true;
							break;
						case 'k': // obfuscated
							break;
						case 'l':
							bold = boldItalicSupported;
							break;
						case 'n':
							underlined = true;
							break;
						case 'o':
							italic = boldItalicSupported;
							break;
						case 'r':
							strikethrough = italic = bold = underlined = false;
							break;
						default:
							textColor = MINECRAFT_COLOR_CODES.getOrDefault(first, color.toNVG());
					}
					NanoVG.nvgFillColor(ctx, textColor);
					if (bold || italic) { // bold and italic actually are separate fonts
						partX = ((VariantFont) font).renderString(part.substring(1), lastPartX = partX, y, italic, bold);
					} else {
						partX = font.renderString(part.substring(1), lastPartX = partX, y);
					}
					if (underlined || strikethrough) {
						NanoVG.nvgBeginPath(ctx);
						if (underlined) {
							NanoVG.nvgMoveTo(ctx, lastPartX, y + lineHeight + 1);
							NanoVG.nvgLineTo(ctx, partX, y + lineHeight + 1);
						}
						if (strikethrough) {
							NanoVG.nvgMoveTo(ctx, lastPartX, y + lineHeight / 2 + 1);
							NanoVG.nvgLineTo(ctx, partX, y + lineHeight / 2 + 1);
						}
						NanoVG.nvgFill(ctx);
					}
				}
				return partX;
			}
		}
		NanoVG.nvgFillColor(ctx, color.toNVG());
		font.bind();
		return font.renderString(text, x, y);
	}
}
