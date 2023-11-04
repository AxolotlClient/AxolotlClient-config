package io.github.axolotlclient.AxolotlClientConfig.impl.options;

public class FloatOption extends NumberOption<Float> {
	public FloatOption(String name, Float defaultValue, Float min, Float max) {
		super(name, defaultValue, min, max);
	}

	public FloatOption(String name, Float defaultValue, ChangeListener<Float> changeListener, Float min, Float max) {
		super(name, defaultValue, changeListener, min, max);
	}

	@Override
	public Float clamp(Number value) {
		return value.floatValue() > getMin() ? (value.floatValue() < getMax() ? value.floatValue() : getMax()) : getMin();
	}

	@Override
	public void fromSerializedValue(String value) {
		this.value = clamp(Float.parseFloat(value));
	}

	@Override
	public String getWidgetIdentifier() {
		return "float";
	}
}
