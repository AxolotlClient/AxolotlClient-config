package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.mojang.blaze3d.glfw.Window;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.AbstractScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.BooleanWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.IntegerWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.nanovg.NanoVG.*;

public class ColorSelectionScreen extends AbstractScreen {
	private NVGPaint paint;
	private final ColorOption option;
	private BooleanOption chroma;
	private IntegerOption alpha;

	private int selectorRadius;
	private float selectorX;
	private float selectorY;
	private int buttonsX;
	public ColorSelectionScreen(AbstractScreen parent, ColorOption option) {
		super(I18n.translate("select_color"), parent, parent.getConfigName());
		this.option = option;
		enableVanillaRendering = false;
	}

	@Override
	public void init() {
		super.init();
		addDrawableChild(new RoundedButtonWidget(width/2-75, height-40, 150, 20, I18n.translate("gui.back"),
			button -> MinecraftClient.getInstance().setScreen(parent)));

		chroma = new BooleanOption("option.chroma", option.get().isChroma(), val -> option.get().setChroma(val));
		alpha = new IntegerOption("option.alpha", option.get().getAlpha(), val -> option.get().setAlpha(val), 0, 255);

		selectorRadius = Math.max(Math.min(width/4-10, (height)/2-60), 75) ;
		selectorX = width/4f-selectorRadius;//width/2f-selectorRadius*2;
		selectorY = height/2f-selectorRadius;//height/2f - selectorRadius;

		buttonsX = (int) Math.max(width/2f+25, selectorX+selectorRadius*2 + 10);
		addDrawableChild(new BooleanWidget(buttonsX, 120, 150, 20, chroma));
		addDrawableChild(new IntegerWidget(buttonsX, 165, 150, 20, alpha));
	}

	@Override
	public void render(long ctx, double mouseX, double mouseY, float delta) {
		drawCenteredString(ctx, RoundedConfigScreen.font, title, width/2f, 20, Colors.WHITE);

		if (paint == null || paint.address() == 0) {
			int image = DrawUtil.nvgCreateImage(ctx, new Identifier("axolotlclientconfig", "textures/gui/colorwheel.png"));
			paint = nvgImagePattern(ctx, selectorX, selectorY, selectorRadius*2, selectorRadius*2, 0, image, 1, NVGPaint.create());
		}

		nvgBeginPath(ctx);
		nvgRoundedRect(ctx, selectorX, selectorY, selectorRadius*2, selectorRadius*2, selectorRadius);
		nvgFillPaint(ctx, paint);
		nvgFill(ctx);

		nvgBeginPath(ctx);
		nvgRoundedRect(ctx, selectorX, selectorY, selectorRadius*2, selectorRadius*2, selectorRadius);
		nvgStrokeColor(ctx, Colors.BLACK.toNVG());
		nvgStrokeWidth(ctx, 1);
		nvgStroke(ctx);

		drawString(ctx, RoundedConfigScreen.font, I18n.translate("option.current"), buttonsX, 40, Colors.WHITE);

		fillRoundedRect(ctx, buttonsX, 55, 150, 40, option.get().get(), 10);
		outlineRoundedRect(ctx, buttonsX, 55, 150, 40, Colors.BLACK, 10, 1);

		drawString(ctx, RoundedConfigScreen.font, I18n.translate("option.chroma"), buttonsX, 105, Colors.WHITE);
		drawString(ctx, RoundedConfigScreen.font, I18n.translate("option.alpha"), buttonsX, 150, Colors.WHITE);
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
		return Math.round((float)(window.getHeight() - y * scale - scale));
	}

	@Override
	public void resize(MinecraftClient minecraftClient, int i, int j) {
		super.resize(minecraftClient, i, j);
		if (paint != null){
			paint=null;
		}
	}
}
