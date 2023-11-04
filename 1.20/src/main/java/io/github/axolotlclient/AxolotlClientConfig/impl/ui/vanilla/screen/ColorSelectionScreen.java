package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.screen;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.mojang.blaze3d.glfw.Window;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class ColorSelectionScreen extends Screen {
	private final ColorOption option;
	private final Identifier texture = new Identifier("axolotlclientconfig", "textures/gui/colorwheel.png");
	private final Screen parent;
	private BooleanOption chroma;
	private IntegerOption alpha;
	private int selectorRadius;
	private float selectorX;
	private float selectorY;
	private int buttonsX;

	public ColorSelectionScreen(Screen parent, ColorOption option) {
		super(Text.translatable("select_color"));
		this.option = option;
		this.parent = parent;
	}

	@Override
	public void init() {
		super.init();
		addDrawableSelectableElement(ButtonWidget.builder(CommonTexts.BACK, buttonWidget -> closeScreen())
			.position(width / 2 - 75, height - 40).build());

		selectorRadius = Math.max(Math.min(width / 4 - 10, (height) / 2 - 60), 75);
		selectorX = width / 4f - selectorRadius;
		selectorY = height / 2f - selectorRadius;

		buttonsX = (int) Math.max(width / 2f + 25, selectorX + selectorRadius * 2 + 10);

		if (this.height - 250 > 0) {
			TextFieldWidget text = new TextFieldWidget(client.textRenderer, buttonsX, 190, 150, 20, Text.empty());
			text.setChangedListener(s -> {
				try {
					option.set(Color.parse(s));

					children().forEach(e -> {
						if (e instanceof Updatable) {
							((Updatable) e).update();
						}
					});
				} catch (Throwable ignored) {
				}
			});
			text.setText(option.get().toString().split(";")[0]);
			addDrawableSelectableElement(text);
		}

		chroma = new BooleanOption("option.chroma", option.get().isChroma(), val -> {
			option.get().setChroma(val);
			children().forEach(e -> {
				if (e instanceof TextFieldWidget) {
					((TextFieldWidget) e).setText(option.get().toString().split(";")[0]);
				}
			});
		});
		alpha = new IntegerOption("option.alpha", option.get().getAlpha(), val -> {
			option.get().setAlpha(val);
			children().forEach(e -> {
				if (e instanceof TextFieldWidget) {
					((TextFieldWidget) e).setText(option.get().toString().split(";")[0]);
				}
			});
		}, 0, 255);

		addDrawableSelectableElement(ConfigStyles.createWidget(buttonsX, 120, 150, 20, chroma));
		addDrawableSelectableElement(ConfigStyles.createWidget(buttonsX, 165, 150, 20, alpha));
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.render(graphics, mouseX, mouseY, delta);
		graphics.drawCenteredShadowedText(client.textRenderer, title, width / 2, 20, Colors.WHITE.toInt());

		graphics.drawTexture(texture, (int) selectorX, (int) selectorY, 0, 0, selectorRadius * 2, selectorRadius * 2, selectorRadius * 2, selectorRadius * 2);

		DrawUtil.outlineRect(graphics, (int) selectorX, (int) selectorY, selectorRadius * 2, selectorRadius * 2, Colors.BLACK.toInt());

		graphics.drawShadowedText(client.textRenderer, I18n.translate("option.current"), buttonsX, 40, Colors.WHITE.toInt());

		DrawUtil.fillRect(graphics, buttonsX, 55, 150, 40, option.get().get().toInt());
		DrawUtil.outlineRect(graphics, buttonsX, 55, 150, 40, Colors.BLACK.toInt());

		graphics.drawShadowedText(client.textRenderer, I18n.translate("option.chroma"), buttonsX, 105, Colors.WHITE.toInt());
		graphics.drawShadowedText(client.textRenderer, I18n.translate("option.alpha"), buttonsX, 150, Colors.WHITE.toInt());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (button == 0) {
			if (mouseX >= selectorX && mouseX <= selectorX + selectorRadius * 2 &&
				mouseY >= selectorY && mouseY <= selectorY + selectorRadius * 2) {

				final ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(4);
				pixelBuffer.order(ByteOrder.nativeOrder());

				GL11.glReadPixels(toGlCoordsX(mouseX), toGlCoordsY(mouseY),
					1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);

				final int r = pixelBuffer.get(0) & 0xff;
				final int g = pixelBuffer.get(1) & 0xff;
				final int b = pixelBuffer.get(2) & 0xff;
				option.get().setRed(r);
				option.get().setGreen(g);
				option.get().setBlue(b);
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	private int toGlCoordsX(double x) {
		Window window = MinecraftClient.getInstance().getWindow();
		return (int) (x * window.getScaleFactor());
	}

	private int toGlCoordsY(double y) {
		Window window = MinecraftClient.getInstance().getWindow();
		double scale = window.getScaleFactor();
		return Math.round((float) (window.getHeight() - y * scale - scale));
	}

	@Override
	public void closeScreen() {
		client.setScreen(parent);
	}
}
