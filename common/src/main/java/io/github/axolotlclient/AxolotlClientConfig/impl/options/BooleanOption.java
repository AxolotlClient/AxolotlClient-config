package io.github.axolotlclient.AxolotlClientConfig.impl.options;

public class BooleanOption extends OptionBase<Boolean> {
	public BooleanOption(String name, Boolean defaultValue) {
		super(name, defaultValue);
	}

	public BooleanOption(String name, Boolean defaultValue, ChangeListener<Boolean> changeListener) {
		super(name, defaultValue, changeListener);
	}

	public void toggle(){
		set(!get());
	}

	@Override
	public String toSerializedValue() {
		return String.valueOf(get());
	}

	@Override
	public void fromSerializedValue(String value) {
		this.value = Boolean.valueOf(value);
	}

	@Override
	public String getWidgetIdentifier() {
		return "boolean";
	}
}
