package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.WidgetIdentifieable;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

public class ButtonListWidget extends ElementListWidget<ButtonListWidget.Entry> implements DrawingUtil {

	protected static int WIDGET_WIDTH = 150;
	protected static int WIDGET_ROW_LEFT = -155;
	protected static int WIDGET_ROW_RIGHT = -155+160;
	public ButtonListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		super(client, width, height, top, bottom, itemHeight);
		centerListVertically = false;
	}

	public void addEntry(OptionCategory first, @Nullable OptionCategory second){
		addEntry(createCategoryEntry(createWidget(width/2 + WIDGET_ROW_LEFT, first), first,
			second == null ? null : createWidget(width/2 + WIDGET_ROW_RIGHT, second), second));
	}

	public void addEntry(Option<?> first, @Nullable Option<?> second){
		addEntry(createOptionEntry(createWidget(width/2 + WIDGET_ROW_LEFT, first), first,
			second == null ? null : createWidget(width/2 + WIDGET_ROW_RIGHT, second), second));
	}

	protected void addCategories(Collection<OptionCategory> categories){
		List<OptionCategory> list = new ArrayList<>(categories);
		for (int i = 0;i<list.size();i+=2){
			addEntry(list.get(i), i<list.size()-1 ? list.get(i+1) : null);
		}
	}

	protected void addOptions(Collection<Option<?>> options){
		List<Option<?>> list = new ArrayList<>(options);
		for (int i = 0;i<list.size();i+=2){
			addEntry(list.get(i), i<list.size()-1 ? list.get(i+1) : null);
		}
	}

	public void addEntries(OptionCategory root){
		addCategories(root.getSubCategories());
		if (!children().isEmpty()){
			addEntry(new Entry() {
				@Override
				public List<? extends Element> children() {
					return Collections.emptyList();
				}
			});
		}
		addOptions(root.getOptions());
	}

	protected ClickableWidget createWidget(int x, WidgetIdentifieable id){
		try {
			return (ClickableWidget) ConfigUI.getInstance().getWidget(id.getWidgetIdentifier(), this.getClass().getClassLoader())
				.getConstructor(int.class, int.class, int.class, int.class, id.getClass())
				.newInstance(x, 0, WIDGET_WIDTH, itemHeight-5, id);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
	}

	protected Entry createOptionEntry(ClickableWidget widget, Option<?> option, @Nullable ClickableWidget other, @Nullable Option<?> otherOption){
		return ButtonEntry.create(widget, other);
	}

	protected Entry createCategoryEntry(ClickableWidget widget, OptionCategory optionCategory, @Nullable ClickableWidget other, @Nullable OptionCategory otherOptionCategory){
		return CategoryEntry.create(widget, other);
	}

	@Override
	public int getRowWidth() {
		return 400;
	}

	@Override
	protected int getScrollbarPositionX() {
		return super.getScrollbarPositionX() + 38;
	}

	public abstract static class Entry extends ElementListWidget.Entry<ButtonListWidget.Entry> {

	}

	protected static class ButtonEntry extends Entry {
		List<ClickableWidget> widgets;
		public ButtonEntry(List<ClickableWidget> widgets){
			this.widgets = widgets;
		}

		public static ButtonEntry create(ClickableWidget first, ClickableWidget second) {
			return new ButtonEntry(second == null ? ImmutableList.of(first) : ImmutableList.of(first, second));
		}

		@Override
		public List<? extends Element> children() {
			return widgets;
		}

		@Override
		public void render(long ctx, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			widgets.forEach(c -> {
				c.setY(y);
				c.render(ctx, mouseX, mouseY, tickDelta);
			});
		}

		@Override
		public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			widgets.forEach(c -> {
				c.setY(y);
				c.render(mouseX, mouseY, tickDelta);
			});
		}
	}

	protected static class CategoryEntry extends Entry {
		List<ClickableWidget> widgets;
		public CategoryEntry(List<ClickableWidget> widgets){
			this.widgets = widgets;
		}

		@Override
		public void render(long ctx, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			widgets.forEach(c -> {
				c.setY(y);
				c.render(ctx, mouseX, mouseY, tickDelta);
			});
		}

		@Override
		public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			widgets.forEach(c -> {
				c.setY(y);
				c.render(mouseX, mouseY, tickDelta);
			});
		}

		public static CategoryEntry create(ClickableWidget first, @Nullable ClickableWidget second){
			return new CategoryEntry(second == null ? ImmutableList.of(first) : ImmutableList.of(first, second));
		}

		@Override
		public List<? extends Element> children() {
			return widgets;
		}
	}
}
