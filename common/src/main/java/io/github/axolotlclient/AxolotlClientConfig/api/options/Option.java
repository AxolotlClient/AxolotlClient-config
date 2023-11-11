package io.github.axolotlclient.AxolotlClientConfig.api.options;

public interface Option<T> extends WidgetIdentifieable {

	String getName();

	T get();

	T getDefault();

	void set(T newValue);

	void setDefault();

	String toSerializedValue();

	void fromSerializedValue(String value);
}
