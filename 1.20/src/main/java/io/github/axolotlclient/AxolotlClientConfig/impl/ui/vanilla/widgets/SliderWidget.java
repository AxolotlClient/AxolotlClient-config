package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import java.text.DecimalFormat;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.NumberOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import net.minecraft.text.Text;

public class SliderWidget<O extends NumberOption<N>, N extends Number> extends net.minecraft.client.gui.widget.SliderWidget implements Updatable {
	private final O option;

	public SliderWidget(int x, int y, int width, int height, O option) {
		super(x, y, width, height, Text.literal(String.valueOf(option.get())), 0);
		this.value = ((option.get().doubleValue() - option.getMin().doubleValue()) / (option.getMax().doubleValue() - option.getMin().doubleValue()));
		this.option = option;
	}

	public void updateMessage() {
		DecimalFormat format = new DecimalFormat("0.##");
		setMessage(Text.literal(format.format(option.get().doubleValue())));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void applyValue() {
		option.set((N) (Double) (option.getMin().doubleValue() +
			(value * (option.getMax().doubleValue()-option.getMin().doubleValue()))));
	}

	public void update() {
		this.value = ((option.get().doubleValue() - option.getMin().doubleValue()) / (option.getMax().doubleValue() - option.getMin().doubleValue()));
		updateMessage();
	}
}
