package io.github.axolotlclient.AxolotlClientConfig.impl.options;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;

public class ColorOption extends OptionBase<Color> {

	public ColorOption(String name, Color defaultValue) {
		super(name, defaultValue);
	}

	public ColorOption(String name, Color defaultValue, ChangeListener<Color> changeListener) {
		super(name, defaultValue, changeListener);
	}

	@Override
	public String toSerializedValue() {
		return get().toString();
	}

	@Override
	public void fromSerializedValue(String value) {
		this.value = Color.parse(value);
	}

	@Override
	public String getWidgetIdentifier() {
		return "color";
	}


}
