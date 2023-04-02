package io.github.axolotlclient.AxolotlClientConfig.screen.overlay;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.common.util.Rectangle;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlClientConfig.screen.OptionsScreenBuilder;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.BooleanWidget;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.OptionSliderWidget;
import io.github.axolotlclient.AxolotlClientConfig.util.ConfigUtils;
import io.github.axolotlclient.AxolotlClientConfig.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class ColorSelectionWidget extends Overlay {
    private final ColorOption option;

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

    public ColorSelectionWidget(ColorOption option, OptionsScreenBuilder parent) {
        super("pickColor", parent);
        this.option=option;
    }

    public void init(){

        super.init();

        chroma.set(option.getChroma());

        alpha.set(option.get().getAlpha());

        if(width>500){
            initLarge();
        } else {
            initSmall();
        }

        if(slidersVisible) {
            red.set(option.get().getRed());
            green.set(option.get().getGreen());
            blue.set(option.get().getBlue());
        }

    }

    private void initLarge(){
        pickerImage = new Rectangle(120, 70, width*3/4, height*3/4);
        pickerOutline = new Rectangle(pickerImage.x-1, pickerImage.y-1, pickerImage.width+2, pickerImage.height+2);
        currentRect = new Rectangle(pickerImage.x + pickerImage.width + 20, pickerImage.y + 10, width - pickerImage.width - 60, 80);

        slidersVisible = true;

        addDrawableChild(textInput = new TextFieldWidget(MinecraftClient.getInstance().textRenderer,
                currentRect.x, currentRect.y + currentRect.height + 10, currentRect.width, 20, Text.empty()));

        addDrawableChild(chromaWidget = new BooleanWidget(currentRect.x, currentRect.y + currentRect.height + 40, currentRect.width, 20, chroma){
            @Override
            public boolean canHover() {
                return true;
            }

            @Override
            public Text getMessage() {
                return Text.of(this.option.getTranslatedName()).copy().append(": ").append(super.getMessage());
            }
        });

        addDrawableChild(redSlider = new ColorSliderWidget(currentRect.x, currentRect.y + currentRect.height + 65, currentRect.width, 20, red));
        addDrawableChild(greenSlider = new ColorSliderWidget(currentRect.x, currentRect.y + currentRect.height + 90, currentRect.width, 20, green));
        addDrawableChild(blueSlider = new ColorSliderWidget(currentRect.x, currentRect.y + currentRect.height + 115, currentRect.width, 20, blue));
        addDrawableChild(alphaSlider = new ColorSliderWidget(currentRect.x, currentRect.y+ currentRect.height + 140, currentRect.width, 20, alpha));

        textInput.setChangedListener(s -> {
            alphaSlider.update();
            redSlider.update();
            greenSlider.update();
            blueSlider.update();
        });
    }

    private void initSmall(){
        pickerImage = new Rectangle(120, 70, width/2, height/2);
        pickerOutline = new Rectangle(pickerImage.x-1, pickerImage.y-1, pickerImage.width+2, pickerImage.height+2);
        currentRect = new Rectangle(pickerImage.x + pickerImage.width + 20, pickerImage.y + 10, width - pickerImage.width - 60, 20);

        slidersVisible = height>175;

        if(slidersVisible) {
            red.set(option.get().getRed());
            green.set(option.get().getGreen());
            blue.set(option.get().getBlue());
        }

        addDrawableChild(textInput = new TextFieldWidget(MinecraftClient.getInstance().textRenderer,
                currentRect.x, currentRect.y + currentRect.height + 10, currentRect.width, 20, Text.empty()));

        addDrawableChild(chromaWidget = new BooleanWidget(currentRect.x, currentRect.y + currentRect.height + 40, currentRect.width, 20, chroma){
            @Override
            public boolean canHover() {
                return true;
            }

            @Override
            public Text getMessage() {
                return Text.of(this.option.getTranslatedName()).copy().append(": ").append(super.getMessage());
            }

            @Override
            public void onPress() {
                super.onPress();
                ColorSelectionWidget.this.option.setChroma(option.get());
            }
        });

        addDrawableChild(alphaSlider = new ColorSliderWidget(pickerImage.x, pickerImage.y + pickerImage.height + 20, pickerImage.width, 20, alpha));

        if(slidersVisible) {

            addDrawableChild(redSlider = new ColorSliderWidget(currentRect.x, currentRect.y + currentRect.height + 65, currentRect.width, 20, red));
            addDrawableChild(greenSlider = new ColorSliderWidget(currentRect.x, currentRect.y + currentRect.height + 90, currentRect.width, 20, green));
            addDrawableChild(blueSlider = new ColorSliderWidget(currentRect.x, currentRect.y + currentRect.height + 115, currentRect.width, 20, blue));
        }

        textInput.setChangedListener(s -> {
            alphaSlider.update();

            if(slidersVisible) {
                redSlider.update();
                greenSlider.update();
                blueSlider.update();
            }
        });
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        DrawUtil.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, Text.translatable("currentColor").append(":") ,currentRect.x, currentRect.y - 10, -1);

        DrawUtil.fillRect(matrices, currentRect, option.get());
        DrawUtil.outlineRect(matrices, currentRect, Color.DARK_GRAY.withAlpha(127));

        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.setShaderTexture(0, wheel);
        DrawableHelper.drawTexture(matrices, pickerImage.x, pickerImage.y, 0, 0, pickerImage.width, pickerImage.height, pickerImage.width, pickerImage.height);
        DrawUtil.outlineRect(matrices, pickerOutline, Color.DARK_GRAY);
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
            if(alphaSlider.isHoveredOrFocused()){
                option.set(new Color(option.get().getRed(), option.get().getGreen(), option.get().getBlue(), alpha.get()));
            } else {
                alpha.set(option.get().getAlpha());
                alphaSlider.update();
            }
        }

        if(slidersVisible) {
            if (option.get().getRed() != redSlider.getSliderValueAsInt()) {
                if (redSlider.isHoveredOrFocused()) {
                    option.set(new Color(red.get(), option.get().getGreen(), option.get().getBlue(), option.get().getAlpha()));
                } else {
                    red.set(option.get().getRed());
                    redSlider.update();
                }
            }

            if (option.get().getGreen() != greenSlider.getSliderValueAsInt()) {
                if (greenSlider.isHoveredOrFocused()) {
                    option.set(new Color(option.get().getRed(), green.get(), option.get().getBlue(), option.get().getAlpha()));
                } else {
                    green.set(option.get().getGreen());
                    greenSlider.update();
                }
            }

            if (option.get().getBlue() != blueSlider.getSliderValueAsInt()) {
                if (blueSlider.isHoveredOrFocused()) {
                    option.set(new Color(option.get().getRed(), option.get().getGreen(), blue.get(), option.get().getAlpha()));
                } else {
                    blue.set(option.get().getBlue());
                    blueSlider.update();
                }
            }
        }
    }

    public boolean onClick(double mouseX, double mouseY){
        if(pickerImage.isMouseOver(mouseX, mouseY)){
            final ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(4);
            pixelBuffer.order(ByteOrder.nativeOrder());

            // Helped in the complete confusion:
            // https://github.com/MrCrayfish/MrCrayfishDeviceMod/blob/2a06b20ad8873855885285f3cee6a682e161e24c/src/main/java/com/mrcrayfish/device/util/GLHelper.java#L71

            GL11.glReadPixels(ConfigUtils.toGlCoordsX((int) mouseX), ConfigUtils.toGlCoordsY((int) mouseY),
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
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(super.charTyped(chr, modifiers)){
            alphaSlider.update();

            if(slidersVisible) {
                redSlider.update();
                greenSlider.update();
                blueSlider.update();
            }
            return true;
        }
        return false;
    }

    private static class ColorSliderWidget extends OptionSliderWidget<IntegerOption, Integer> {

        public ColorSliderWidget(int x, int y, int width, int height, IntegerOption option) {
            super(x, y, width, height, option);
        }

        @Override
        public boolean canHover() {
            return true;
        }

        @Override
        public Text getMessage() {
            return Text.of(this.getOption().getTranslatedName()).copy().append(": ").append(super.getMessage());
        }

        @Override
        public void render(MatrixStack matrices, int i, int j, float f) {
            matrices.push();
            matrices.translate(0, 0, 5);
            super.render(matrices, i, j, f);
            matrices.pop();
        }
    }
}
