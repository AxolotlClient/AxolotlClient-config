package io.github.axolotlclient.AxolotlClientConfig.impl.options;

import lombok.Getter;

public class EnumOption<T extends Enum<T>> extends OptionBase<T> {

	@Getter
	private final Class<T> clazz;

	public EnumOption(String name, Class<T> e) {
		this(name, e, e.getEnumConstants()[0]);
	}

	public EnumOption(String name, Class<T> e, T defaultValue) {
		super(name, defaultValue);
		clazz = e;
	}

	public EnumOption(String name, Class<T> e, T defaultValue, ChangeListener<T> changeListener) {
		super(name, defaultValue, changeListener);
		this.clazz = e;
	}

	@Override
	public String toSerializedValue() {
		return get().toString();
	}

	@Override
	public void fromSerializedValue(String value) {
		for (T val : clazz.getEnumConstants()) {
			if (val.toString().equals(value)) {
				this.value = val;
			}
		}
	}

	@Override
	public String getWidgetIdentifier() {
		return "enum";
	}


}
