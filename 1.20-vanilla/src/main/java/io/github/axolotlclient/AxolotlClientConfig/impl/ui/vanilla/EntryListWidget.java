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

package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla;

import java.util.*;
import java.util.stream.Stream;

import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.options.WidgetIdentifieable;
import io.github.axolotlclient.AxolotlClientConfig.api.util.AlphabeticalComparator;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

public class EntryListWidget extends ElementListWidget<EntryListWidget.Entry> {

	protected static int WIDGET_WIDTH = 150;
	protected static int WIDGET_ROW_LEFT = -155;
	protected static int WIDGET_ROW_RIGHT = WIDGET_ROW_LEFT + WIDGET_WIDTH + 10;
	protected String searchFilter = null;
	@Nullable
	private final ConfigManager manager;
	private final OptionCategory category;

	public EntryListWidget(ConfigManager manager, OptionCategory category, int screenWidth, int screenHeight, int top, int bottom, int entryHeight) {
		super(MinecraftClient.getInstance(), screenWidth, screenHeight, top, bottom, entryHeight);
		centerListVertically = false;
		this.manager = manager;
		this.category = category;

		addEntries(manager, category);
	}

	public void addEntry(OptionCategory first, @Nullable OptionCategory second) {
		addEntry(createCategoryEntry(createWidget(width / 2 + WIDGET_ROW_LEFT, first), first,
			second == null ? null : createWidget(width / 2 + WIDGET_ROW_RIGHT, second), second));
	}

	public void addEntry(Option<?> first, @Nullable Option<?> second) {
		addEntry(createOptionEntry(createWidget(width / 2 + WIDGET_ROW_LEFT, first), first,
			second == null ? null : createWidget(width / 2 + WIDGET_ROW_RIGHT, second), second));
	}

	protected void addCategories(ConfigManager manager, Collection<OptionCategory> categories) {
		List<OptionCategory> list = new ArrayList<>(categories);
		if (manager != null) {
			list.removeIf(c -> manager.getSuppressedNames().contains(c.getName()));
		}
		for (int i = 0; i < list.size(); i += 2) {
			addEntry(list.get(i), i < list.size() - 1 ? list.get(i + 1) : null);
		}
	}

	protected void addOptions(ConfigManager manager, Collection<Option<?>> options) {
		List<Option<?>> list = new ArrayList<>(options);
		if (manager != null) {
			list.removeIf(o -> manager.getSuppressedNames().contains(o.getName()));
		}
		for (int i = 0; i < list.size(); i += 2) {
			addEntry(list.get(i), i < list.size() - 1 ? list.get(i + 1) : null);
		}
	}

	public void addEntries(ConfigManager manager, OptionCategory category) {
		addCategories(manager, category.getSubCategories());
		if (!children().isEmpty() && !category.getOptions().isEmpty()) {
			addEntry(new Entry(Collections.emptyList()));
		}
		addOptions(manager, category.getOptions());
	}

	public void setSearchFilter(String filter) {
		this.searchFilter = filter;
		clearEntries();
		if (searchFilter == null) {
			addEntries(manager, category);
			return;
		}
		List<OptionCategory> flattenedCategories = new ArrayList<>();
		List<Option<?>> flattenedOptions = new ArrayList<>();
		collectEntries(category, flattenedCategories, flattenedOptions);
		flattenedCategories.removeIf(c -> !I18n.translate(c.getName()).toLowerCase(Locale.ROOT).contains(searchFilter.toLowerCase(Locale.ROOT)));
		flattenedOptions.removeIf(c -> !I18n.translate(c.getName()).toLowerCase(Locale.ROOT).contains(searchFilter.toLowerCase(Locale.ROOT)));
		flattenedCategories.sort((o1, o2) -> AlphabeticalComparator.cmp(I18n.translate(o1.getName()), I18n.translate(o2.getName())));
		flattenedOptions.sort((o1, o2) -> AlphabeticalComparator.cmp(I18n.translate(o1.getName()), I18n.translate(o2.getName())));
		addCategories(manager, flattenedCategories);
		addOptions(manager, flattenedOptions);
	}

	protected void collectEntries(OptionCategory current, List<OptionCategory> categoryCollector, List<Option<?>> optionCollector) {
		optionCollector.addAll(current.getOptions());
		current.getSubCategories().forEach(c -> {
			categoryCollector.add(c);
			collectEntries(c, categoryCollector, optionCollector);
		});
	}

	protected ClickableWidget createWidget(int x, WidgetIdentifieable id) {
		return ConfigStyles.createWidget(x, 0, WIDGET_WIDTH, itemHeight - 5, id);
	}

	protected Entry createOptionEntry(ClickableWidget widget, Option<?> option, @Nullable ClickableWidget other, @Nullable Option<?> otherOption) {
		return Entry.create(widget, other);
	}

	protected Entry createCategoryEntry(ClickableWidget widget, OptionCategory optionCategory, @Nullable ClickableWidget other, @Nullable OptionCategory otherOptionCategory) {
		return Entry.create(widget, other);
	}

	@Override
	public int getRowWidth() {
		return 400;
	}

	@Override
	protected int getScrollbarPositionX() {
		return super.getScrollbarPositionX() + 38;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
		return getHoveredEntry() != null && getHoveredEntry().mouseScrolled(mouseX, mouseY, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollY);
	}

	protected static class Entry extends ElementListWidget.Entry<Entry> {


		private final List<ClickableWidget> children = new ArrayList<>();

		public Entry(Collection<ClickableWidget> widgets) {
			children.addAll(widgets);
		}

		public static Entry create(ClickableWidget first, ClickableWidget other) {
			return new Entry(Stream.of(first, other).filter(Objects::nonNull).toList());
		}

		@Override
		public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			children.forEach(c -> {
				c.setY(y);
				c.render(graphics, mouseX, mouseY, tickDelta);
			});
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return children;
		}

		@Override
		public List<? extends Element> children() {
			return children;
		}
	}
}
