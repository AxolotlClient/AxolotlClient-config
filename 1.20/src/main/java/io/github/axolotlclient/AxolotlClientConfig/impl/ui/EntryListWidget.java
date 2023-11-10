package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.options.WidgetIdentifieable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.list.ElementListWidget;
import org.jetbrains.annotations.Nullable;

public class EntryListWidget extends ElementListWidget<EntryListWidget.Entry> {

	protected static int WIDGET_WIDTH = 150;
	protected static int WIDGET_ROW_LEFT = -155;
	protected static int WIDGET_ROW_RIGHT = WIDGET_ROW_LEFT + WIDGET_WIDTH + 10;

	public EntryListWidget(OptionCategory category, int screenWidth, int screenHeight, int top, int bottom, int entryHeight) {
		super(MinecraftClient.getInstance(), screenWidth, screenHeight, top, bottom, entryHeight);
		centerListVertically = false;

		addEntries(category);
	}

	public void addEntry(OptionCategory first, @Nullable OptionCategory second) {
		addEntry(createCategoryEntry(createWidget(width / 2 + WIDGET_ROW_LEFT, first), first,
			second == null ? null : createWidget(width / 2 + WIDGET_ROW_RIGHT, second), second));
	}

	public void addEntry(Option<?> first, @Nullable Option<?> second) {
		addEntry(createOptionEntry(createWidget(width / 2 + WIDGET_ROW_LEFT, first), first,
			second == null ? null : createWidget(width / 2 + WIDGET_ROW_RIGHT, second), second));
	}

	protected void addCategories(Collection<OptionCategory> categories) {
		List<OptionCategory> list = new ArrayList<>(categories);
		for (int i = 0; i < list.size(); i += 2) {
			addEntry(list.get(i), i < list.size() - 1 ? list.get(i + 1) : null);
		}
	}

	protected void addOptions(Collection<Option<?>> options) {
		List<Option<?>> list = new ArrayList<>(options);
		for (int i = 0; i < list.size(); i += 2) {
			addEntry(list.get(i), i < list.size() - 1 ? list.get(i + 1) : null);
		}
	}

	public void addEntries(OptionCategory root) {
		addCategories(root.getSubCategories());
		if (!children().isEmpty() && !root.getOptions().isEmpty()) {
			addEntry(new Entry(Collections.emptyList()));
		}
		addOptions(root.getOptions());
	}

	protected ClickableWidget createWidget(int x, WidgetIdentifieable id) {
		try {
			return (ClickableWidget) ConfigUI.getInstance().getWidget(id.getWidgetIdentifier(), this.getClass().getClassLoader())
				.getConstructor(int.class, int.class, int.class, int.class, id.getClass())
				.newInstance(x, 0, WIDGET_WIDTH, itemHeight - 5, id);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				 NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
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
