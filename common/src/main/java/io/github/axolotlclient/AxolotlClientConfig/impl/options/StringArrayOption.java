package io.github.axolotlclient.AxolotlClientConfig.impl.options;

import java.util.Arrays;

import lombok.Getter;

public class StringArrayOption extends OptionBase<String> {

	@Getter
	private final String[] values;

	public StringArrayOption(String name, String... values){
		super(name, values[0]);
		this.values = values;
	}

	public StringArrayOption(String name, String[] values, String defaultValue) {
		super(name, defaultValue);
		this.values = values;
	}

	public StringArrayOption(String name, String[] values, String defaultValue, ChangeListener<String> changeListener) {
		super(name, defaultValue, changeListener);
		this.values = values;
	}

	@Override
	public String toSerializedValue() {
		return get();
	}

	@Override
	public void fromSerializedValue(String value) {
		if (Arrays.asList(values).contains(value)) {
			this.value = value;
		} else {
			setDefault();
		}
	}

	@Override
	public String getWidgetIdentifier() {
		return "string[]";
	}

	@Override
	public void set(String value) {
		if (Arrays.asList(values).contains(value)) {
			super.set(value);
		}
	}
}
