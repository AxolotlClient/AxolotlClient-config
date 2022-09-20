package io.github.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
import io.github.axolotlclient.config.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class ColorOptionWidget extends ButtonWidget {

    private final ColorOption option;

    public final TextFieldWidget textField;
    private final ButtonWidget openPicker;

    /** Pipette icon in KDE plasma icon theme 'BeautyLine' by Sajjad Abdollahzadeh <sajjad606@gmail.com>
     * <a href="https://store.kde.org/p/1425426">KDE Store Link</a>
     * @license GPL-3
     **/
    protected final Identifier pipette = new Identifier("axolotlclient","textures/gui/pipette.png");

    public ColorOptionWidget(int id, int x, int y, ColorOption option) {
        super(id, x, y, 150, 20, "");
        this.option=option;
        textField = new TextFieldWidget(0, MinecraftClient.getInstance().textRenderer, x, y, 128, 19);
        textField.write(option.get().toString());

        openPicker = new ButtonWidget(2, x+128, y, 21, 21, ""){
            @Override
            public void render(MinecraftClient client, int mouseX, int mouseY) {
                DrawUtil.fill(x, y, x+width, y+height, option.get().getAsInt());
                DrawUtil.outlineRect(x, y, width, height, -6250336);

                GlStateManager.color3f(1, 1,1);
                MinecraftClient.getInstance().getTextureManager().bindTexture(pipette);
                drawTexture(x, y, 0, 0, 20, 20, 21, 21);
            }
        };
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {

        textField.y = y;
        textField.x = x;
        textField.render();

        openPicker.y = y-1;
        openPicker.x = x+128;
        openPicker.render(client, mouseX, mouseY);

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        super.mouseReleased(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isPickerOpen()) return false;
        return super.isMouseOver(client, mouseX, mouseY);
    }

    public void mouseClicked(int mouseX, int mouseY){
        if(openPicker.isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)){

            if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder){
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).openColorPicker(option);
            }
        } else {
            textField.mouseClicked(mouseX, mouseY, 0);
            if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder){
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).closeColorPicker();
            }
        }
    }

    public void tick(){
        if(textField.isFocused()) {
            textField.tick();
        } else {
            if((MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
                    ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isPickerOpen() || option.getChroma()) &&
                    !Objects.equals(textField.getText(), option.get().toString())){
                textField.setText(option.get().toString());
            }
        }
    }

    public void keyPressed(char c, int code){
        if(textField.isFocused()) {
            textField.keyPressed(c, code);
            option.set(Color.parse(textField.getText()));
        }
    }
}
