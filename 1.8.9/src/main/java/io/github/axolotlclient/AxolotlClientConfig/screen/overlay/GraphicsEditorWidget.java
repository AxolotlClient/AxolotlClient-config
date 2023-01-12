package io.github.axolotlclient.AxolotlClientConfig.screen.overlay;

import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfig.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.Arrays;

public class GraphicsEditorWidget extends Overlay {

    private final GraphicsOption option;
    private int[][] pixels;
    private int maxGridWidth;
    private int maxGridHeight;
    private int gridCollumns;
    private int gridRows;
    private int pixelSize;
    private int gridX;
    private int gridY;

    private boolean mouseDown;
    private final int[] lastChangedPixel = new int[2];
    private long lastChangeTime;

    private ButtonWidget clear;

    public GraphicsEditorWidget(GraphicsOption option) {
        super("drawGraphic");
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

        gridX = window.getWidth()/2 - (gridCollumns*pixelSize)/2;
        maxGridWidth = Math.min(maxGridWidth, gridCollumns*pixelSize);
        maxGridHeight = Math.min(maxGridHeight, gridRows*pixelSize);

        clear = new ButtonWidget(0, gridX + maxGridWidth + 10, gridY, 50, 20, I18n.translate("clearGraphics"));
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {
        super.render(client, mouseX, mouseY);

        // Draw Pixels
        for (int x = 0; x < gridCollumns; x++) {
            for (int y = 0; y < gridRows; y++) {
                if (pixels[x][y] != 0) {
                    fill(gridX + x*pixelSize, gridY + y*pixelSize, gridX +x*pixelSize + pixelSize, gridY + y*pixelSize + pixelSize, pixels[x][y]);
                }
            }
        }

        // Draw Grid
        for (int i = gridX; i <= (gridX + maxGridWidth); i += pixelSize) {
            fill(i, gridY, i+1, gridY + maxGridHeight+1, -1);

        }

        for (int i = gridY; i <= (gridY + maxGridHeight); i += pixelSize) {
            fill(gridX, i, gridX + maxGridWidth, i+1, -1);
        }

        // Mouse Interaction
        int mouseGridX = (mouseX - gridX) / pixelSize;
        int mouseGridY = (mouseY - gridY) / pixelSize;

        if (mouseGridX >= 0 && mouseGridY >= 0 && mouseGridX < gridCollumns && mouseGridY < gridRows) {
            DrawUtil.outlineRect(gridX + mouseGridX * pixelSize + 1, gridY + mouseGridY * pixelSize + 1, pixelSize-1, pixelSize-1, Color.SELECTOR_GREEN.getAsInt());

            if (mouseDown) {
                if (mouseGridX != lastChangedPixel[0] || mouseGridY != lastChangedPixel[1] || MinecraftClient.getTime() - lastChangeTime >= 300) {
                    pixels[mouseGridX][mouseGridY] = pixels[mouseGridX][mouseGridY] != 0 ? 0 : -1;
                    lastChangedPixel[0] = mouseGridX;
                    lastChangedPixel[1] = mouseGridY;
                    lastChangeTime = MinecraftClient.getTime();
                }
            }
        }

        clear.render(client, mouseX, mouseY);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void onClick(int mouseX, int mouseY) {
        mouseDown = true;

        if(clear.isHovered()){
            clear.playDownSound(MinecraftClient.getInstance().getSoundManager());
            clearGraphics();
        }
    }

    private void clearGraphics(){
        for (int[] a : pixels) {
            Arrays.fill(a, 0);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        mouseDown = false;
        option.set(pixels);
    }
}
