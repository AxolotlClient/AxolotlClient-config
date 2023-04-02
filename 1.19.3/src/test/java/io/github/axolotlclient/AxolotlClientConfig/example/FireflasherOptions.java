package io.github.axolotlclient.AxolotlClientConfig.example;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Comparators;
import com.google.gson.*;
import com.mojang.blaze3d.glfw.Window;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.common.ConfigHolder;
import io.github.axolotlclient.AxolotlClientConfig.common.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.options.GenericOption;
import io.github.axolotlclient.AxolotlClientConfig.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.options.StringOption;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.OptionWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

public class FireflasherOptions {

    private static OptionCategory CATEGORY;

    private static final String PLACEHOLDER = "DELETE";
    private static String MODID;

    public static OptionCategory getCategory() {
        return CATEGORY;
    }

    public void register(String modid) {
        MODID = modid;
        // The name of your category, also used for the title. This is the translation key if you use localization.
        CATEGORY = new OptionCategory("Fireflasher's Options");
        AxolotlClientConfigManager.getInstance().registerConfig(modid, new ConfigHolder() {
            @Override
            public List<io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory> getCategories() {
                return Collections.singletonList(init());
            }
        }, new FireflasherConfigManager(modid));
    }

    private OptionCategory init() {
        OptionCategory category = CATEGORY;

        if (category.getOptions().stream().noneMatch(o -> o instanceof StringOption))
            // the first parameter is always the name, and these can always be translation keys.
            category.add(new StringOption("Add Entries", ""){
                @Override
                public ClickableWidget getWidget(int x, int y, int width, int height) {
                    return new FireflasherWidget(x, y, width, height, Text.empty(), category, this);
                }
            });
        return category;
    }

    private Option<?> getSpecialOption(OptionCategory category, String name) {
        return new GenericOption(name, "Delete", (mouseX, mouseY) -> {}){
            @Override
            public JsonElement getJson() {
                return new JsonPrimitive(PLACEHOLDER);
            }

            @Override
            public OnClick get() {
                return (mouseX, mouseY) -> removeOption(category, this);
            }
        };
    }

    private void removeOption(OptionCategory category, Option<?> o){
        category.getOptions().remove(o);
        Window window = MinecraftClient.getInstance().getWindow();
        MinecraftClient.getInstance().currentScreen.init(MinecraftClient.getInstance(), window.getScaledWidth(), window.getScaledHeight());
        AxolotlClientConfigManager.getInstance().save(MODID);
        System.out.println(Arrays.toString(category.getOptions().stream().map(io.github.axolotlclient.AxolotlClientConfig.common.options.Option::getName).toArray()));
    }

    private class FireflasherWidget extends OptionWidget implements ParentElement {

        private final List<Element> children = new ArrayList<>();
        private final ButtonWidget add;
        private final TextFieldWidget widget;
        private boolean dragging;
        private Element focused;

        public FireflasherWidget(int x, int y, int width, int height, Text text, OptionCategory category, StringOption option) {
            super(x, y, width, height, Text.empty(), a -> {
            }); // no action needed because it's a ParentElement as well
            widget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x, y, width - 20, height, text);
            children.add(widget);
            add = ButtonWidget.builder(Text.translatable("+"), buttonWidget -> {
                category.getOptions().add(0, getSpecialOption(category, widget.getText()));
                Window window = MinecraftClient.getInstance().getWindow();
                MinecraftClient.getInstance().currentScreen.init(MinecraftClient.getInstance(), window.getScaledWidth(), window.getScaledHeight());
                AxolotlClientConfigManager.getInstance().saveCurrentConfig();
            }).positionAndSize(x + width - 20, y, 20, height).build();
            children.add(add);
            ClientTickEvents.END.register(client -> { // Because ticking them otherwise doesn't work...
                if (widget.isFocused()) {
                    widget.tick();
                }
            });
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            widget.setX(getX());
            widget.setY(getY());
            widget.render(matrices, mouseX, mouseY, delta);
            add.setY(getY());
            add.setX(getX() + getWidth() - 20);
            add.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return ParentElement.super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return ParentElement.super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public List<? extends Element> children() {
            return children;
        }

        @Override
        public boolean isDragging() {
            return dragging;
        }

        @Override
        public void setDragging(boolean dragging) {
            this.dragging = dragging;
        }

        @Nullable
        @Override
        public Element getFocused() {
            return focused;
        }

        @Override
        public void setFocused(@Nullable Element focused) {
            this.focused = focused;
        }

        @Override
        public void unfocus() {
            focused = null;
            widget.setTextFieldFocused(false);
        }
    }

    public class FireflasherConfigManager implements ConfigManager {
        private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        private final String modid;
        private final Path confPath;
        private final List<io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory> modConfig;

        public FireflasherConfigManager(String modid) {
            this(modid, modid + ".json");

        }

        public FireflasherConfigManager(String modid, String configFileName) {
            this(modid, QuiltLoader.getConfigDir().resolve(configFileName));
        }

        public FireflasherConfigManager(String modid, Path confPath) {
            this(modid, confPath, Collections.singletonList(CATEGORY));
        }

        public FireflasherConfigManager(String modid, Path confPath, List<io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory> modConfig) {
            this.modid = modid;
            this.confPath = confPath;
            this.modConfig = modConfig;
        }

        public void save() {
            try {
                saveFile();
            } catch (IOException e) {
                AxolotlClientConfigManager.LOGGER.error("Failed to save config for mod: {}!", modid);
            }
        }

        private void saveFile() throws IOException {

            JsonObject config;
            try {
                config = JsonParser.parseReader(new FileReader(confPath.toString(), StandardCharsets.UTF_8)).getAsJsonObject();
            } catch (Exception e) {
                config = new JsonObject();
            }
            for (io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory category : modConfig) {
                JsonObject o;
                if (config.has(getName(category)) && config.get(getName(category)).isJsonObject() && !category.getName().equals(CATEGORY.getName())) {
                    o = config.get(getName(category)).getAsJsonObject();
                } else {
                    o = new JsonObject();
                }
                config.add(getName(category), getConfig(o, category));
            }

            Files.write(confPath, Collections.singleton(gson.toJson(config)));
        }

        public void load() {

            loadDefaults();

            try {
                JsonObject config = JsonParser.parseReader(new FileReader(confPath.toString())).getAsJsonObject();

                config.asMap().forEach((s, e) -> {
                    if (s.equals(CATEGORY.getName())) {
                        e.getAsJsonObject().asMap().forEach((name, value) -> {
                            if (value.getAsString().equals(PLACEHOLDER)) {
                                CATEGORY.add(getSpecialOption(CATEGORY, name));
                            }
                        });
                    }
                });
                for (io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory category : modConfig) {
                    if (config.has(getName(category))) {
                        setOptions(config.get(getName(category)).getAsJsonObject(), category);
                    }
                }
            } catch (Exception e) {
                AxolotlClientConfigManager.LOGGER.error("Failed to load config for modid {}! Using default values...", modid);
            }
        }

        public void loadDefaults() {
            modConfig.forEach(this::setOptionDefaults);
        }
    }
}
