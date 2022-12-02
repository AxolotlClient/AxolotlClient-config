package io.github.axolotlclient.AxolotlclientConfig.screen.widgets;

import com.mojang.blaze3d.glfw.Window;
import com.mojang.blaze3d.platform.InputUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlclientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlclientConfig.screen.OptionsScreenBuilder;
import io.github.axolotlclient.AxolotlclientConfig.util.ConfigUtils;
import io.github.axolotlclient.AxolotlclientConfig.util.DrawUtil;
import io.github.axolotlclient.AxolotlclientConfig.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class ColorSelectionWidget extends Screen {
    private final ColorOption option;

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

    protected OptionsScreenBuilder parent;

    public ColorSelectionWidget(ColorOption option, OptionsScreenBuilder parent) {
        super(Text.of("Pick Color"));
        this.parent = parent;
        this.option=option;
    }

    public void init(){
        Window window= MinecraftClient.getInstance().getWindow();
        width=window.getScaledWidth()-200;
        height=window.getScaledHeight()-100;

        picker = new Rectangle(100, 50, width, height);

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

		addSelectableChild(parent.backButton);
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
            protected boolean canHover() {
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
            protected boolean canHover() {
                return true;
            }

            @Override
            public Text getMessage() {
                return Text.of(this.option.getTranslatedName()).copy().append(": ").append(super.getMessage());
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
        parent.render(matrices, mouseX, mouseY, delta);

        DrawUtil.fillRect(matrices, picker, Color.DARK_GRAY.withAlpha(127));
        DrawUtil.outlineRect(matrices, picker, Color.BLACK);

        drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, Text.translatable("pickColor"), MinecraftClient.getInstance().getWindow().getScaledWidth()/2, 54, -1);

        DrawUtil.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, Text.translatable("currentColor").append(":") ,currentRect.x, currentRect.y - 10, -1);

        DrawUtil.fillRect(matrices, currentRect, option.get());
        DrawUtil.outlineRect(matrices, currentRect, Color.DARK_GRAY.withAlpha(127));

        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.setShaderTexture(0, wheel);
        DrawableHelper.drawTexture(matrices, pickerImage.x, pickerImage.y, 0, 0, pickerImage.width, pickerImage.height, pickerImage.width, pickerImage.height);
        DrawUtil.outlineRect(matrices, pickerOutline, Color.DARK_GRAY);

        super.render(matrices, mouseX, mouseY, delta);
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
            if(alphaSlider.isHoveredOrFocused() || alphaSlider.dragging){
                option.set(new Color(option.get().getRed(), option.get().getGreen(), option.get().getBlue(), alpha.get()));
            } else {
                alpha.set(option.get().getAlpha());
                alphaSlider.update();
            }
        }

        if(slidersVisible) {
            if (option.get().getRed() != redSlider.getSliderValueAsInt()) {
                if (redSlider.isHoveredOrFocused() || redSlider.dragging) {
                    option.set(new Color(red.get(), option.get().getGreen(), option.get().getBlue(), option.get().getAlpha()));
                } else {
                    red.set(option.get().getRed());
                    redSlider.update();
                }
            }

            if (option.get().getGreen() != greenSlider.getSliderValueAsInt()) {
                if (greenSlider.isHoveredOrFocused() || greenSlider.dragging) {
                    option.set(new Color(option.get().getRed(), green.get(), option.get().getBlue(), option.get().getAlpha()));
                } else {
                    green.set(option.get().getGreen());
                    greenSlider.update();
                }
            }

            if (option.get().getBlue() != blueSlider.getSliderValueAsInt()) {
                if (blueSlider.isHoveredOrFocused() || blueSlider.dragging) {
                    option.set(new Color(option.get().getRed(), option.get().getGreen(), blue.get(), option.get().getAlpha()));
                } else {
                    blue.set(option.get().getBlue());
                    blueSlider.update();
                }
            }
        }
        parent.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!picker.isMouseOver(mouseX, mouseY)){
            parent.closeColorPicker();
            return true;
        }
        if(parent.backButton.isMouseOver(mouseX, mouseY)){
            parent.backButton.playDownSound(MinecraftClient.getInstance().getSoundManager());
            parent.closeColorPicker();
            return true;
        }
        return onClick(mouseX, mouseY) || super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean onClick(double mouseX, double mouseY){
        boolean bl = false;
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
            bl=true;
        } else if (chromaWidget.isMouseOver(mouseX, mouseY)) {
            chromaWidget.mouseClicked(mouseX, mouseY, 0);
            option.setChroma(chroma.get());
            bl=true;
        } else if (alphaSlider.isMouseOver(mouseX, mouseY)) {
            option.set(new Color(option.get().getRed(), option.get().getGreen(), option.get().getBlue(), alpha.get()));
            bl=true;
        }
        if(slidersVisible) {
            if (redSlider.isMouseOver(mouseX, mouseY)) {
                option.set(new Color(red.get(), option.get().getGreen(), option.get().getBlue(), option.get().getAlpha()));
                bl=true;
            } else if (greenSlider.isMouseOver(mouseX, mouseY)) {
                option.set(new Color(option.get().getRed(), green.get(), option.get().getBlue(), option.get().getAlpha()));
                bl=true;
            } else if (blueSlider.isMouseOver(mouseX, mouseY)) {
                option.set(new Color(option.get().getRed(), option.get().getGreen(), blue.get(), option.get().getAlpha()));
                bl=true;
            }
        }
        return bl || textInput.mouseClicked(mouseX, mouseY, 0);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(slidersVisible) {
            redSlider.mouseReleased(mouseX, mouseY, button);
            greenSlider.mouseReleased(mouseX, mouseY, button);
            blueSlider.mouseReleased(mouseX, mouseY, button);
        }
        return alphaSlider.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == InputUtil.KEY_ESCAPE_CODE){
            parent.closeColorPicker();
            return true;
        }
        boolean bl = super.keyPressed(keyCode, scanCode, modifiers);
        if(textInput.isFocused()){
            bl = bl || textInput.keyPressed(keyCode, scanCode, modifiers);
        }
        alphaSlider.update();

        if(slidersVisible) {
            redSlider.update();
            greenSlider.update();
            blueSlider.update();
        }
        return bl;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(textInput.isFocused()){
            boolean b = textInput.charTyped(chr, modifiers);
            alphaSlider.update();

            if(slidersVisible) {
                redSlider.update();
                greenSlider.update();
                blueSlider.update();
            }
            return  b;
        }
        return false;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        parent.resize(client, width, height);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount) || parent.mouseScrolled(mouseX, mouseY, amount);
    }

    private static class ColorSliderWidget extends OptionSliderWidget<IntegerOption, Integer> {

        public ColorSliderWidget(int x, int y, int width, int height, IntegerOption option) {
            super(x, y, width, height, option);
        }

        @Override
        protected boolean canHover() {
            return true;
        }

        @Override
        public Text getMessage() {
            return Text.of(this.getOption().getTranslatedName()).copy().append(": ").append(super.getMessage());
        }
    }

    public String getModId(){
        return parent.modid;
    }
}
