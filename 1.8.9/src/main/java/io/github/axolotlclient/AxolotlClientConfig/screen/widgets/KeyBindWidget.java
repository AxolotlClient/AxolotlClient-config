package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlClientConfig.options.KeyBindOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KeyBindWidget extends OptionWidget {

    private final KeyBindOption option;

    private final ButtonWidget keyButton;
    private final ButtonWidget resetButton;

    public KeyBindWidget(int x, int y, int width, int height, KeyBindOption option) {
        super(0, x, y, width, height, "");
        this.option = option;
        keyButton = new OptionWidget(0, x, y, width * 2 / 3, 20, GameOptions.getFormattedNameForKeyCode(option.get().getCode())) ;
        resetButton = new OptionWidget(0, x + width * 2 / 3, y, width / 3, 20, I18n.translate("controls.reset"));
    }

    private void updateMessage(){
        if(getFocused()) {
            this.keyButton.message = Formatting.WHITE + "> " + Formatting.YELLOW + GameOptions.getFormattedNameForKeyCode(option.get().getCode()) + Formatting.WHITE + " <";
        } else if (isConflict()) {
            this.keyButton.message = Formatting.RED + GameOptions.getFormattedNameForKeyCode(option.get().getCode());
        } else {
            keyButton.message = GameOptions.getFormattedNameForKeyCode(option.get().getCode());
        }
    }

    private boolean isConflict(){
        if (this.option.get().getCode() != 0) {
            final List<KeyBinding> binds = Arrays.stream(MinecraftClient.getInstance().options.allKeys.clone()).collect(Collectors.toList());
            binds.addAll(KeyBindOption.getBindings());
            for(KeyBinding keyBinding : binds) {
                if (keyBinding != this.option.get() && keyBinding.getCode() == this.option.get().getCode()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {
        keyButton.x = this.x;
        resetButton.x = this.x+width*2/3;

        keyButton.y = resetButton.y = this.y;

        updateMessage();

        keyButton.render(client, mouseX, mouseY);

        this.resetButton.active = this.option.get().getCode() != this.option.get().getDefaultCode();
        resetButton.render(client, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(keyButton.isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)){
            setFocused(true);
        } else if (resetButton.isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY) && resetButton.active) {
            MinecraftClient.getInstance().options.setKeyBindingCode(option.get(), option.get().getDefaultCode());
            KeyBinding.updateKeysByCode();
        }
    }

    @Override
    public boolean keyPressed(char character, int code) {
        if(focused) {
            if (code == 1) {
                MinecraftClient.getInstance().options.setKeyBindingCode(option.get(), 0);
            } else if (code != 0) {
                MinecraftClient.getInstance().options.setKeyBindingCode(option.get(), code);
            } else if (character > 0) {
                MinecraftClient.getInstance().options.setKeyBindingCode(option.get(), character + 256);
            }
            KeyBinding.updateKeysByCode();
            unfocus();
            return true;
        }
        return false;
    }
}
