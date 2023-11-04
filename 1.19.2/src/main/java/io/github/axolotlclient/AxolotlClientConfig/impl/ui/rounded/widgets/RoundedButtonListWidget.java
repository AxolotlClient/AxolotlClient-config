package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ClickableWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen.RoundedConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

public class RoundedButtonListWidget extends ButtonListWidget {
	public RoundedButtonListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		super(client, width, height, top, bottom, itemHeight);
		setRenderBackground(false);
		setRenderHeader(false, 0);
	}

	@Override
	protected void addOptions(Collection<Option<?>> options) {
		options.forEach(o -> addEntry(o, null));
	}

	@Override
	public void addEntry(Option<?> first, @Nullable Option<?> second){
		addEntry(createOptionEntry(createWidget(width/2 + WIDGET_ROW_RIGHT, first), first, null, null));
	}

	@Override
	protected Entry createOptionEntry(ClickableWidget widget, Option<?> option, @Nullable ClickableWidget other, @Nullable Option<?> otherOption) {
		return new RoundedButtonEntry(widget, option);
	}

	@Override
	protected void renderScrollbar(long ctx, int x, int y, int width, int height) {
		fillRoundedRect(ctx, x, top, width, bottom - top, Colors.GRAY, width/2);
		fillRoundedRect(ctx, x, y, width, height, Colors.TURQUOISE, width/2);
	}

	class RoundedButtonEntry extends ButtonEntry {

		private final Option<?> option;

		public RoundedButtonEntry(ClickableWidget widget, Option<?> option) {
			super(ImmutableList.of(widget,
				new ResetButtonWidget(widget.getX() + widget.getWidth()-20, 0, 20, widget.getHeight(), option)));
			widget.setWidth(widget.getWidth()-22);
			this.option = option;
		}

		@Override
		public void render(long ctx, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			super.render(ctx, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

			float nameY = y+entryHeight/2f-RoundedConfigScreen.font.getLineHeight()/2;
			drawString(ctx, RoundedConfigScreen.font, I18n.translate(option.getName()), width/2f+WIDGET_ROW_LEFT, nameY, Colors.TURQUOISE);
		}
	}
}
