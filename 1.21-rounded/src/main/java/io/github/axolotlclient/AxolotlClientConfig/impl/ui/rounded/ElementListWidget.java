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
import net.minecraft.client.gui.ElementPath;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigationEvent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.navigation.NavigationAxis;
import net.minecraft.client.gui.screen.navigation.NavigationDirection;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public abstract class ElementListWidget<E extends ElementListWidget.Entry<E>> extends EntryListWidget<E> {
	public ElementListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	@Nullable
	@Override
	public ElementPath nextFocusPath(GuiNavigationEvent event) {
		if (this.getEntryCount() == 0) {
			return null;
		} else if (!(event instanceof GuiNavigationEvent.TabNavigation tabNavigation)) {
			return super.nextFocusPath(event);
		} else {
			E entry = this.getFocused();
			if (tabNavigation.direction().getAxis() == NavigationAxis.HORIZONTAL && entry != null) {
				return ElementPath.createPath(this, entry.nextFocusPath(event));
			} else {
				int i = -1;
				NavigationDirection navigationDirection = tabNavigation.direction();
				if (entry != null) {
					i = entry.children().indexOf(entry.getFocused());
				}

				if (i == -1) {
					switch (navigationDirection) {
						case LEFT:
							i = Integer.MAX_VALUE;
							navigationDirection = NavigationDirection.DOWN;
							break;
						case RIGHT:
							i = 0;
							navigationDirection = NavigationDirection.DOWN;
							break;
						default:
							i = 0;
					}
				}

				E entry2 = entry;

				ElementPath elementPath;
				do {
					entry2 = this.nextEntry(navigationDirection, entryx -> !entryx.children().isEmpty(), entry2);
					if (entry2 == null) {
						return null;
					}

					elementPath = entry2.getFocusPathAtIndex(tabNavigation, i);
				} while (elementPath == null);

				return ElementPath.createPath(this, elementPath);
			}
		}
	}

	@Override
	public void setFocusedChild(@Nullable Element child) {
		super.setFocusedChild(child);
		if (child == null) {
			this.setSelected(null);
		}
	}

	@Override
	public Selectable.SelectionType getType() {
		return this.isFocused() ? Selectable.SelectionType.FOCUSED : super.getType();
	}

	@Override
	protected boolean isSelectedEntry(int index) {
		return false;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		E entry = this.getHoveredEntry();
		if (entry != null) {
			entry.appendNarrations(builder.nextMessage());
			this.appendNarrations(builder, entry);
		} else {
			E entry2 = this.getFocused();
			if (entry2 != null) {
				entry2.appendNarrations(builder.nextMessage());
				this.appendNarrations(builder, entry2);
			}
		}

		builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
	}

	public abstract static class Entry<E extends ElementListWidget.Entry<E>> extends EntryListWidget.Entry<E> implements ParentElement {
		@Nullable
		private Element focused;
		@Nullable
		private Selectable focusedSelectable;
		private boolean dragging;

		@Override
		public boolean isDragging() {
			return this.dragging;
		}

		@Override
		public void setDragging(boolean dragging) {
			this.dragging = dragging;
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			return ParentElement.super.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public void setFocusedChild(@Nullable Element child) {
			if (this.focused != null) {
				this.focused.setFocused(false);
			}

			if (child != null) {
				child.setFocused(true);
			}

			this.focused = child;
		}

		@Nullable
		@Override
		public Element getFocused() {
			return this.focused;
		}

		@Nullable
		public ElementPath getFocusPathAtIndex(GuiNavigationEvent event, int index) {
			if (this.children().isEmpty()) {
				return null;
			} else {
				ElementPath elementPath = this.children().get(Math.min(index, this.children().size() - 1)).nextFocusPath(event);
				return ElementPath.createPath(this, elementPath);
			}
		}

		@Nullable
		@Override
		public ElementPath nextFocusPath(GuiNavigationEvent event) {
			if (event instanceof GuiNavigationEvent.TabNavigation tabNavigation) {
				int i = switch (tabNavigation.direction()) {
					case LEFT -> -1;
					case RIGHT -> 1;
					case UP, DOWN -> 0;
				};
				if (i == 0) {
					return null;
				}

				int j = MathHelper.clamp(i + this.children().indexOf(this.getFocused()), 0, this.children().size() - 1);

				for (int k = j; k >= 0 && k < this.children().size(); k += i) {
					Element element = this.children().get(k);
					ElementPath elementPath = element.nextFocusPath(event);
					if (elementPath != null) {
						return ElementPath.createPath(this, elementPath);
					}
				}
			}

			return ParentElement.super.nextFocusPath(event);
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
					builder.put(NarrationPart.POSITION, Text.translatable("narrator.position.object_list", selectedElementNarrationData.index + 1, list.size()));
					if (selectedElementNarrationData.selectType == Selectable.SelectionType.FOCUSED) {
						builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
					}
				}

				selectedElementNarrationData.selectable.appendNarrations(builder.nextMessage());
			}
		}
	}
}
