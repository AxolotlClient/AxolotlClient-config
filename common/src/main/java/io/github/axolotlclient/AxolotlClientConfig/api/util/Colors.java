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

package io.github.axolotlclient.AxolotlClientConfig.api.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Colors {

	public final Color WHITE = new Color(-1).immutable();
	public final Color TURQUOISE = Color.parse("#009999").immutable();
	public final Color TRANSPARENT = new Color(0, 0, 0, 0).immutable();
	public final Color RED = new Color(255, 0, 0).immutable();
	public final Color GREEN = new Color(0, 255, 0).immutable();
	public final Color BLUE = new Color(0, 0, 255).immutable();
	public final Color DARK_YELLOW = new Color(155, 155, 0).immutable();
	public final Color YELLOW = new Color(255, 255, 0).immutable();
	public final Color BLACK = new Color(0, 0, 0).immutable();
	public final Color GRAY = new Color(127, 127, 127).immutable();
	public final Color DARK_GRAY = new Color(50, 50, 50).immutable();


	public static Color background() {
		return DARK_GRAY;
	}

	public static Color foreground() {
		return GRAY;
	}

	public static Color accent() {
		return TURQUOISE;
	}

	public static Color accent2() {
		return DARK_YELLOW;
	}

	public static Color text() {
		return WHITE;
	}

	public static Color highlight() {
		return WHITE;
	}
}
