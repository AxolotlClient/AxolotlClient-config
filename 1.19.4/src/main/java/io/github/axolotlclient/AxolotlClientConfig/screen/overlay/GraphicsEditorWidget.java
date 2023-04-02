package io.github.axolotlclient.AxolotlClientConfig.screen.overlay;

import java.util.Arrays;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfig.screen.OptionsScreenBuilder;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.BooleanWidget;
import io.github.axolotlclient.AxolotlClientConfig.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class GraphicsEditorWidget extends Overlay {

    private final GraphicsOption option;
    private final int[] lastChangedPixel = new int[2];
    private int[][] pixels;
    private int maxGridWidth;
    private int maxGridHeight;
    private int gridCollumns;
    private int gridRows;
    private int pixelSize;
    private int gridX;
    private int gridY;
    private boolean mouseDown;
    private long lastChangeTime;
    private boolean keyboardInput;

    public GraphicsEditorWidget(GraphicsOption option, OptionsScreenBuilder parent) {
        super("drawGraphic", parent);
        this.option = option;
    }

    @Override
    public void init() {

        super.init();
        gridX = 110;
        gridY = 70;

        pixels = option.get();

        maxGridWidth = width - 20;
        maxGridHeight = height - 30;

        Arrays.stream(pixels).forEach(arr -> gridCollumns = Math.max(gridCollumns, arr.length));
        gridRows = pixels.length;

        pixelSize = Math.min(maxGridHeight / gridRows, maxGridWidth / gridCollumns);

        gridX = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - (gridCollumns * pixelSize) / 2;
        maxGridWidth = Math.min(maxGridWidth, gridCollumns * pixelSize);
        maxGridHeight = Math.min(maxGridHeight, gridRows * pixelSize);

        addDrawableChild(new ElementSelectable(gridX, gridY, maxGridWidth, maxGridHeight));

        addDrawableChild(ButtonWidget.builder(Text.translatable("clearGraphics"),
                        buttonWidget -> clearGraphics())
                .width(50).position(gridX + maxGridWidth + 10, gridY).build());

        if (option.mayDrawHint()) {
            addDrawableChild(
                    new BooleanWidget(gridX + maxGridWidth + 10, gridY + 25, 50, 20,
                            new BooleanOption("showHint", option::setDrawHint, option.isDrawHint())){
                        @Override
                        public boolean canHover() {
                            return true;
                        }

                        @Override
                        public Text getMessage() {
                            return Text.of(this.option.getTranslatedName()).copy().append(": ").append(super.getMessage());
                        }
                    });
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        // Draw pixels
        for (int x = 0; x < gridCollumns; x++) {
            for (int y = 0; y < gridRows; y++) {
                if (pixels[x][y] != 0) {
                    fill(matrices, gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, pixels[x][y]);
                } else {
                    if (x % 2 == 0 && y % 2 == 0 || (x % 2 != 0 && y % 2 != 0)) {
                        int checkerboardColor1 = 0xFF242424;
                        fill(matrices, gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, checkerboardColor1);
                    } else {
                        int checkerboardColor2 = 0xFF383838;
                        fill(matrices, gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, checkerboardColor2);
                    }
                }
            }
        }

        DrawUtil.outlineRect(matrices, gridX - 1, gridY - 1, maxGridWidth + 2, maxGridHeight + 2, -1);

        // Draw Hint (default, but in black and outlined)
        if(option.isDrawHint()){
            for (int x = 0; x < gridCollumns; x++) {
                for (int y = 0; y < gridRows; y++) {
                    if (option.getDefault()[x][y] != 0) {
                        DrawUtil.outlineRect(matrices, gridX + x * pixelSize, gridY + y * pixelSize, pixelSize, pixelSize, 0xFF000000);
                    }
                }
            }
        }

        DrawUtil.outlineRect(matrices, gridX + lastChangedPixel[0] * pixelSize, gridY + lastChangedPixel[1] * pixelSize, pixelSize, pixelSize, Color.SELECTOR_GREEN.getAsInt());

        // Mouse interaction
        int mouseGridX = (int) Math.floor((mouseX - gridX) / (float) pixelSize);
        int mouseGridY = (int) Math.floor((mouseY - gridY) / (float) pixelSize);

        if (mouseGridX >= 0 && mouseGridY >= 0 && mouseGridX < gridCollumns && mouseGridY < gridRows && !keyboardInput) {

            if (mouseDown) {
                if (mouseGridX != lastChangedPixel[0] || mouseGridY != lastChangedPixel[1] || Util.getMeasuringTimeMs() - lastChangeTime >= 300) {
                    pixels[mouseGridX][mouseGridY] = pixels[mouseGridX][mouseGridY] != 0 ? 0 : -1;
                    lastChangeTime = Util.getMeasuringTimeMs();
                }
            }

            lastChangedPixel[0] = mouseGridX;
            lastChangedPixel[1] = mouseGridY;
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean onClick(double mouseX, double mouseY) {
        if (mouseX >= gridX && mouseY >= gridY && mouseX <= gridX + maxGridWidth && mouseY <= gridY + maxGridHeight && !mouseDown) {
            mouseDown = true;
            keyboardInput = false;
            return true;
        }
        return false;
    }

    private void clearGraphics() {
        for (int[] a : pixels) {
            Arrays.fill(a, 0);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (mouseDown) {
            mouseDown = false;
            option.set(pixels);
            return true;
        }
        return false;
    }

    private class ElementSelectable extends PressableWidget {

        public ElementSelectable(int x, int y, int width, int height){
            super(x, y, width, height, Text.empty());
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            keyboardInput = true;
            if(keyCode == InputUtil.KEY_UP_CODE){
                if(lastChangedPixel[1] > 0) {
                    lastChangedPixel[1] -= 1;
                    return true;
                }
            } else if (keyCode == InputUtil.KEY_DOWN_CODE){
                if((lastChangedPixel[1]+1) * pixelSize < height) {
                    lastChangedPixel[1] += 1;
                    return true;
                }
            } else if(keyCode == InputUtil.KEY_LEFT_CODE){
                if(lastChangedPixel[0] > 0) {
                    lastChangedPixel[0] -= 1;
                    return true;
                }
            } else if (keyCode == InputUtil.KEY_RIGHT_CODE){
                if((lastChangedPixel[0]+1) * pixelSize < width) {
                    lastChangedPixel[0] += 1;
                    return true;
                }
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public void onPress() {
            pixels[lastChangedPixel[0]][lastChangedPixel[1]] = pixels[lastChangedPixel[0]][lastChangedPixel[1]] != 0 ? 0 : -1;
        }

        @Override
        public void render(MatrixStack matrices, int i, int j, float f) {
            if(isHoveredOrFocused()){
                DrawUtil.outlineRect(matrices, gridX - 1, gridY - 1, maxGridWidth + 2, maxGridHeight + 2, Color.SELECTOR_RED.getAsInt());
            }
        }

        @Override
        protected void updateNarration(NarrationMessageBuilder builder) {

        }
    }
}
