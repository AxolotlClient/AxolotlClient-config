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
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
	private final Map<String, Style> styles = new HashMap<>();
	private String currentStyle = "vanilla";
	private boolean loaded;

	private ConfigUIImpl() {
	}

	public void preReload() {
		styles.clear();
	}

	public void read(InputStream stream) {
		JsonObject ui = gson.fromJson(new InputStreamReader(stream), JsonObject.class);

		if (ui.has("styles")) {
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
	}

	public void postReload() {
		if (styles.isEmpty()) {
			System.out.println("Falling back to using the classloader to find widget definitions!");
			try {
				Enumeration<URL> list = this.getClass().getClassLoader().getResources("/assets/axolotlclientconfig/config.ui.json");
				while (list.hasMoreElements()) {
					try (InputStream stream = list.nextElement().openStream()) {
						read(stream);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		loaded = true;
		runWhenLoaded.forEach(Runnable::run);
		runWhenLoaded.clear();
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

	public <T> T getScreen(ClassLoader loader, OptionCategory category, T parent) {
		return getScreen(loader, getCurrentStyle(), category, parent);
	}

	@SuppressWarnings("unchecked")
	private <T> T getScreen(ClassLoader loader, Style style, OptionCategory category, T parent) {
		String name = style.getScreen();
		if (name == null || name.trim().isEmpty()) {
			if (style.equals(getDefaultStyle())) {
				throw new IllegalStateException("Something is seriously wrong! The default style's screen is empty! default style: " + getDefaultStyle());
			}
			if (style.getParentStyleName().isPresent()) {
				return getScreen(loader, getStyle(style.getParentStyleName().get()), category, parent);
			} else {
				return getScreen(loader, getDefaultStyle(), category, parent);
			}
		}
		try {
			Class<?> c = Class.forName(name, true, loader);
			Constructor<?> screenConstructor = null;
			Object[] params = {parent, category};
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
				.newInstance(parent, category);
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

	@Override
	public void addWidget(String styleName, String widgetId, String widgetClassName) {
		styles.computeIfAbsent(styleName, name -> new StyleImpl(name, new HashMap<>(), null, null))
			.getWidgets().put(widgetId, widgetClassName);
	}

	@Override
	public void addStyle(Style style) {
		if (!styles.containsKey(style.getName())) {
			styles.put(style.getName(), style);
		} else {
			Style prev = styles.get(style.getName());
			style.getWidgets().putAll(prev.getWidgets());
			String screen = style.getScreen() == null ? prev.getScreen() : style.getScreen();
			styles.put(style.getName(), new StyleImpl(style.getName(), style.getWidgets(), screen,
				style.getParentStyleName().orElse(prev.getParentStyleName().orElse(null))));
		}
	}

	@Override
	public void addScreen(String styleName, String screenClassName) {
		if (styles.containsKey(styleName)){
			Style style = styles.get(styleName);
			styles.put(style.getName(), new StyleImpl(styleName, style.getWidgets(), screenClassName, style.getParentStyleName().orElse(null)));
		} else {
			styles.put(styleName, new StyleImpl(styleName, new HashMap<>(), screenClassName, null));
		}
	}
}
