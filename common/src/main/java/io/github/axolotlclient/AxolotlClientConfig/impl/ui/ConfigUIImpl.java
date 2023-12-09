/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
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
import io.github.axolotlclient.AxolotlClientConfig.api.options.WidgetIdentifieable;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.Style;
import lombok.Getter;

public class ConfigUIImpl implements ConfigUI {

	@Getter
	private static final ConfigUIImpl instance = new ConfigUIImpl();
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final Collection<Runnable> runWhenLoaded = new ArrayList<>();
	@Getter
	private final String uiJsonPath = "axolotlclientconfig:config.ui.json";
	Map<String, Style> styles = new HashMap<>();
	private String currentStyle = "vanilla";
	private boolean loaded;

	private ConfigUIImpl() {
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

	public <T> T getScreen(ClassLoader loader, ConfigManager manager, OptionCategory category, T parent) {
		return getScreen(loader, getCurrentStyle(), manager, category, parent);
	}

	@SuppressWarnings("unchecked")
	private   <T> T getScreen(ClassLoader loader, Style style, ConfigManager manager, OptionCategory category, T parent) {
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
			Class<?> c = Class.forName(name, true, loader);
			Constructor<?> screenConstructor = null;
			Object[] params = {parent, manager, category};
			for (Constructor<?> con : c.getDeclaredConstructors()) {
				if (con.getParameterTypes().length != params.length) {
					continue;
				}
				boolean matching = true;
				for (int i = 0; i < params.length; i++) {
					if (params[i] != null) {
						if (!con.getParameterTypes()[i].isAssignableFrom(params[i].getClass())) {
							matching = false;
						}
					}
				}
				if (matching) {
					screenConstructor = con;
				}
			}
			if (screenConstructor == null) {
				throw new IllegalStateException("Constructor couldn't be found!");
			}
			return (T) screenConstructor
				.newInstance(parent, manager, category);
		} catch (Throwable e) {
			throw new IllegalStateException("Error while getting screen for " + style.getName(), e);
		}
	}

	public Object getWidget(int x, int y, int width, int height, WidgetIdentifieable id, ClassLoader loader) {
		return getWidget(x, y, width, height, id, loader, getCurrentStyle());
	}

	private Object getWidget(int x, int y, int width, int height, WidgetIdentifieable id, ClassLoader loader, Style style) {
		String name = style.getWidgets().get(id.getWidgetIdentifier());
		if (name == null || name.trim().isEmpty()) {
			if (style.getParentStyleName().isPresent()) {
				return getWidget(x, y, width, height, id, loader, getStyle(style.getParentStyleName().get()));
			} else {
				return getWidget(x, y, width, height, id, loader, getDefaultStyle());
			}
		}
		try {
			return Class.forName(name, true, loader)
				.getDeclaredConstructor(int.class, int.class, int.class, int.class, id.getClass())
				.newInstance(x, y, width, height, id);
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
