package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.options.WidgetIdentifieable;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

public class ButtonListWidget extends ElementListWidget<ButtonListWidget.Entry> {

	protected static int WIDGET_WIDTH = 150;
	protected static int WIDGET_ROW_LEFT = -155;
	protected static int WIDGET_ROW_RIGHT = WIDGET_ROW_LEFT + WIDGET_WIDTH + 10;

	public ButtonListWidget(ConfigManager manager, OptionCategory category, int screenWidth, int screenHeight, int top, int bottom, int entryHeight) {
		super(MinecraftClient.getInstance(), screenWidth, screenHeight, top, bottom, entryHeight);
		centerListVertically = false;

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
		List<OptionCategory> list = categories.stream()
			.filter(c -> !manager.getSuppressedNames().contains(c.getName())).collect(Collectors.toList());
		for (int i = 0; i < list.size(); i += 2) {
			addEntry(list.get(i), i < list.size() - 1 ? list.get(i + 1) : null);
		}
	}

	protected void addOptions(ConfigManager manager, Collection<Option<?>> options) {
		List<Option<?>> list = options.stream()
			.filter(o -> !manager.getSuppressedNames().contains(o.getName())).collect(Collectors.toList());
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

	protected AbstractButtonWidget createWidget(int x, WidgetIdentifieable id) {
		return ConfigStyles.createWidget(x, 0, WIDGET_WIDTH, itemHeight - 5, id);
	}

	protected Entry createOptionEntry(AbstractButtonWidget widget, Option<?> option, @Nullable AbstractButtonWidget other, @Nullable Option<?> otherOption) {
		return Entry.create(widget, other);
	}

	protected Entry createCategoryEntry(AbstractButtonWidget widget, OptionCategory optionCategory, @Nullable AbstractButtonWidget other, @Nullable OptionCategory otherOptionCategory) {
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
	protected void renderList(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		DrawUtil.pushScissor(left, top, right - left, bottom - top);
		super.renderList(matrices, mouseX, mouseY, delta);
		DrawUtil.popScissor();
	}

	protected static class Entry extends ElementListWidget.Entry<Entry> {


		private final List<AbstractButtonWidget> children = new ArrayList<>();

		public Entry(Collection<AbstractButtonWidget> widgets) {
			children.addAll(widgets);
		}

		public static Entry create(AbstractButtonWidget first, AbstractButtonWidget other) {
			return new Entry(Stream.of(first, other).filter(Objects::nonNull).collect(Collectors.toList()));
		}

		@Override
		public void render(MatrixStack graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			children.forEach(c -> {
				c.y = y;
				c.render(graphics, mouseX, mouseY, tickDelta);
			});
		}

		@Override
		public List<? extends Element> children() {
			return children;
		}
	}
}
