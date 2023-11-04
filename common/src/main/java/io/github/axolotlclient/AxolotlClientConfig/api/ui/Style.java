package io.github.axolotlclient.AxolotlClientConfig.api.ui;

import java.util.Map;
import java.util.Optional;

public interface Style {

	String getName();
	String getScreen();
	Map<String, String> getWidgets();
	Optional<String> getParentStyleName();

}
