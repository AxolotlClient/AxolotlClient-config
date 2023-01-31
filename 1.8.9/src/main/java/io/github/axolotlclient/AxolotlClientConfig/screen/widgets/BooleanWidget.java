package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;

public class BooleanWidget extends OptionWidget {

    public final BooleanOption option;

    public BooleanWidget(int id, int x, int y, int width, int height, BooleanOption option) {
        super(id, x, y, width, height, "");
        this.active=true;
        this.option=option;
    }

    public void updateMessage(){
        this.message = option.get()? I18n.translate ("options."+"on"): I18n.translate ("options."+"off");
    }

    public void mouseClicked(int mouseX, int mouseY, int button){
        if(!option.getForceDisabled()){
            option.toggle();
        }
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {

        TextRenderer textRenderer = client.textRenderer;
        client.getTextureManager().bindTexture(WIDGETS_LOCATION);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.hovered = isMouseOver(client, mouseX, mouseY);

        renderBg();
        if(!option.getForceDisabled()) {
            renderSwitch();
        }

        int color = option.get()? 0x55FF55 : 0xFF5555;
        updateMessage();

        this.drawCenteredString(textRenderer, this.message, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);

    }

    private void renderSwitch(){
        int x = option.get() ? this.x + width - 8: this.x;
        this.drawTexture(x, this.y, 0, 66 + (hovered ? 20:0), 4, this.height/2);
        this.drawTexture(x, this.y + height/2, 0, 86 - height/2 + (hovered ? 20:0), 4, this.height/2);
        this.drawTexture(x + 4, this.y, 200 - 4, 66 + (hovered ? 20:0), 4, this.height);
        this.drawTexture(x + 4, this.y + height/2, 200 - 4, 86 - height/2 + (hovered ? 20:0), 4, this.height/2);
    }

    private void renderBg(){
        this.drawTexture(this.x, this.y, 0, 46, this.width / 2, this.height/2);
        this.drawTexture(this.x, this.y + height/2, 0, 66 - height/2, this.width / 2, this.height/2);
        this.drawTexture(this.x + this.width / 2, this.y, 200 - this.width / 2, 46, this.width / 2, this.height);
        this.drawTexture(this.x + this.width / 2, this.y + height/2, 200 - this.width / 2, 66 - height/2, this.width / 2, this.height/2);
    }
}
