package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.EnumOption;
import net.minecraft.text.Text;

public class EnumWidget<T extends Enum<T>> extends RoundedButtonWidget {
	private final EnumOption<T> option;

	public EnumWidget(int x, int y, int width, int height, EnumOption<T> option) {
		super(x, y, width, height, Text.translatable(String.valueOf(option.get())), widget -> {});
		this.option = option;
	}

	@Override
	public void onPress() {
		T[] values = option.getClazz().getEnumConstants();
		int i = 0;
		while(!values[i].equals(option.get())){
			i+=1;
		}
		i+=1;
		if(i >= values.length){
			i = 0;
		}
		option.set(values[i]);
		setMessage(Text.translatable(String.valueOf(option.get())));
	}
}
