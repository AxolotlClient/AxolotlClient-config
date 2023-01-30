package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlClientConfig.options.KeyBindOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KeyBindWidget extends OptionWidget {

    private final KeyBindOption option;

    private final OptionWidget bindButton;
    private final OptionWidget resetButton;

    private final KeyBinding key;

    private boolean edit;

    public KeyBindWidget(int x, int y, int width, int height, KeyBindOption option) {
        super(x, y, width, height, LiteralText.EMPTY, (button) -> {});
        this.option = option;
        key = option.get();

        Text text = new TranslatableText(option.get().getBoundKeyTranslationKey());
        this.bindButton = new OptionWidget(0, 0, 100, 20, text, button -> edit = true) {
            protected MutableText getNarrationMessage() {
                return option.get().isUnbound()
                        ? new TranslatableText("narrator.controls.unbound", text)
                        : new TranslatableText("narrator.controls.bound", text, super.getNarrationMessage());
            }
        };
        this.resetButton = new OptionWidget(0, 0, 50, 20, new TranslatableText("controls.reset"), button -> {
            MinecraftClient.getInstance().options.setKeyCode(option.get(), option.get().getDefaultKey());
            KeyBinding.updateKeysByCode();
            keyPressed(GLFW.GLFW_KEY_LEFT, 0, 0);
        }) {
            protected MutableText getNarrationMessage() {
                return new TranslatableText("narrator.controls.reset", text);
            }
        };
    }

    @Override
    public void onPress() {
        if(resetButton.isHovered()){
            resetButton.onPress();
        } else if (bindButton.isHovered()) {
            bindButton.onPress();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        if(resetButton.isHovered() && bindButton.isHovered()){
            resetButton.unfocus();
        }

        this.resetButton.x = x + 100;
        this.resetButton.y = y;
        this.resetButton.active = !option.get().isDefault();
        this.resetButton.render(matrices, mouseX, mouseY, tickDelta);
        this.bindButton.x = (x);
        this.bindButton.y = (y);
        this.bindButton.setMessage(this.key.getBoundKeyLocalizedText());
        boolean bl2 = false;
        if (!this.key.isUnbound()) {
            List<KeyBinding> binds = KeyBindOption.getBindings();
            binds.addAll(Arrays.stream(MinecraftClient.getInstance().options.keysAll).collect(Collectors.toList()));
            for(KeyBinding keyBind : binds) {
                if (keyBind != this.key && this.key.equals(keyBind)) {
                    bl2 = true;
                    break;
                }
            }
        }

        if (edit) {
            this.bindButton
                    .setMessage(
                            new LiteralText("> ").append(this.bindButton.getMessage().copy().formatted(Formatting.YELLOW)).append(" <").formatted(Formatting.YELLOW)
                    );
        } else if (bl2) {
            this.bindButton.setMessage(this.bindButton.getMessage().copy().formatted(Formatting.RED));
        }

        this.bindButton.render(matrices, mouseX, mouseY, tickDelta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(edit){
			MinecraftClient.getInstance().options.setKeyCode(key, InputUtil.Type.MOUSE.createFromCode(button));
			edit = false;
			KeyBinding.updateKeysByCode();
			return true;
		} else if (this.bindButton.mouseClicked(mouseX, mouseY, button)) {
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
            KeyBinding.updateKeysByCode();
            return true;
        } else {
            if(keyCode == GLFW.GLFW_KEY_RIGHT && bindButton.isFocused()){
                resetButton.setFocused(true);
                bindButton.setFocused(false);
            } else if (keyCode == GLFW.GLFW_KEY_LEFT && resetButton.isFocused()){
                bindButton.setFocused(true);
                resetButton.setFocused(false);
            }
            if(keyCode == GLFW.GLFW_KEY_TAB){
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
    public boolean isHovered() {
        return bindButton.isHovered() || resetButton.isHovered();
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return bindButton.changeFocus(lookForwards);
    }

    @Override
    public boolean isFocused() {
        return bindButton.isFocused() || resetButton.isFocused();
    }

	@Override
	protected MutableText getNarrationMessage() {
		return new LiteralText(option.getTranslatedName()).append(super.getNarrationMessage());
	}
}
