package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.ButtonListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public class VanillaButtonListWidget extends ButtonListWidget {
	public VanillaButtonListWidget(OptionCategory category, int screenWidth, int screenHeight, int top, int bottom, int entryHeight) {
		super(category, screenWidth, screenHeight, top, bottom, entryHeight);
		setRenderBackground(MinecraftClient.getInstance().world == null);
		setRenderHeader(MinecraftClient.getInstance().world == null, headerHeight);
		setRenderHorizontalShadows(MinecraftClient.getInstance().world == null);
	}

	protected void addOptions(Collection<Option<?>> options) {
		options.forEach(o -> addEntry(o, null));
	}

	@Override
	public void addEntry(Option<?> first, @Nullable Option<?> second) {
		addEntry(createOptionEntry(createWidget(width / 2 + WIDGET_ROW_RIGHT, first), first, null, null));
	}

	@Override
	protected Entry createOptionEntry(AbstractButtonWidget widget, Option<?> option, @Nullable AbstractButtonWidget other, @Nullable Option<?> otherOption) {
		return new VanillaOptionEntry(widget, option);
	}

	private class VanillaOptionEntry extends Entry {

		private final Option<?> option;

		public VanillaOptionEntry(AbstractButtonWidget widget, Option<?> option) {
			super(ImmutableList.of(widget,
				new ResetButtonWidget(widget.x + widget.getWidth() - 40, 0, 40, widget.getHeight(), option)));
			widget.setWidth(widget.getWidth() - 42);
			this.option = option;
		}

		@Override
		public void render(MatrixStack graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

			client.textRenderer.drawWithShadow(graphics, new TranslatableText(option.getName()), width / 2f + WIDGET_ROW_LEFT, y, -1);
		}
	}
}
