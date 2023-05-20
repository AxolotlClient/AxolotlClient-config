package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlClientConfig.options.DoubleOption;
import io.github.axolotlclient.AxolotlClientConfig.options.FloatOption;
import io.github.axolotlclient.AxolotlClientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlClientConfig.options.NumericOption;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.text.DecimalFormat;

public class OptionSliderWidget<T extends NumericOption<N>, N extends Number> extends SliderWidget implements OptionWidget {
	private final DecimalFormat format = new DecimalFormat("##.#");
	private final DecimalFormat intFormat = new DecimalFormat("##");

	private final T option;
	private final N min;
	private final N max;

	public OptionSliderWidget(int x, int y, T option) {
		this(x, y, option, option.getMin(), option.getMax());
	}

	public OptionSliderWidget(int x, int y, int width, int height, T option) {
		this(x, y, width, height, option, option.getMin(), option.getMax());
	}

	public OptionSliderWidget(int x, int y, T option, N min, N max) {
		this(x, y, 150, 20, option, min, max);
	}

	public OptionSliderWidget(int x, int y, int width, int height, T option, N min, N max) {
		super(x, y, width, height, Text.empty(), 0);
		this.option = option;
		this.min = min;
		this.max = max;
		this.value = (option.get().doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue());
		this.setMessage(this.getMessage());
	}

	public void update() {
		value = (option.get().doubleValue() - min.doubleValue()) / max.doubleValue() - min.doubleValue();
		this.setMessage(this.getMessage());
	}

	public Double getSliderValue() {
		format.applyLocalizedPattern("###.##");
		return Double.parseDouble(format.format(this.min.doubleValue() + (this.max.doubleValue() - this.min.doubleValue()) * this.value));
	}

	public int getSliderValueAsInt() {
		intFormat.applyLocalizedPattern("##");
		return Integer.parseInt(intFormat.format(this.min.doubleValue() + (this.max.doubleValue() - this.min.doubleValue()) * this.value));
	}

	public Text getMessage() {
		return Text.of(String.valueOf((option instanceof IntegerOption ? String.valueOf(getSliderValueAsInt()).split("\\.")[0] : this.getSliderValue())));
	}

	public NumericOption<N> getOption() {
		return option;
	}

	@Override
	protected void updateMessage() {

	}

	@Override
	protected void applyValue() {
		if (option instanceof FloatOption) ((FloatOption) option).set(getSliderValue().floatValue());
		else if (option instanceof DoubleOption) ((DoubleOption) option).set(getSliderValue());
		else if (option instanceof IntegerOption) ((IntegerOption) option).set(getSliderValueAsInt());
	}

	@Override
	protected MutableText getNarrationMessage() {
		return Text.literal(option.getTranslatedName()).append(super.getNarrationMessage());
	}

	@Override
	public boolean isHoveredOrFocused() {
		return canHover() && super.isHoveredOrFocused();
	}
}
