package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlClientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlClientConfig.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

public class EnumOptionWidget extends OptionWidget {

    private final EnumOption option;

    public EnumOptionWidget(int id, int x, int y, EnumOption option) {
        super(id, x, y, 150, 20, I18n.translate(option.get()));
        this.option = option;
    }

    @Override
    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
        if (canHover()) {
            return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        }
        return false;
    }

    protected boolean canHover() {
        if (MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isOverlayOpen()) {
            this.hovered = false;
            return false;
        }
        return true;
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 1) {
            this.message = I18n.translate(option.last());
        } else {
            this.message = I18n.translate(option.next());
        }
    }

    @Override
    protected int getYImage(boolean isHovered) {
        if (canHover()) {
            return super.getYImage(isHovered);
        }
        return 1;
    }
}
