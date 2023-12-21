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

package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public abstract class ElementListWidget<E extends ElementListWidget.Entry<E>> extends EntryListWidget<E> {
	private boolean widgetFocused;

	public ElementListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	public boolean changeFocus(boolean lookForwards) {
		this.widgetFocused = super.changeFocus(lookForwards);
		if (this.widgetFocused) {
			this.ensureVisible(this.getFocused());
		}

		return this.widgetFocused;
	}

	public SelectionType getType() {
		return this.widgetFocused ? SelectionType.FOCUSED : super.getType();
	}

	protected boolean isSelectedEntry(int index) {
		return false;
	}

	public void appendNarrations(NarrationMessageBuilder builder) {
		E entry = (E) this.getHoveredEntry();
		if (entry != null) {
			entry.appendNarrations(builder.nextMessage());
			this.appendNarrations(builder, entry);
		} else {
			E entry2 = (E) this.getFocused();
			if (entry2 != null) {
				entry2.appendNarrations(builder.nextMessage());
				this.appendNarrations(builder, entry2);
			}
		}

		builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
	}

	public abstract static class Entry<E extends ElementListWidget.Entry<E>>
		extends EntryListWidget.Entry<E>
		implements ParentElement {
		@Nullable
		private Element focused;
		@Nullable
		private Selectable focusedSelectable;
		private boolean dragging;

		public Entry() {
		}

		@Override
		public boolean isDragging() {
			return this.dragging;
		}

		@Override
		public void setDragging(boolean dragging) {
			this.dragging = dragging;
		}

		@Nullable
		@Override
		public Element getFocused() {
			return this.focused;
		}

		@Override
		public void setFocused(@Nullable Element focused) {
			this.focused = focused;
		}

		public abstract List<? extends Selectable> selectableChildren();

		void appendNarrations(NarrationMessageBuilder builder) {
			List<? extends Selectable> list = this.selectableChildren();
			Screen.SelectedElementNarrationData selectedElementNarrationData = Screen.findSelectedElementData(list, this.focusedSelectable);
			if (selectedElementNarrationData != null) {
				if (selectedElementNarrationData.selectType.isFocused()) {
					this.focusedSelectable = selectedElementNarrationData.selectable;
				}

				if (list.size() > 1) {
					builder.put(
						NarrationPart.POSITION,
						Text.translatable("narrator.position.object_list", new Object[]{selectedElementNarrationData.index + 1, list.size()})
					);
					if (selectedElementNarrationData.selectType == SelectionType.FOCUSED) {
						builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
					}
				}

				selectedElementNarrationData.selectable.appendNarrations(builder.nextMessage());
			}
		}
	}
}
