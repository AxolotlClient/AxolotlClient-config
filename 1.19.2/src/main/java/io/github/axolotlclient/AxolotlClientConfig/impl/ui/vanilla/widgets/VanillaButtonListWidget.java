package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ClickableWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.MatrixStackProvider;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

public class VanillaButtonListWidget extends ButtonListWidget {
	public VanillaButtonListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		super(client, width, height, top, bottom, itemHeight);

		if (client.world != null){
			setRenderBackground(false);
		}
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
		return new VanillaButtonEntry(widget, option);
	}

	@Override
	protected void enableScissor(long ctx) {
		DrawUtil.pushScissor(0, top, width, bottom-top);
	}

	@Override
	protected void disableScissor(long ctx) {
		DrawUtil.popScissor();
	}

	class VanillaButtonEntry extends ButtonEntry {

		private final Option<?> option;

		public VanillaButtonEntry(ClickableWidget widget, Option<?> option) {
			super(ImmutableList.of(widget,
				new ResetButtonWidget(widget.getX() + widget.getWidth()-40, 0, 40, widget.getHeight(), option)));
			widget.setWidth(widget.getWidth()-42);
			this.option = option;
		}

		@Override
		public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

			MinecraftClient.getInstance().textRenderer.draw(MatrixStackProvider.getInstance().getStack(),
				I18n.translate(option.getName()), width/2f+WIDGET_ROW_LEFT, y, -1);
		}
	}
}
