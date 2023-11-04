package io.github.axolotlclient.AxolotlClientConfig.impl.options;

import java.util.Base64;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Graphics;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.GraphicsImpl;

public class GraphicsOption extends OptionBase<Graphics> {
	public GraphicsOption(String name, int width, int height) {
		super(name, new GraphicsImpl(width, height));
	}

	public GraphicsOption(String name, int width, int height, ChangeListener<Graphics> changeListener) {
		super(name, new GraphicsImpl(width, height), changeListener);
	}

	public GraphicsOption(String name, int[][] data) {
		super(name, new GraphicsImpl(data));
	}

	public GraphicsOption(String name, int[][] data, ChangeListener<Graphics> changeListener) {
		super(name, new GraphicsImpl(data), changeListener);
	}

	@Override
	public String toSerializedValue() {
		return Base64.getEncoder().encodeToString(get().getPixelData());
	}

	@Override
	public void fromSerializedValue(String value) {
		byte[] data = Base64.getDecoder().decode(value);
		this.value.setPixelData(data);
	}

	@Override
	public String getWidgetIdentifier() {
		return "graphics";
	}
}
