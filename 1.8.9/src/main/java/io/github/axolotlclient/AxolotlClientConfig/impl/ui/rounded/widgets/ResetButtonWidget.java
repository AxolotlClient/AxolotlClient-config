package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import com.google.common.util.concurrent.AtomicDouble;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Screen;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.lwjgl.nanovg.NanoVG;

public class ResetButtonWidget extends RoundedButtonWidget {

	private final Option<?> option;

	public ResetButtonWidget(int x, int y, int width, int height, Option<?> option) {
		super(x, y, width, height, "", widget -> {
			option.setDefault();
			Window window = new Window(MinecraftClient.getInstance());
			double i = window.getScaledWidth();
			double j = window.getScaledHeight();
			Screen current = (Screen) MinecraftClient.getInstance().currentScreen;
			if (current != null) {
				AtomicDouble scroll = new AtomicDouble();
				current.children().stream()
					.filter(e -> e instanceof RoundedButtonListWidget)
					.map(e -> (RoundedButtonListWidget) e).findFirst().ifPresent(list -> {
						scroll.set(list.getScrollAmount());
					});
				current.init(MinecraftClient.getInstance(), (int) i, (int) j);
				current.children().stream()
					.filter(e -> e instanceof RoundedButtonListWidget)
					.map(e -> (RoundedButtonListWidget) e).findFirst().ifPresent(list -> {
						list.setScrollAmount(scroll.get());
					});
			}
		});
		this.option = option;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.active = !option.getDefault().equals(option.get());
		super.render(mouseX, mouseY, delta);
	}

	@Override
	public void drawWidget(int mouseX, int mouseY, float delta) {
		long ctx = NVGHolder.getContext();
		this.active = !option.getDefault().equals(option.get());
		super.drawWidget(mouseX, mouseY, delta);

		Color color = !active ? Colors.GRAY : Colors.WHITE;
		if (active && isHovered()) {
			color = Colors.DARK_YELLOW;
		}

		NanoVG.nvgLineCap(ctx, NanoVG.NVG_ROUND);
		NanoVG.nvgLineJoin(ctx, NanoVG.NVG_ROUND);
		outlineCircle(ctx, getX() + getWidth() / 2, getY() + getHeight() / 2, color, getWidth() / 4, 2, 0, 270);
		NanoVG.nvgBeginPath(ctx);
		NanoVG.nvgMoveTo(ctx, getX() + getWidth() / 2f, getY() + getHeight() / 4f);
		NanoVG.nvgLineTo(ctx, getX() + getWidth() / 2f - 3, getY() + getHeight() / 4f);
		NanoVG.nvgMoveTo(ctx, getX() + getWidth() / 2f, getY() + getHeight() / 2f - 2);
		NanoVG.nvgLineTo(ctx, getX() + getWidth() / 2f - 3, getY() + getHeight() / 4f);
		NanoVG.nvgLineTo(ctx, getX() + getWidth() / 2f, getY() + 2);
		NanoVG.nvgStrokeColor(ctx, color.toNVG());
		NanoVG.nvgStroke(ctx);

	}

	@Override
	protected Color getWidgetColor() {
		return Colors.TURQUOISE;
	}
}
