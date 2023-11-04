package io.github.axolotlclient.AxolotlClientConfig.impl.options;

public class StringOption extends OptionBase<String> {

	public StringOption(String name, String defaultValue) {
		super(name, defaultValue);
	}

	public StringOption(String name, String defaultValue, ChangeListener<String> changeListener) {
		super(name, defaultValue, changeListener);
	}

	@Override
	public String toSerializedValue() {
		return get();
	}

	@Override
	public void fromSerializedValue(String value) {
		this.value = value;
	}

	@Override
	public String getWidgetIdentifier() {
		return "string";
	}
}
