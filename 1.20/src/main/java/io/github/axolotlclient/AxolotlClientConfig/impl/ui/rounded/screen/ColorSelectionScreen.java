package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.mojang.blaze3d.glfw.Window;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.FloatOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.DrawingUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.NVGMC;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.TextFieldWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.nanovg.NanoVG.*;

public class ColorSelectionScreen extends Screen implements DrawingUtil {
	private final ColorOption option;
	private final Screen parent;
	private NVGPaint paint;
	private BooleanOption chroma;
	private FloatOption speed;
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
		addDrawableSelectableElement(new RoundedButtonWidget(width / 2 - 75, height - 40, Text.translatable("gui.back"),
			button -> MinecraftClient.getInstance().setScreen(parent)));

		chroma = new BooleanOption("option.chroma", option.get().isChroma(), val -> {
			option.get().setChroma(val);
		});
		speed = new FloatOption("option.speed", option.get().getChromaSpeed(), val -> {
			option.get().setChromaSpeed(val);
		}, 0f, 4f);
		alpha = new IntegerOption("option.alpha", option.get().getAlpha(), val -> {
			option.get().setAlpha(val);
			children().forEach(e -> {
				if (e instanceof TextFieldWidget) {
					((TextFieldWidget) e).setText(option.get().toString().split(";")[0]);
				}
			});
		}, 0, 255);

		selectorRadius = Math.max(Math.min(width / 4 - 10, (height) / 2 - 60), 75);
		selectorX = width / 4f - selectorRadius;//width/2f-selectorRadius*2;
		selectorY = height / 2f - selectorRadius;//height/2f - selectorRadius;

		buttonsX = (int) Math.max(width / 2f + 25, selectorX + selectorRadius * 2 + 10);

		int y = 120;
		addDrawableSelectableElement(ConfigStyles.createWidget(buttonsX, y, 150, 20, chroma));
		y += 45;
		if (height > 300) {
			addDrawableSelectableElement(ConfigStyles.createWidget(buttonsX, y, 150, 20, speed));
			y += 45;
		}
		addDrawableSelectableElement(ConfigStyles.createWidget(buttonsX, y, 150, 20, alpha));
		y += 45;
		if (this.height - 250 > 0) {
			y -= 25;
			TextFieldWidget text = new TextFieldWidget(client.textRenderer, buttonsX, y, 150, 20, Text.empty());
			text.setChangedListener(s -> {
				try {
					option.set(Color.parse(s));
					option.get().setChroma(chroma.get());
					option.get().setChromaSpeed(speed.get());

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
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		NVGMC.wrap(ctx -> {
			NVGHolder.setContext(ctx);
			super.render(graphics, mouseX, mouseY, delta);

			drawCenteredString(ctx, NVGHolder.getFont(), title.getString(), width / 2f, 20, Colors.WHITE);

			if (paint == null || paint.address() == 0) {
				int image = DrawUtil.nvgCreateImage(ctx, new Identifier("axolotlclientconfig", "textures/gui/colorwheel.png"));
				paint = nvgImagePattern(ctx, selectorX, selectorY, selectorRadius * 2, selectorRadius * 2, 0, image, 1, NVGPaint.create());
			}

			nvgBeginPath(ctx);
			nvgRoundedRect(ctx, selectorX, selectorY, selectorRadius * 2, selectorRadius * 2, selectorRadius);
			nvgFillPaint(ctx, paint);
			nvgFill(ctx);

			nvgBeginPath(ctx);
			nvgRoundedRect(ctx, selectorX, selectorY, selectorRadius * 2, selectorRadius * 2, selectorRadius);
			nvgStrokeColor(ctx, Colors.BLACK.toNVG());
			nvgStrokeWidth(ctx, 1);
			nvgStroke(ctx);

			drawString(ctx, NVGHolder.getFont(), I18n.translate("option.current"), buttonsX, 40, Colors.WHITE);

			fillRoundedRect(ctx, buttonsX, 55, 150, 40, option.get().get(), 10);
			outlineRoundedRect(ctx, buttonsX, 55, 150, 40, Colors.BLACK, 10, 1);

			int y = 105;
			drawString(ctx, NVGHolder.getFont(), I18n.translate("option.chroma"), buttonsX, y, Colors.WHITE);
			y += 45;
			if (height > 300) {
				drawString(ctx, NVGHolder.getFont(), I18n.translate("option.speed"), buttonsX, y, Colors.WHITE);
				y += 45;
			}
			drawString(ctx, NVGHolder.getFont(), I18n.translate("option.alpha"), buttonsX, y, Colors.WHITE);
		});
	}

	@Override
	public void renderBackground(GuiGraphics graphics, int i, int j, float f) {
		super.renderBackground(graphics, i, j, f);
		fillRoundedRect(NVGHolder.getContext(), 15, 15, width - 30, height - 30, Colors.DARK_GRAY, 12);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (button == 0) {
			double x = (selectorX + selectorRadius - mouseX);
			double y = (selectorY + selectorRadius - mouseY);
			if (Math.hypot(Math.abs(x), Math.abs(y)) <= selectorRadius) {

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
	public void resize(MinecraftClient minecraftClient, int i, int j) {
		super.resize(minecraftClient, i, j);
		if (paint != null) {
			paint = null;
		}
	}
}
