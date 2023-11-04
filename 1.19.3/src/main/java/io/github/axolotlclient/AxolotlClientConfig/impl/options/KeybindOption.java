package io.github.axolotlclient.AxolotlClientConfig.impl.options;

import com.mojang.blaze3d.platform.InputUtil;
import net.minecraft.client.option.KeyBind;

public class KeybindOption extends OptionBase<KeyBind> {

	public KeybindOption(String name, KeyBind defaultValue) {
		super(name, defaultValue);
	}

	public KeybindOption(String name, KeyBind defaultValue, ChangeListener<KeyBind> changeListener) {
		super(name, defaultValue, changeListener);
	}

	@Override
	public String toSerializedValue() {
		return get().getTranslationKey();
	}

	@Override
	public void fromSerializedValue(String value) {
		this.value.setBoundKey(InputUtil.fromTranslationKey(value));
	}
}
