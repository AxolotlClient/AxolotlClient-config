package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.lwjgl.nanovg.NanoVG;

public class ResetButtonWidget extends RoundedButtonWidget{

	private final Option<?> option;
	public ResetButtonWidget(int x, int y, int width, int height, Option<?> option) {
		super(x, y, width, height, "", widget -> {
			option.setDefault();
			Window window = MinecraftClient.getInstance().getWindow();
			int i = window.getWidth();
			int j = window.getHeight();
			MinecraftClient.getInstance().currentScreen.init(MinecraftClient.getInstance(), i, j);
		});
		this.option = option;
	}

	@Override
	protected void drawWidget(long ctx, int mouseX, int mouseY, float delta) {
		this.active = !option.getDefault().equals(option.get());
		super.drawWidget(ctx, mouseX, mouseY, delta);

		Color color = !active ? Colors.GRAY : Colors.WHITE;
		if (active && isHovered()) {
			color = Colors.DARK_YELLOW;
		}

		NanoVG.nvgLineCap(ctx, NanoVG.NVG_ROUND);
		NanoVG.nvgLineJoin(ctx, NanoVG.NVG_ROUND);
		outlineCircle(ctx, getX()+getWidth()/2, getY()+getHeight()/2, color, getWidth()/4, 2, 0, 270);
		NanoVG.nvgBeginPath(ctx);
		NanoVG.nvgMoveTo(ctx, getX()+getWidth()/2f, getY()+getHeight()/4f);
		NanoVG.nvgLineTo(ctx, getX()+getWidth()/2f-3, getY()+getHeight()/4f);
		NanoVG.nvgMoveTo(ctx, getX()+getWidth()/2f, getY()+getHeight()/2f-2);
		NanoVG.nvgLineTo(ctx, getX()+getWidth()/2f-3, getY()+getHeight()/4f);
		NanoVG.nvgLineTo(ctx, getX()+getWidth()/2f, getY()+2);
		NanoVG.nvgStrokeColor(ctx, color.toNVG());
		NanoVG.nvgStroke(ctx);

	}

	@Override
	protected Color getWidgetColor() {
		return Colors.TURQUOISE;
	}
}
