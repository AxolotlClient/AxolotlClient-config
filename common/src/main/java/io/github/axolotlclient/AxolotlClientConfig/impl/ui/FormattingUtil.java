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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

public class FormattingUtil implements DrawingUtil {
	private final Random random = new Random();
	private static final FormattingUtil INSTANCE = new FormattingUtil();
	private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
	private static final Pattern PARAGRAPH_PATTERN = Pattern.compile("§");
	private final Map<Character, Color> MINECRAFT_COLOR_CODES;


	static FormattingUtil getInstance() {
		return INSTANCE;
	}

	private FormattingUtil() {
		Map<Character, Color> map = new HashMap<>();
		map.put('0', new Color(0)); // black
		map.put('1', new Color(0xFF0000B3)); // dark blue
		map.put('2', new Color(0xFF00AA00)); // dark green
		map.put('3', new Color(0xFF00AAAA)); // dark aqua
		map.put('4', new Color(0xFFAA0000)); // dark red
		map.put('5', new Color(0xFFAA00AA)); // dark purple
		map.put('6', new Color(0xFFFFAA00)); // gold
		map.put('7', new Color(0xFFAAAAAA)); // gray
		map.put('8', new Color(0xFF555555)); // dark gray
		map.put('9', new Color(0xFF5555FF)); // blue
		map.put('a', new Color(0xFF55FF55)); // green
		map.put('b', new Color(0xFF55FFFF)); // aqua
		map.put('c', new Color(0xFFFF5555)); // red
		map.put('d', new Color(0xFFFF55FF)); // light_purple
		map.put('e', new Color(0xFFFFFF55)); // yellow
		map.put('f', new Color(0xFFFFFFFF)); // white
		MINECRAFT_COLOR_CODES = map;
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
				List<Line> decorations = new ArrayList<>(parts.length);
				for (int i = 0, partsLength = parts.length; i < partsLength; i++) {
					String part = parts[i];
					if (part.isEmpty()) {
						continue;
					}
					if (i != 0) {
						char first = part.charAt(0);
						switch (first) {
							case 'm':
								strikethrough = true;
								part = part.substring(1);
								break;
							case 'k': // obfuscated
								part = part.substring(1);
								part = obfuscateString(font, part);
								break;
							case 'l':
								bold = boldItalicSupported;
								part = part.substring(1);
								break;
							case 'n':
								underlined = true;
								part = part.substring(1);
								break;
							case 'o':
								italic = boldItalicSupported;
								part = part.substring(1);
								break;
							case 'r':
								strikethrough = italic = bold = underlined = false;
								part = part.substring(1);
								break;
							default:
								if (MINECRAFT_COLOR_CODES.containsKey(first)) {
									textColor = MINECRAFT_COLOR_CODES.get(first).withAlpha(color.getAlpha()).toNVG();
									part = part.substring(1);
								} else {
									textColor = color.toNVG();
								}
						}
					}
					NanoVG.nvgFillColor(ctx, textColor);
					if (bold || italic) { // bold and italic actually are separate fonts
						partX = ((VariantFont) font).renderString(part, lastPartX = partX, y, italic, bold);
					} else {
						partX = font.renderString(part, lastPartX = partX, y);
					}
					if (underlined || strikethrough) {
						if (underlined) {
							decorations.add(new Line(lastPartX, y+lineHeight+1, partX, y+lineHeight+1));
						}
						if (strikethrough) {
							decorations.add(new Line(lastPartX, y+lineHeight/2+1, partX, y+lineHeight/2+1));
						}
					}
				}
				if (!decorations.isEmpty()) {
					NanoVG.nvgBeginPath(ctx);
					decorations.forEach(line -> {
						NanoVG.nvgMoveTo(ctx, line.x(), line.y());
						NanoVG.nvgLineTo(ctx, line.x2(), line.y2());
					});
					NanoVG.nvgFill(ctx);
				}
				return partX;
			}
		}
		NanoVG.nvgFillColor(ctx, color.toNVG());
		font.bind();
		return font.renderString(text, x, y);
	}

	private String obfuscateString(NVGFont font, String s) {
		String characters = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■";
		StringBuilder builder = new StringBuilder(s.length());
		for (char c : s.toCharArray()) {
			float width = font.getWidth(String.valueOf(c));
			char n;
			do {
				n = characters.charAt(random.nextInt(characters.length()));
			} while (font.getWidth(String.valueOf(n)) != width);
			builder.append(n);
		}
		return builder.toString();
	}

	private record Line(float x, float y, float x2, float y2) {}
}
