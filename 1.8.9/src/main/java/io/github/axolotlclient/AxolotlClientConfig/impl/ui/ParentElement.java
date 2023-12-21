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

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public interface ParentElement extends Element {
	List<? extends Element> children();

	default Optional<Element> hoveredElement(double mouseX, double mouseY) {
		for (Element element : this.children()) {
			if (element.isMouseOver(mouseX, mouseY)) {
				return Optional.of(element);
			}
		}

		return Optional.empty();
	}

	@Override
	default boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (Element element : this.children()) {
			if (element.mouseClicked(mouseX, mouseY, button)) {
				this.setFocusedChild(element);
				if (button == 0) {
					this.setDragging(true);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	default boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.setDragging(false);
		return this.hoveredElement(mouseX, mouseY).filter(element -> element.mouseReleased(mouseX, mouseY, button)).isPresent();
	}

	@Override
	default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return this.getFocused() != null && this.isDragging() && button == 0 ? this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY) : false;
	}

	boolean isDragging();

	void setDragging(boolean dragging);

	default boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
		return this.hoveredElement(mouseX, mouseY).filter(element -> element.mouseScrolled(mouseX, mouseY, amountX, amountY)).isPresent();
	}

	@Override
	default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.getFocused() != null && this.getFocused().keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return this.getFocused() != null && this.getFocused().keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	default boolean charTyped(char chr, int modifiers) {
		return this.getFocused() != null && this.getFocused().charTyped(chr, modifiers);
	}

	@Nullable
	Element getFocused();

	void setFocusedChild(@Nullable Element child);

	default boolean isFocused() {
		return this.getFocused() != null;
	}

	default void setFocused(boolean focused) {
	}
}
