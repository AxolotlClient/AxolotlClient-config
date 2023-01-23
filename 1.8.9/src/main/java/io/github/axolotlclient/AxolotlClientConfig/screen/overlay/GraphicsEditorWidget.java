package io.github.axolotlclient.AxolotlClientConfig.screen.overlay;

import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.BooleanWidget;
import io.github.axolotlclient.AxolotlClientConfig.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

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

    private ButtonWidget clear;
    private BooleanWidget hint;

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

        gridX = window.getWidth() / 2 - (gridCollumns * pixelSize) / 2;
        maxGridWidth = Math.min(maxGridWidth, gridCollumns * pixelSize);
        maxGridHeight = Math.min(maxGridHeight, gridRows * pixelSize);

        clear = new ButtonWidget(0, gridX + maxGridWidth + 10, gridY, 50, 20, I18n.translate("clearGraphics"));

        if (option.mayDrawHint()) {
            hint = new BooleanWidget(0, gridX + maxGridWidth+10, gridY+25, 50, 20,
                    new BooleanOption("showHint", option::setDrawHint, option.isDrawHint())){
                @Override
                protected boolean canHover() {
                    return true;
                }

                @Override
                public void updateMessage() {
                    this.message = option.getTranslatedName() + ": " + (option.get()? I18n.translate ("options."+"on"): I18n.translate ("options."+"off"));
                }
            };
        }
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {
        super.render(client, mouseX, mouseY);

        // Draw Pixels
        for (int x = 0; x < gridCollumns; x++) {
            for (int y = 0; y < gridRows; y++) {
                if (pixels[x][y] != 0) {
                    fill(gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, pixels[x][y]);
                } else {
                    if (x % 2 == 0 && y % 2 == 0 || (x % 2 != 0 && y % 2 != 0)) {
                        fill(gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, 0xFF242424);
                    } else {
                        fill(gridX + x * pixelSize, gridY + y * pixelSize, gridX + x * pixelSize + pixelSize, gridY + y * pixelSize + pixelSize, 0xFF383838);
                    }
                }
            }
        }

        DrawUtil.outlineRect(gridX - 1, gridY - 1, maxGridWidth + 2, maxGridHeight + 2, -1);

        // Draw Hint (default, but in black and outlined)
        if (option.isDrawHint()) {
            for (int x = 0; x < gridCollumns; x++) {
                for (int y = 0; y < gridRows; y++) {
                    if (option.getDefault()[x][y] != 0) {
                        DrawUtil.outlineRect(gridX + x * pixelSize, gridY + y * pixelSize, pixelSize, pixelSize, 0xFF000000);
                    }
                }
            }
        }

        // Mouse Interaction
        int mouseGridX = (int) Math.floor((mouseX - gridX) / (float) pixelSize);
        int mouseGridY = (int) Math.floor((mouseY - gridY) / (float) pixelSize);

        if (mouseGridX >= 0 && mouseGridY >= 0 && mouseGridX < gridCollumns && mouseGridY < gridRows) {
            DrawUtil.outlineRect(gridX + mouseGridX * pixelSize, gridY + mouseGridY * pixelSize, pixelSize, pixelSize, Color.SELECTOR_GREEN.getAsInt());

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
        if(option.mayDrawHint()) {
            hint.render(client, mouseX, mouseY);
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void onClick(int mouseX, int mouseY) {
        mouseDown = true;

        if (clear.isHovered()) {
            clear.playDownSound(MinecraftClient.getInstance().getSoundManager());
            clearGraphics();
        } else if (option.mayDrawHint() && hint.isHovered()) {
            hint.playDownSound(MinecraftClient.getInstance().getSoundManager());
            hint.mouseClicked(mouseX, mouseY, 0);
        }
    }

    private void clearGraphics() {
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
