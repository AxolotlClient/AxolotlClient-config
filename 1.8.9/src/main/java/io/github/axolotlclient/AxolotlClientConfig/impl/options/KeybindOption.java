package io.github.axolotlclient.AxolotlClientConfig.impl.options;

import net.minecraft.client.option.KeyBinding;

public class KeybindOption extends OptionBase<KeyBinding> {

	public KeybindOption(String name, KeyBinding defaultValue) {
		super(name, defaultValue);
	}

	public KeybindOption(String name, KeyBinding defaultValue, ChangeListener<KeyBinding> changeListener) {
		super(name, defaultValue, changeListener);
	}

	@Override
	public String toSerializedValue() {
		return get().getTranslationKey();
	}

	@Override
	public void fromSerializedValue(String value) {
		this.value.setCode(Integer.parseInt(value));
	}

	@Override
	public String getWidgetIdentifier() {
		return "keybind";
	}
}
