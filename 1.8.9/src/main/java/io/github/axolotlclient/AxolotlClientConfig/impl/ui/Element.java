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

public interface Element extends DrawingUtil {

	default boolean mouseClicked(double mouseX, double mouseY, int button) {
		return false;
	}

	default boolean mouseReleased(double mouseX, double mouseY, int button) {
		return false;
	}

	default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return false;
	}

	default boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
		return false;
	}

	default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	default boolean charTyped(char chr, int modifiers) {
		return false;
	}

	default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	default boolean isMouseOver(double mouseX, double mouseY) {
		return false;
	}

	boolean isFocused();

	void setFocused(boolean focused);
}
