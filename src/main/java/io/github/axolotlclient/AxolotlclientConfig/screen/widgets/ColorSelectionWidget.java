package io.github.axolotlclient.AxolotlclientConfig.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlclientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlclientConfig.util.ConfigUtils;
import io.github.axolotlclient.AxolotlclientConfig.util.DrawUtil;
import io.github.axolotlclient.AxolotlclientConfig.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class ColorSelectionWidget extends ButtonWidget {
    private final ColorOption option;
    private Window window;

    protected Rectangle picker;

    // Texture based on https://github.com/MartinThoma/LaTeX-examples/blob/master/documents/printer-testpage/printer-testpage.tex
    protected Identifier wheel = new Identifier("axolotlclient", "textures/gui/colorwheel.png");
    protected Rectangle pickerImage;
    protected Rectangle currentRect;
    protected Rectangle pickerOutline;

    protected BooleanOption chroma = new BooleanOption("chroma", false);
    protected BooleanWidget chromaWidget;

    protected IntegerOption alpha = new IntegerOption("alpha", 0, 0, 255);
    protected ColorSliderWidget alphaSlider;

    protected IntegerOption red = new IntegerOption("red", 0, 0, 255);
    protected ColorSliderWidget redSlider;

    protected IntegerOption green = new IntegerOption("green", 0, 0, 255);
    protected ColorSliderWidget greenSlider;

    protected IntegerOption blue = new IntegerOption("blue", 0, 0, 255);
    protected ColorSliderWidget blueSlider;

    protected boolean slidersVisible;

    protected TextFieldWidget textInput;

    public ColorSelectionWidget(ColorOption option) {
        super(0, 100, 50, "");
        this.option=option;
        init();
    }

    public void init(){
        window = new Window(MinecraftClient.getInstance());
        width=window.getWidth()-200;
        height=window.getHeight()-100;

        picker = new Rectangle(100, 50, width, height);

        pickerImage = new Rectangle(120, 70, width/2, height/2);
        pickerOutline = new Rectangle(pickerImage.x-1, pickerImage.y-1, pickerImage.width+2, pickerImage.height+2);
        currentRect = new Rectangle(pickerImage.x + pickerImage.width + 20, pickerImage.y + 10, width - pickerImage.width - 60, 20);

        chroma.set(option.getChroma());

        alpha.set(option.get().getAlpha());

        slidersVisible = height>175;

        if(slidersVisible) {
            red.set(option.get().getRed());
            green.set(option.get().getGreen());
            blue.set(option.get().getBlue());
        }

        chromaWidget = new BooleanWidget(0, currentRect.x, currentRect.y + currentRect.height + 40, currentRect.width, 20, chroma){
            @Override
            protected boolean canHover() {
                return true;
            }

            @Override
            public void updateMessage() {
                this.message = option.getTranslatedName() + ": " + (option.get()? I18n.translate ("options."+"on"): I18n.translate ("options."+"off"));
            }
        };

        alphaSlider = new ColorSliderWidget(0, pickerImage.x, pickerImage.y + pickerImage.height + 20, pickerImage.width, 20, alpha);

        if(slidersVisible) {
            redSlider = new ColorSliderWidget(0, currentRect.x, currentRect.y + currentRect.height + 65, currentRect.width, 20, red);
            greenSlider = new ColorSliderWidget(0, currentRect.x, currentRect.y + currentRect.height + 90, currentRect.width, 20, green);
            blueSlider = new ColorSliderWidget(0, currentRect.x, currentRect.y + currentRect.height + 115, currentRect.width, 20, blue);
        }

        textInput = new TextFieldWidget(0, MinecraftClient.getInstance().textRenderer,
                currentRect.x, currentRect.y + currentRect.height + 10, currentRect.width, 20);
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {

        DrawUtil.fillRect(new Rectangle(100, 50, width, height), Color.DARK_GRAY.withAlpha(127));
        DrawUtil.outlineRect(new Rectangle(100, 50, width, height), Color.BLACK);

        drawCenteredString(MinecraftClient.getInstance().textRenderer, I18n.translate("pickColor"), window.getWidth()/2, 54, -1);

        DrawUtil.drawString(MinecraftClient.getInstance().textRenderer, I18n.translate("currentColor") + ":" ,currentRect.x, currentRect.y - 10, -1, true);

        DrawUtil.fillRect(currentRect, option.get());
        DrawUtil.outlineRect(currentRect, Color.DARK_GRAY.withAlpha(127));

        GlStateManager.color3f(1, 1, 1);

        MinecraftClient.getInstance().getTextureManager().bindTexture(wheel);
        DrawableHelper.drawTexture(pickerImage.x, pickerImage.y, 0, 0, pickerImage.width, pickerImage.height, pickerImage.width, pickerImage.height);
        DrawUtil.outlineRect(pickerOutline, Color.DARK_GRAY);

        chromaWidget.render(client, mouseX, mouseY);

        alphaSlider.render(client, mouseX, mouseY);

        if(slidersVisible) {
            redSlider.render(client, mouseX, mouseY);
            greenSlider.render(client, mouseX, mouseY);
            blueSlider.render(client, mouseX, mouseY);
        }

        textInput.render();

    }

    public void tick(){
        textInput.tick();

        if(!Objects.equals(textInput.getText(), option.get().toString())){
               if(textInput.isFocused()){
                   option.set(Color.parse(textInput.getText()));
               } else {
                   textInput.setText(option.get().toString());
               }
        }

        if(option.get().getAlpha() != alphaSlider.getSliderValueAsInt()){
            if(alphaSlider.isHovered() || alphaSlider.dragging){
                option.set(new Color(option.get().getRed(), option.get().getGreen(), option.get().getBlue(), alpha.get()));
            } else {
                alpha.set(option.get().getAlpha());
                alphaSlider.update();
            }
        }

        if(slidersVisible) {
            if (option.get().getRed() != redSlider.getSliderValueAsInt()) {
                if (redSlider.isHovered() || redSlider.dragging) {
                    option.set(new Color(red.get(), option.get().getGreen(), option.get().getBlue(), option.get().getAlpha()));
                } else {
                    red.set(option.get().getRed());
                    redSlider.update();
                }
            }

            if (option.get().getGreen() != greenSlider.getSliderValueAsInt()) {
                if (greenSlider.isHovered() || greenSlider.dragging) {
                    option.set(new Color(option.get().getRed(), green.get(), option.get().getBlue(), option.get().getAlpha()));
                } else {
                    green.set(option.get().getGreen());
                    greenSlider.update();
                }
            }

            if (option.get().getBlue() != blueSlider.getSliderValueAsInt()) {
                if (blueSlider.isHovered() || blueSlider.dragging) {
                    option.set(new Color(option.get().getRed(), option.get().getGreen(), blue.get(), option.get().getAlpha()));
                } else {
                    blue.set(option.get().getBlue());
                    blueSlider.update();
                }
            }
        }
    }

    public void onClick(int mouseX, int mouseY){

        if(pickerImage.isMouseOver(mouseX, mouseY)){
            final ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(4);
            pixelBuffer.order(ByteOrder.nativeOrder());

            // Helped in the complete confusion:
            // https://github.com/MrCrayfish/MrCrayfishDeviceMod/blob/2a06b20ad8873855885285f3cee6a682e161e24c/src/main/java/com/mrcrayfish/device/util/GLHelper.java#L71
            GL11.glReadPixels(ConfigUtils.toGlCoordsX(mouseX),
                    ConfigUtils.toGlCoordsY(mouseY),
                    1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);

            final int r = pixelBuffer.get(0) & 0xff;
            final int g = pixelBuffer.get(1) & 0xff;
            final int b = pixelBuffer.get(2) & 0xff;
            final Color index = new Color(r, g, b, alpha.get());

            option.set(index);

            alphaSlider.update();

            if(slidersVisible) {
                redSlider.update();
                greenSlider.update();
                blueSlider.update();
            }

        } else if (chromaWidget.isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)) {
            chromaWidget.mouseClicked(mouseX, mouseY, 0);
            option.setChroma(chroma.get());

        } else if (alphaSlider.isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)) {
            option.set(new Color(option.get().getRed(), option.get().getGreen(), option.get().getBlue(), alpha.get()));
        }
        if(slidersVisible) {
            if (redSlider.isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)) {
                option.set(new Color(red.get(), option.get().getGreen(), option.get().getBlue(), option.get().getAlpha()));
            } else if (greenSlider.isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)) {
                option.set(new Color(option.get().getRed(), green.get(), option.get().getBlue(), option.get().getAlpha()));
            } else if (blueSlider.isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)) {
                option.set(new Color(option.get().getRed(), option.get().getGreen(), blue.get(), option.get().getAlpha()));
            }
        }
        textInput.mouseClicked(mouseX, mouseY, 0);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        alphaSlider.mouseReleased(mouseX, mouseY);

        if(slidersVisible) {
            redSlider.mouseReleased(mouseX, mouseY);
            greenSlider.mouseReleased(mouseX, mouseY);
            blueSlider.mouseReleased(mouseX, mouseY);
        }
    }

    public void keyPressed(char c, int code){
        if(textInput.isFocused()){
            textInput.keyPressed(c, code);
            alphaSlider.update();

            if(slidersVisible) {
                redSlider.update();
                greenSlider.update();
                blueSlider.update();
            }
        }
    }

    private class ColorSliderWidget extends OptionSliderWidget<IntegerOption, Integer> {

        public ColorSliderWidget(int id, int x, int y, int width, int height, IntegerOption option) {
            super(id, x, y, width, height, option);
        }

        @Override
        protected boolean canHover() {
            return true;
        }

        @Override
        protected @NotNull String getMessage() {
            return getOption().getTranslatedName() + ": " + super.getMessage();
        }
    }
}
