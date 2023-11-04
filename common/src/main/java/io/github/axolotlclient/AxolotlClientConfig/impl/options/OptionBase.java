package io.github.axolotlclient.AxolotlClientConfig.impl.options;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import lombok.Getter;

public abstract class OptionBase<T> implements Option<T> {

	@Getter
	private final String name;
	protected T value;
	@Getter
	private final T defaultValue;

	private final ChangeListener<T> changeListener;

	public OptionBase(String name, T defaultValue){
		this(name, defaultValue, val -> {});
	}

	public OptionBase(String name, T defaultValue, ChangeListener<T> changeListener) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.changeListener = changeListener;
	}

	public T get() {
		return value;
	}

	@Override
	public T getDefault() {
		return defaultValue;
	}

	public void set(T value) {
		this.value = value;
		changeListener.onChange(value);
	}

	public interface ChangeListener<T> {
		void onChange(T newValue);
	}
}
