package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.options.StringOption;
import io.github.axolotlclient.AxolotlClientConfig.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class StringOptionWidget extends OptionWidget {

    public TextFieldWidget textField;

    public final StringOption option;

    public StringOptionWidget(int id, int x, int y, StringOption option){
        super(id, x, y, 150, 40, option.get());
        textField = new TextFieldWidget(0, MinecraftClient.getInstance().textRenderer, x, y, 149, 20){
            @Override
            public void mouseClicked(int mouseX, int mouseY, int button) {
                if(isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)) {
                    super.mouseClicked(mouseX, mouseY, button);
                } else {
                    this.setFocused(false);
                }
            }
        };
        this.option=option;
        textField.setText(option.get());
        textField.setVisible(true);
        textField.setEditable(true);
        textField.setMaxLength(512);
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {
        GlStateManager.disableDepthTest();
        textField.y = y;
        textField.x = x;
        textField.render();
        GlStateManager.enableDepthTest();
        if(!textField.getText().equals(option.get())){
            textField.setText(option.get());
        }
    }

    public boolean keyPressed(char c, int code){
        if(textField.isFocused()) {
            this.textField.keyPressed(c, code);
            this.option.set(textField.getText());
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isOverlayOpen()){
            this.hovered = false;
            return false;
        }
        return super.isMouseOver(client, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        textField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void unfocus() {
        textField.mouseClicked(0, 0, 0);
    }
}
