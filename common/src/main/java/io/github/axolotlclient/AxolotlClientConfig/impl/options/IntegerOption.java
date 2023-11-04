package io.github.axolotlclient.AxolotlClientConfig.impl.options;

public class IntegerOption extends NumberOption<Integer> {
	public IntegerOption(String name, Integer defaultValue, Integer min, Integer max) {
		super(name, defaultValue, min, max);
	}
	public IntegerOption(String name, Integer defaultValue, ChangeListener<Integer> changeListener, Integer min, Integer max) {
		super(name, defaultValue, changeListener, min, max);
	}

	@Override
	public Integer clamp(Number value) {
		return value.intValue() > getMin() ? (value.intValue() < getMax() ? value.intValue() : getMax()) : getMin();
	}

	@Override
	public void fromSerializedValue(String value) {
		this.value = clamp(Integer.parseInt(value));
	}

	@Override
	public String getWidgetIdentifier() {
		return "integer";
	}
}
