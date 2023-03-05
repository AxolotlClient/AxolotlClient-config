package io.github.axolotlclient.AxolotlClientConfig.options;

import com.google.gson.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.texture.NativeImage;
import io.github.axolotlclient.AxolotlClientConfig.common.commands.CommandResponse;
import io.github.axolotlclient.AxolotlClientConfig.screen.OptionsScreenBuilder;
import io.github.axolotlclient.AxolotlClientConfig.screen.overlay.GraphicsEditorWidget;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.GenericOptionWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.ColorUtil;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class GraphicsOption extends OptionBase<int[][]> {

    private NativeImageBackedTexture texture;
    private int width;
    private int height;
    private Identifier textureId;
    private boolean mayDrawHint;
    private boolean drawHint;

    public GraphicsOption(String name, int[][] def) {
        super(name, def);
    }

    public GraphicsOption(String name, ChangedListener<int[][]> onChange, int[][] def) {
        super(name, onChange, def);
    }

    public GraphicsOption(String name, String tooltipKeyPrefix, int[][] def) {
        super(name, tooltipKeyPrefix, def);
    }

    public GraphicsOption(String name, String tooltipKeyPrefix, ChangedListener<int[][]> onChange, int[][] def) {
        super(name, tooltipKeyPrefix, onChange, def);
    }

    public GraphicsOption(String name, int[][] def, boolean mayDrawHint) {
        super(name, def);
        this.mayDrawHint = mayDrawHint;
    }

    public GraphicsOption(String name, ChangedListener<int[][]> onChange, int[][] def, boolean mayDrawHint) {
        super(name, onChange, def);
        this.mayDrawHint = mayDrawHint;
    }

    public GraphicsOption(String name, String tooltipKeyPrefix, int[][] def, boolean mayDrawHint) {
        super(name, tooltipKeyPrefix, def);
        this.mayDrawHint = mayDrawHint;
    }

    public GraphicsOption(String name, String tooltipKeyPrefix, ChangedListener<int[][]> onChange, int[][] def, boolean mayDrawHint) {
        super(name, tooltipKeyPrefix, onChange, def);
        this.mayDrawHint = mayDrawHint;
    }

    public boolean isDrawHint() {
        return drawHint;
    }

    public void setDrawHint(boolean drawHint) {
        this.drawHint = drawHint;
    }

    public boolean mayDrawHint() {
        return mayDrawHint;
    }

    @Override
    public void setDefaults() {
        option = deepCopy(getDefault());
    }

    private int[][] deepCopy(int[][] in) {
        AtomicInteger width = new AtomicInteger();
        Arrays.stream(in).forEach(arr -> width.set(Math.max(width.get(), arr.length)));
        int height = in.length;

        int[][] out = new int[width.get()][height];

        for (int i = 0; i < height; i++) {
            out[i] = new int[in[i].length];

            System.arraycopy(in[i], 0, out[i], 0, in[i].length);
        }

        return out;
    }

    @Override
    protected CommandResponse onCommandExecution(String arg) {

        if (arg.length() > 0) {
            try {
                setValueFromJsonElement(JsonParser.parseString(arg));
                return new CommandResponse(true, "Successfully set " + getName() + " to its new value!");
            } catch (Exception e) {
                return new CommandResponse(false, "Failed to parse input " + arg + "!");
            }
        }

        return new CommandResponse(true, getName() + " is currently set to '" + getJson() + "'.");
    }

    @Override
    public JsonElement getJson() {
        JsonObject object = new JsonObject();
        JsonArray data = new JsonArray();

        for (int[] a : get()) {
            JsonArray j = new JsonArray();
            for (int i : a) {
                j.add(new JsonPrimitive(i));
            }
            data.add(j);
        }

        object.add("data", data);
        object.add("hint", new JsonPrimitive(mayDrawHint));

        return object;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {

        mayDrawHint = element.getAsJsonObject().get("hint").getAsBoolean();
        JsonArray data = element.getAsJsonObject().get("data").getAsJsonArray();

        int i = 0;
        for (int[] a : option) {
            JsonArray r = data.get(i).getAsJsonArray();

            for (int it = 0; it < a.length; it++) {
                a[it] = r.get(it).getAsInt();
            }

            i++;
        }

    }

    @Override
    public ButtonWidget getWidget(int x, int y, int width, int height) {
        return new GenericOptionWidget(x, y, width, height,
                new GenericOption(getName(), "openEditor", (mouseX, mouseY) ->
                        ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).setOverlay(
                                new GraphicsEditorWidget(this, (OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen))));
    }

    public void bindTexture() {
        if (texture == null) {
            int[][] data = get();
            AtomicInteger width = new AtomicInteger();
            Arrays.stream(data).forEach(arr -> width.set(Math.max(width.get(), arr.length)));
            height = data.length;
            texture = new NativeImageBackedTexture(this.width = width.get(), height, false);
            textureId = new Identifier("graphicsoption", getName().toLowerCase(Locale.ROOT) + java.util.UUID.randomUUID().toString().replace('-', '_'));
            MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, texture);
        }

        NativeImage image = texture.getImage();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = get()[x][y];
                image.setPixelColor(x, y, ColorUtil.ABGR32.getColor(ColorUtil.ABGR32.getAlpha(color), ColorUtil.ABGR32.getRed(color), ColorUtil.ABGR32.getGreen(color), ColorUtil.ABGR32.getBlue(color)));
            }
        }
        texture.upload();

        RenderSystem.setShaderTexture(0, textureId);
    }
}
