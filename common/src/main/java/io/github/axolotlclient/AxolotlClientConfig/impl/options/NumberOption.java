package io.github.axolotlclient.AxolotlClientConfig.impl.options;

import lombok.Getter;

public abstract class NumberOption<T extends Number> extends OptionBase<T> {

	@Getter
	T min, max;

	public NumberOption(String name, T defaultValue, T min, T max) {
		super(name, defaultValue);
		this.min = min;
		this.max = max;
	}

	public NumberOption(String name, T defaultValue, ChangeListener<T> changeListener, T min, T max) {
		super(name, defaultValue, changeListener);
		this.min = min;
		this.max = max;
	}

	public abstract T clamp(Number value);

	@Override
	public void set(T newValue){
		super.set(clamp(newValue));
	}

	@Override
	public String toSerializedValue() {
		return String.valueOf(get());
	}
}
