package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.util.Map;
import java.util.Optional;

import io.github.axolotlclient.AxolotlClientConfig.api.ui.Style;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class StyleImpl implements Style {

	private final String name;
	private final Map<String, String> widgets;
	private final String screen;
	private final String parentStyle;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getScreen() {
		return screen;
	}

	@Override
	public Map<String, String> getWidgets() {
		return widgets;
	}

	@Override
	public Optional<String> getParentStyleName() {
		return Optional.ofNullable(parentStyle);
	}
}
