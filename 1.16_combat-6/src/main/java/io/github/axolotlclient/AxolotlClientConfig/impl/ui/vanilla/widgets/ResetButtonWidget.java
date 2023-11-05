package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import com.google.common.util.concurrent.AtomicDouble;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class ResetButtonWidget extends VanillaButtonWidget {

	private final Option<?> option;

	public ResetButtonWidget(int x, int y, int width, int height, Option<?> option) {
		super(x, y, width, height, new TranslatableText("action.reset"), widget -> {
			option.setDefault();
			Window window = MinecraftClient.getInstance().getWindow();
			int i = window.getScaledWidth();
			int j = window.getScaledHeight();
			Screen current = MinecraftClient.getInstance().currentScreen;
			if (current != null) {
				AtomicDouble scroll = new AtomicDouble();
				current.children().stream()
					.filter(e -> e instanceof VanillaButtonListWidget)
					.map(e -> (VanillaButtonListWidget) e).findFirst().ifPresent(list -> {
						scroll.set(list.getScrollAmount());
					});
				current.init(MinecraftClient.getInstance(), i, j);
				current.children().stream()
					.filter(e -> e instanceof VanillaButtonListWidget)
					.map(e -> (VanillaButtonListWidget) e).findFirst().ifPresent(list -> {
						list.setScrollAmount(scroll.get());
					});
			}
		});
		this.option = option;
	}

	@Override
	public void render(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		this.active = !option.getDefault().equals(option.get());
		super.render(graphics, mouseX, mouseY, delta);
	}
}
