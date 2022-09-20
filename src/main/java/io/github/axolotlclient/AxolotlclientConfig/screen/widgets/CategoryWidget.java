package io.github.axolotlclient.AxolotlclientConfig.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.AxolotlClientConfigConfig;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.screen.OptionsScreenBuilder;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.NotNull;

public class CategoryWidget extends ButtonWidget {

    public OptionCategory category;

    public BooleanWidget enabledButton;

    public CategoryWidget(OptionCategory category, int x, int y, int width, int height) {
        super(0, x, y, width, 20, category.getTranslatedName() +"...");
        this.category=category;

        if (AxolotlClientConfigConfig.showQuickToggles.get()) {
            category.getOptions().forEach(option -> {
                if (option.getName().contains("enabled") && option instanceof BooleanOption) {
                    enabledButton = new BooleanWidget(930486, (x + width) - 35, y + 3, 30, height - 6, (BooleanOption) option);
                }
            });
        }

    }

    @Override
    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isPickerOpen()){
            this.hovered = false;
            return false;
        }

        if(enabledButton!=null && enabledButton.isMouseOver(client, mouseX, mouseY) ) {
            this.hovered = false;
            return true;
        }
        return super.isMouseOver(client, mouseX, mouseY);
    }

    @Override
    public void render(@NotNull MinecraftClient client, int mouseX, int mouseY) {
        if (this.visible) {
            TextRenderer textRenderer = client.textRenderer;
            client.getTextureManager().bindTexture(WIDGETS_LOCATION);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = super.isMouseOver(client, mouseX, mouseY)&& (enabledButton == null || !enabledButton.isMouseOver(client, mouseX, mouseY));
            int i = this.getYImage(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.drawTexture(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexture(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.renderBg(client, mouseX, mouseY);
            int j = 14737632;
            if (!this.active) {
                j = 10526880;
            } else if (this.hovered) {
                j = 16777120;
            }

            this.drawCenteredString(textRenderer, this.message, this.x + this.width / 2 - (enabledButton!=null?enabledButton.getWidth()/2:0), this.y + (this.height - 8) / 2, j);
        }

        if(enabledButton != null){
            enabledButton.y = y+2;
            enabledButton.render(client, mouseX, mouseY);
        }
    }

    public void mouseClicked(int mouseX, int mouseY){
        playDownSound(MinecraftClient.getInstance().getSoundManager());
        if(enabledButton!=null &&
                enabledButton.isHovered()) {
            enabledButton.mouseClicked(mouseX, mouseY, 0);
        } else {
            if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder) {
                MinecraftClient.getInstance().openScreen(new OptionsScreenBuilder(MinecraftClient.getInstance().currentScreen, category, ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).modid));
            }
        }
    }


}
