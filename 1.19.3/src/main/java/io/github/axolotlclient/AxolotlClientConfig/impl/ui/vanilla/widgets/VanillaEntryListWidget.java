package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.EntryListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class VanillaEntryListWidget extends EntryListWidget {
	public VanillaEntryListWidget(ConfigManager manager, OptionCategory category, int screenWidth, int screenHeight, int top, int bottom, int entryHeight) {
		super(manager, category, screenWidth, screenHeight, top, bottom, entryHeight);
		setRenderBackground(MinecraftClient.getInstance().world == null);
		setRenderHeader(MinecraftClient.getInstance().world == null, headerHeight);
		setRenderHorizontalShadows(MinecraftClient.getInstance().world == null);
	}

	@Override
	protected void addOptions(ConfigManager manager, Collection<Option<?>> options) {
		options.stream()
			.filter(o -> !manager.getSuppressedNames().contains(o.getName()))
			.forEach(o -> addEntry(o, null));
	}

	@Override
	public void addEntry(Option<?> first, @Nullable Option<?> second) {
		addEntry(createOptionEntry(createWidget(width / 2 + WIDGET_ROW_RIGHT, first), first, null, null));
	}

	@Override
	protected Entry createOptionEntry(ClickableWidget widget, Option<?> option, @Nullable ClickableWidget other, @Nullable Option<?> otherOption) {
		return new VanillaOptionEntry(widget, option);
	}

	private class VanillaOptionEntry extends Entry {

		private final Option<?> option;

		public VanillaOptionEntry(ClickableWidget widget, Option<?> option) {
			super(ImmutableList.of(widget,
				new ResetButtonWidget(widget.getX() + widget.getWidth() - 40, 0, 40, widget.getHeight(), option)));
			widget.setWidth(widget.getWidth() - 42);
			this.option = option;
		}

		@Override
		public void render(MatrixStack graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

			client.textRenderer.drawWithShadow(graphics, Text.translatable(option.getName()), width / 2f + WIDGET_ROW_LEFT, y, -1);
		}
	}
}
