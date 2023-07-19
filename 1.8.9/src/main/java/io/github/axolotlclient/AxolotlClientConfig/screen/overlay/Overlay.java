package io.github.axolotlclient.AxolotlClientConfig.screen.overlay;

import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.common.util.Rectangle;
import io.github.axolotlclient.AxolotlClientConfig.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;

public class Overlay extends ButtonWidget {

    protected Window window;
    protected Rectangle overlay;

    public Overlay(String title) {
        super(0, 100, 50, I18n.translate(title));
    }

    public void init() {
        window = new Window(MinecraftClient.getInstance());
        width=window.getWidth()-200;
        height=window.getHeight()-100;
        overlay = new Rectangle(100, 50, width, height);
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {

		DrawUtil.getInstance().drawRoundedRect(overlay, Color.DARK_GRAY.withAlpha(127).getAsInt(), 12);
		DrawUtil.getInstance().outlineRoundedRect(overlay, Color.WHITE.getAsInt(), 12);

		drawCenteredString(MinecraftClient.getInstance().textRenderer, message, window.getWidth() / 2, 54, -1);

	}

    public void tick(){

    }

    public void onClick(int mouseX, int mouseY){

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {

    }

    public void keyPressed(char c, int code){

    }
}
