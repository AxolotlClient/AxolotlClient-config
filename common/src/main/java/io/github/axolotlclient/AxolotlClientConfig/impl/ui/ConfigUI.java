package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.Style;
import lombok.Getter;

public class ConfigUI {

	Map<String, Style> styles = new HashMap<>();

	private String currentStyle = "vanilla";

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private ConfigUI() {
	}

	@Getter
	private static final ConfigUI instance = new ConfigUI();

	private final Collection<Runnable> runWhenLoaded = new ArrayList<>();
	private boolean loaded;
	@Getter
	private final String uiJsonPath = "axolotlclientconfig:config.ui.json";

	public void read(InputStream stream) {
		JsonObject ui = gson.fromJson(new InputStreamReader(stream), JsonObject.class);

		for (Map.Entry<String, JsonElement> entry : ui.get("styles").getAsJsonObject().entrySet()) {
			if (styles.containsKey(entry.getKey())) {
				continue;
			}
			JsonObject widgetsObject = entry.getValue().getAsJsonObject().get("widgets").getAsJsonObject();
			String screen = entry.getValue().getAsJsonObject().get("screen").getAsString();
			Map<String, String> widgets = widgetsObject.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAsString()));
			JsonElement parentStyle = entry.getValue().getAsJsonObject().get("extends");
			String parentStyleName = parentStyle != null ? parentStyle.getAsString() : null;
			styles.put(entry.getKey(), new StyleImpl(entry.getKey(), widgets, screen, parentStyleName));
		}

		loaded = true;
		runWhenLoaded.forEach(Runnable::run);
	}

	public Style getCurrentStyle() {
		return styles.getOrDefault(currentStyle, getDefaultStyle());
	}

	private Style getStyle(String name) {
		return styles.get(name);
	}

	public Style getDefaultStyle() {
		return styles.get("vanilla");
	}

	public void setStyle(String name) {
		if (styles.containsKey(name)) {
			currentStyle = name;
		}
	}

	public Collection<String> getStyleNames() {
		return styles.keySet();
	}

	public Class<?> getWidget(String identifier, ClassLoader loader) {
		return getWidget(identifier, loader, getCurrentStyle());
	}

	private Class<?> getWidget(String identifier, ClassLoader loader, Style style) {
		String name = style.getWidgets().get(identifier);
		if (name == null || name.isEmpty() || name.trim().isEmpty()) {
			if (style.getParentStyleName().isPresent()) {
				name = getStyle(style.getParentStyleName().get()).getWidgets().get(identifier);
			} else {
				name = getDefaultStyle().getWidgets().get(identifier);
			}

			if (name == null || name.isEmpty() || name.trim().isEmpty()) {
				throw new IllegalStateException("Error while getting widget for " + name);
			}
		}
		try {
			return Class.forName(name, true, loader);
		} catch (Throwable e) {
			throw new IllegalStateException("Error while getting widget for " + name, e);
		}
	}

	public void runWhenLoaded(Runnable runnable) {
		if (loaded) {
			runnable.run();
		} else {
			runWhenLoaded.add(runnable);
		}
	}
}
