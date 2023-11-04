package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.nanovg.NanoVG;

public class StringWidget extends TextFieldWidget implements DrawingUtil {
	private final StringOption option;

	private final Color highlightColor = Colors.DARK_YELLOW.withAlpha(100);

	public StringWidget(int x, int y, int width, int height, StringOption option) {
		super(MinecraftClient.getInstance().textRenderer, x, y, width, height, Text.translatable(option.getName()));

		write(option.get());

		this.option = option;
		setChangedListener(option::set);
	}

	@Override
	public void drawWidget(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		if (!option.get().equals(getText())) {
			setText(option.get());
		}
		super.drawWidget(graphics, mouseX, mouseY, delta);
	}

	protected void drawSelectionHighlight(long ctx, float x1, float y1, float x2, float y2) {
		if (x1 < x2) {
			float i = x1;
			x1 = x2;
			x2 = i;
		}

		if (y1 < y2) {
			float i = y1;
			y1 = y2;
			y2 = i;
		}

		if (x2 > this.getX() + this.getWidth()) {
			x2 = this.getX() + this.getWidth();
		}

		if (x1 > this.getX() + this.getWidth()) {
			x1 = this.getX() + this.getWidth();
		}

		NanoVG.nvgBeginPath(ctx);
		NanoVG.nvgRect(ctx, x1, y1, x2 - x1, y2 - y1);
		NanoVG.nvgFillColor(ctx, highlightColor.toNVG());
		NanoVG.nvgFill(ctx);
	}
}
