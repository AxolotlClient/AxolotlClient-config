package io.github.axolotlclient.AxolotlclientConfig.screen.widgets;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlclientConfig.options.KeyBindOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;

public class KeyBindWidget extends OptionWidget {

    private final KeyBindOption option;

    private final OptionWidget bindButton;
    private final OptionWidget resetButton;

    private final KeyBind key;

    private boolean edit;

    public KeyBindWidget(int x, int y, int width, int height, KeyBindOption option) {
        super(x, y, width, height, Text.empty(), (button) -> {});
        this.option = option;
        key = option.get();

        Text text = Text.translatable(option.get().getKeyTranslationKey());
        this.bindButton = new OptionWidget(0, 0, 100, 20, text, button -> edit = true) {
            protected MutableText getNarrationMessage() {
                return option.get().isUnbound()
                        ? Text.translatable("narrator.controls.unbound", text)
                        : Text.translatable("narrator.controls.bound", text, super.getNarrationMessage());
            }
        };
        this.resetButton = new OptionWidget(0, 0, 50, 20, Text.translatable("controls.reset"), button -> {
            MinecraftClient.getInstance().options.setKeyCode(option.get(), option.get().getDefaultKey());
            KeyBind.updateBoundKeys();
            keyPressed(InputUtil.KEY_LEFT_CODE, 0, 0);
        }) {
            protected MutableText getNarrationMessage() {
                return Text.translatable("narrator.controls.reset", text);
            }
        };
    }

    @Override
    public void onPress() {
        if(resetButton.isHoveredOrFocused()){
            resetButton.onPress();
        } else if (bindButton.isHoveredOrFocused()) {
            bindButton.onPress();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        if(resetButton.isHoveredOrFocused() && bindButton.isHoveredOrFocused()){
            resetButton.unfocus();
        }

        this.resetButton.x = x + 100;
        this.resetButton.y = y;
        this.resetButton.active = !option.get().isDefault();
        this.resetButton.render(matrices, mouseX, mouseY, tickDelta);
        this.bindButton.x = x;
        this.bindButton.y = y;
        this.bindButton.setMessage(this.key.getKeyName());
        boolean bl2 = false;
        if (!this.key.isUnbound()) {
            List<KeyBind> binds = KeyBindOption.getBindings();
            binds.addAll(Arrays.stream(MinecraftClient.getInstance().options.allKeys).toList());
            for(KeyBind keyBind : binds) {
                if (keyBind != this.key && this.key.keyEquals(keyBind)) {
                    bl2 = true;
                    break;
                }
            }
        }

        if (edit) {
            this.bindButton
                    .setMessage(
                            Text.literal("> ").append(this.bindButton.getMessage().copy().formatted(Formatting.YELLOW)).append(" <").formatted(Formatting.YELLOW)
                    );
        } else if (bl2) {
            this.bindButton.setMessage(this.bindButton.getMessage().copy().formatted(Formatting.RED));
        }

        this.bindButton.render(matrices, mouseX, mouseY, tickDelta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.bindButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else {
            return this.resetButton.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.bindButton.mouseReleased(mouseX, mouseY, button) || this.resetButton.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (edit) {
            if (keyCode == 256) {
                MinecraftClient.getInstance().options.setKeyCode(key, InputUtil.UNKNOWN_KEY);
            } else {
                MinecraftClient.getInstance().options.setKeyCode(key, InputUtil.fromKeyCode(keyCode, scanCode));
            }

            edit = false;
            KeyBind.updateBoundKeys();
            return true;
        } else {
            if(keyCode == InputUtil.KEY_RIGHT_CODE && bindButton.isFocused()){
                resetButton.setFocused(true);
                bindButton.setFocused(false);
            } else if (keyCode == InputUtil.KEY_LEFT_CODE && resetButton.isFocused()){
                bindButton.setFocused(true);
                resetButton.setFocused(false);
            }
            if(keyCode == InputUtil.KEY_TAB_CODE){
                resetButton.setFocused(false);
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void unfocus() {
        super.unfocus();
        bindButton.unfocus();
        resetButton.unfocus();
    }

    @Override
    public boolean isHoveredOrFocused() {
        return bindButton.isHoveredOrFocused() || resetButton.isHoveredOrFocused();
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return bindButton.changeFocus(lookForwards);
    }

    @Override
    public boolean isFocused() {
        return bindButton.isFocused() || resetButton.isFocused();
    }
}
