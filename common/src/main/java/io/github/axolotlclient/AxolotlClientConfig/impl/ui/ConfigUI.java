package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.io.IOException;
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
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.Style;
import lombok.Getter;

public class ConfigUI {

	@Getter
	private static final ConfigUI instance = new ConfigUI();
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final Collection<Runnable> runWhenLoaded = new ArrayList<>();
	@Getter
	private final String uiJsonPath = "axolotlclientconfig:config.ui.json";
	Map<String, Style> styles = new HashMap<>();
	private String currentStyle = "vanilla";
	private boolean loaded;
	private ConfigUI() {
	}

	public void preReload() {
		styles.clear();
	}

	public void read(InputStream stream) {
		JsonObject ui = gson.fromJson(new InputStreamReader(stream), JsonObject.class);

		for (Map.Entry<String, JsonElement> entry : ui.get("styles").getAsJsonObject().entrySet()) {
			JsonObject widgetsObject = entry.getValue().getAsJsonObject().get("widgets").getAsJsonObject();
			String screen = getOrNull(entry.getValue(), "screen");
			Map<String, String> widgets = widgetsObject.entrySet().stream()
				.filter(en -> !en.getValue().getAsString().trim().isEmpty())
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAsString()));
			String parentStyleName = getOrNull(entry.getValue(), "extends");
			if (styles.containsKey(entry.getKey())) {
				Style s = styles.get(entry.getKey());
				s.getWidgets().putAll(widgets);
				if (screen != null && !screen.trim().isEmpty()) {
					styles.put(entry.getKey(), new StyleImpl(entry.getKey(), s.getWidgets(), screen,
						parentStyleName != null ? parentStyleName : s.getParentStyleName().orElse(null)));
				}
				continue;
			}

			styles.put(entry.getKey(), new StyleImpl(entry.getKey(), widgets, screen, parentStyleName));
		}
	}

	public void postReload() {
		if (styles.isEmpty()) {
			try (InputStream stream = this.getClass().getResourceAsStream("/assets/axolotlclientconfig/config.ui.json")) {
				read(stream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		loaded = true;
		runWhenLoaded.forEach(Runnable::run);
	}

	private String getOrNull(JsonElement element, String child) {
		if (element.isJsonObject() && element.getAsJsonObject().has(child)) {
			return element.getAsJsonObject().get(child).getAsString();
		}
		return null;
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

	public Object getScreen(ClassLoader loader, ConfigManager manager, OptionCategory category, Object parent) {
		return getScreen(loader, getCurrentStyle(), manager, category, parent);
	}

	private Object getScreen(ClassLoader loader, Style style, ConfigManager manager, OptionCategory category, Object parent) {
		String name = style.getScreen();
		if (name == null || name.trim().isEmpty()) {
			if (style.equals(getDefaultStyle())) {
				throw new IllegalStateException("Something is seriously wrong! The default style's screen is empty! default style: " + getDefaultStyle());
			}
			if (style.getParentStyleName().isPresent()) {
				return getScreen(loader, getStyle(style.getParentStyleName().get()), manager, category, parent);
			} else {
				return getScreen(loader, getDefaultStyle(), manager, category, parent);
			}
		}
		try {
			return Class.forName(name, true, loader)
				.getConstructor(parent.getClass(), ConfigManager.class, OptionCategory.class)
				.newInstance(parent, manager, category);
		} catch (Throwable e) {
			throw new IllegalStateException("Error while getting screen for " + style.getName(), e);
		}
	}

	public Class<?> getWidget(String identifier, ClassLoader loader) {
		return getWidget(identifier, loader, getCurrentStyle());
	}

	private Class<?> getWidget(String identifier, ClassLoader loader, Style style) {
		String name = style.getWidgets().get(identifier);
		if (name == null || name.trim().isEmpty()) {
			if (style.getParentStyleName().isPresent()) {
				return getWidget(identifier, loader, getStyle(style.getParentStyleName().get()));
			} else {
				return getWidget(identifier, loader, getDefaultStyle());
			}
		}
		try {
			return Class.forName(name, true, loader);
		} catch (Throwable e) {
			throw new IllegalStateException("Error while getting widget for " + style.getName(), e);
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
