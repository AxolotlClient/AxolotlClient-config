package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.ButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class RoundedButtonListWidget extends ButtonListWidget {
	public RoundedButtonListWidget(OptionCategory category, int screenWidth, int screenHeight, int top, int bottom, int entryHeight) {
		super(category, screenWidth, screenHeight, top, bottom, entryHeight);
		setRenderBackground(false);
	}

	protected void addOptions(Collection<Option<?>> options) {
		options.forEach(o -> addEntry(o, null));
	}

	@Override
	public void addEntry(Option<?> first, @Nullable Option<?> second) {
		addEntry(createOptionEntry(createWidget(width / 2 + WIDGET_ROW_RIGHT, first), first, null, null));
	}

	@Override
	protected Entry createOptionEntry(ClickableWidget widget, Option<?> option, @Nullable ClickableWidget other, @Nullable Option<?> otherOption) {
		return new RoundedOptionEntry(widget, option);
	}

	private class RoundedOptionEntry extends Entry implements DrawingUtil {

		private final Option<?> option;

		public RoundedOptionEntry(ClickableWidget widget, Option<?> option) {
			super(ImmutableList.of(widget,
				new ResetButtonWidget(widget.getX() + widget.getWidth() - 20, 0, 20, widget.getHeight(), option)));
			widget.setWidth(widget.getWidth() - 22);
			this.option = option;
		}

		@Override
		public void render(MatrixStack graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

			drawString(NVGHolder.getContext(), NVGHolder.getFont(),
				Text.translatable(option.getName()).getString(), width / 2f + WIDGET_ROW_LEFT, y, Colors.TURQUOISE);
		}
	}
}
