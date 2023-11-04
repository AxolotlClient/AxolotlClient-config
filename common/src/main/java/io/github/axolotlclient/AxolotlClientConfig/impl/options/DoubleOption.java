package io.github.axolotlclient.AxolotlClientConfig.impl.options;

public class DoubleOption extends NumberOption<Double> {
	public DoubleOption(String name, Double defaultValue, Double min, Double max) {
		super(name, defaultValue, min, max);
	}

	public DoubleOption(String name, Double defaultValue, ChangeListener<Double> changeListener, Double min, Double max) {
		super(name, defaultValue, changeListener, min, max);
	}

	@Override
	public Double clamp(Number value) {
		return value.doubleValue() > getMin() ? (value.doubleValue() < getMax() ? value.doubleValue() : getMax()) : getMin();
	}

	@Override
	public void fromSerializedValue(String value) {
		this.value = clamp(Double.parseDouble(value));
	}

	@Override
	public String getWidgetIdentifier() {
		return "double";
	}
}
