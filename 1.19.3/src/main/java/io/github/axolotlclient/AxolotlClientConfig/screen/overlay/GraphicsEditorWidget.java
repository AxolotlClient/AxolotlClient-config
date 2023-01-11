package io.github.axolotlclient.AxolotlClientConfig.screen.overlay;

import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfig.screen.OptionsScreenBuilder;
import io.github.axolotlclient.AxolotlClientConfig.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.Arrays;

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

        addDrawableChild(ButtonWidget.builder(Text.translatable("clearGraphics"),
                        buttonWidget -> clearGraphics())
                .width(50).position(gridX + maxGridWidth + 10, gridY).build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        // Draw pixels
        for (int x = 0; x < gridCollumns; x++) {
            for (int y = 0; y < gridRows; y++) {
                if (pixels[x][y] != 0) {
                    fill(matrices, gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, pixels[x][y]);
                }
            }
        }

        // Draw grid
        for (int i = gridX; i <= (gridX + maxGridWidth); i += pixelSize) {
            fill(matrices, i, gridY, i + 1, gridY + maxGridHeight + 1, -1);
        }

        for (int i = gridY; i <= (gridY + maxGridHeight); i += pixelSize) {
            fill(matrices, gridX, i, gridX + maxGridWidth, i + 1, -1);
        }


        // Mouse interaction
        int mouseGridX = (mouseX - gridX) / pixelSize;
        int mouseGridY = (mouseY - gridY) / pixelSize;

        if (mouseGridX >= 0 && mouseGridY >= 0 && mouseGridX < gridCollumns && mouseGridY < gridRows) {
            DrawUtil.outlineRect(matrices, gridX + mouseGridX * pixelSize + 1, gridY + mouseGridY * pixelSize + 1, pixelSize - 1, pixelSize - 1, Color.SELECTOR_GREEN.getAsInt());

            if (mouseDown) {
                if (mouseGridX != lastChangedPixel[0] || mouseGridY != lastChangedPixel[1] || Util.getMeasuringTimeMs() - lastChangeTime >= 300) {
                    pixels[mouseGridX][mouseGridY] = pixels[mouseGridX][mouseGridY] != 0 ? 0 : -1;
                    lastChangedPixel[0] = mouseGridX;
                    lastChangedPixel[1] = mouseGridY;
                    lastChangeTime = Util.getMeasuringTimeMs();
                }
            }
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
}
