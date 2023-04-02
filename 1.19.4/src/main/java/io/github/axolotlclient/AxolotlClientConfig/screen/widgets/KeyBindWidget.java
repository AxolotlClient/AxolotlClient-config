package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlClientConfig.options.KeyBindOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class KeyBindWidget extends ButtonWidget implements OptionWidget, ParentElement {

    private final KeyBindOption option;

    private final ButtonWidget bindButton;
    private final ButtonWidget resetButton;

    private Element focused;

    private final KeyBind key;

    private boolean edit;
    private boolean conflicting;

    public KeyBindWidget(int x, int y, int width, int height, KeyBindOption option) {
        super(x, y, width, height, Text.empty(), (button) -> {}, DEFAULT_NARRATION);
        this.option = option;
        key = option.get();

        Text text = Text.translatable(option.get().getKeyTranslationKey());
        this.bindButton = new ButtonWidget(0, 0, 100, 20, text, button -> edit = true, DEFAULT_NARRATION) {
            protected MutableText getNarrationMessage() {
                return option.get().isUnbound()
                        ? Text.translatable("narrator.controls.unbound", text)
                        : Text.translatable("narrator.controls.bound", text, super.getNarrationMessage());
            }

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                if (edit) {
                    System.out.println("Receiving input " + keyCode);
                    if (keyCode == 256) {
                        MinecraftClient.getInstance().options.setKeyCode(key, InputUtil.UNKNOWN_KEY);
                    } else {
                        MinecraftClient.getInstance().options.setKeyCode(key, InputUtil.fromKeyCode(keyCode, scanCode));
                    }

                    edit = false;
                    KeyBind.updateBoundKeys();
                    update();
                    unfocus();
                    return true;
                }
                boolean bl = super.keyPressed(keyCode, scanCode, modifiers);
                update();
                return bl;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if(edit) {
                    MinecraftClient.getInstance().options.setKeyCode(key, InputUtil.Type.MOUSE.createFromKeyCode(button));
                    edit = false;
                    KeyBind.updateBoundKeys();
                    update();
                    unfocus();
                    return true;
                }
                boolean bl = super.mouseClicked(mouseX, mouseY, button);
                update();
                return bl;
            }
        };
        this.resetButton = new ButtonWidget(0, 0, 50, 20, Text.translatable("controls.reset"), button -> {
            MinecraftClient.getInstance().options.setKeyCode(option.get(), option.get().getDefaultKey());
            KeyBind.updateBoundKeys();
            update();
        }, DEFAULT_NARRATION) {
            protected MutableText getNarrationMessage() {
                return Text.translatable("narrator.controls.reset", text);
            }
        };

        update();
    }

    private void update(){
        this.bindButton.setMessage(this.key.getKeyName());
        this.resetButton.active = !this.key.isDefault();
        this.conflicting = false;
        MutableText text = Text.empty();
        Set<KeyBind> conflicts = new HashSet<>();
        if (!this.key.isUnbound()) {
            List<KeyBind> binds = KeyBindOption.getBindings();
            binds.addAll(Arrays.stream(MinecraftClient.getInstance().options.allKeys).toList());
            for(KeyBind keyBind : binds) {
                if (keyBind != this.key && this.key.keyEquals(keyBind)) {
                    boolean alreadyConflicting = conflicts.contains(keyBind);
                    if (this.conflicting && !alreadyConflicting) {
                        text.append(", ");
                    }

                    this.conflicting = true;
                    if(!alreadyConflicting) {
                        text.append(Text.translatable(keyBind.getTranslationKey()));
                        conflicts.add(keyBind);
                    }
                }
            }
        }

        if (this.conflicting) {
            this.bindButton.setMessage(Text.literal("[ ").append(this.bindButton.getMessage().copy().formatted(Formatting.WHITE)).append(" ]").formatted(Formatting.RED));
            this.bindButton.setTooltip(Tooltip.create(Text.translatable("controls.keybinds.duplicateKeybinds", text)));
        } else {
            this.bindButton.setTooltip(null);
        }

        if (edit) {
            this.bindButton.setMessage(Text.literal("> ").append(this.bindButton.getMessage().copy().formatted(Formatting.WHITE, Formatting.UNDERLINE)).append(" <").formatted(Formatting.YELLOW));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        if(resetButton.isHoveredOrFocused() && bindButton.isHoveredOrFocused()){
            resetButton.setFocused(false);
        }

        this.resetButton.setX(getX() + 100);
        this.resetButton.setY(getY());
        this.resetButton.render(matrices, mouseX, mouseY, tickDelta);
        this.bindButton.setX(getX());
        this.bindButton.setY(getY());
        if (this.conflicting) {
            int x = this.bindButton.getX() - 6;
            DrawableHelper.fill(matrices, x, getY()+ 2, x + 3, getY() + getHeight() - 3, Formatting.RED.getColorValue() | -16777216);
        }

        this.bindButton.render(matrices, mouseX, mouseY, tickDelta);
    }

    @Override
    public List<? extends Element> children() {
        return ImmutableList.of(this.bindButton, this.resetButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return ParentElement.super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean dragging) {

    }

    @Nullable
    @Override
    public Element getFocused() {
        return focused;
    }

    @Override
    public void setFocusedChild(@Nullable Element child) {
        if(focused != null) {
            focused.setFocused(false);
        }
        focused = child;
        if(child != null){
            child.setFocused(true);
        }
    }

    @Override
    public void unfocus() {
        children().forEach(e -> e.setFocused(false));
        setFocused(false);
    }

    @Override
    public boolean isHoveredOrFocused() {
        return canHover() && (bindButton.isHoveredOrFocused() || resetButton.isHoveredOrFocused());
    }

    @Override
    public boolean isFocused() {
        return ParentElement.super.isFocused();
    }

	@Override
	protected MutableText getNarrationMessage() {
		return Text.literal(option.getTranslatedName()).append(super.getNarrationMessage());
	}

    @Override
    public List<? extends Selectable> selectableChildren() {
        return ImmutableList.of(bindButton, resetButton);
    }
}
