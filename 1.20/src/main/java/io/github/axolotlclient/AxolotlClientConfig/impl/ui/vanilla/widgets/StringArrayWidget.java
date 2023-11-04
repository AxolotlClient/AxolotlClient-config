package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringArrayOption;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.text.Text;

public class StringArrayWidget extends ButtonWidget {

	private final StringArrayOption option;

	public StringArrayWidget(int x, int y, int width, int height, StringArrayOption option) {
		super(x, y, width, height, Text.translatable(option.get()), widget -> {}, DEFAULT_NARRATION);
		this.option = option;
	}

	@Override
	protected void drawWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if (!getMessage().getString().equals(Text.translatable(option.get()).getString())){
			setMessage(Text.translatable(option.get()));
		}
		super.drawWidget(graphics, mouseX, mouseY, delta);
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		String[] values = option.getValues();
		int i = 0;
		while(!values[i].equals(option.get())){
			i+=1;
		}
		i+=1;
		if(i >= values.length){
			i = 0;
		}
		option.set(values[i]);
		setMessage(Text.translatable(option.get()));
	}
}
