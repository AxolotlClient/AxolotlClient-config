package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import com.google.common.util.concurrent.AtomicDouble;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Screen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;

public class ResetButtonWidget extends VanillaButtonWidget {

	private final Option<?> option;

	public ResetButtonWidget(int x, int y, int width, int height, Option<?> option) {
		super(x, y, width, height, I18n.translate("action.reset"), widget -> {
			option.setDefault();
			Window window = new Window(MinecraftClient.getInstance());
			int i = (int) window.getScaledWidth();
			int j = (int) window.getScaledHeight();
			Screen current = (Screen) MinecraftClient.getInstance().currentScreen;
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
	public void render(int mouseX, int mouseY, float delta) {
		this.active = !option.getDefault().equals(option.get());
		super.render(mouseX, mouseY, delta);
	}
}