package io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.screen;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.AbstractScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.BooleanWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.IntegerWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class ColorSelectionScreen extends AbstractScreen {
	private final ColorOption option;
	private BooleanOption chroma;
	private IntegerOption alpha;

	private final Identifier texture = new Identifier("axolotlclientconfig", "textures/gui/colorwheel.png");

	private int selectorRadius;
	private float selectorX;
	private float selectorY;
	private int buttonsX;
	public ColorSelectionScreen(AbstractScreen parent, ColorOption option) {
		super(I18n.translate("select_color"), parent, parent.getConfigName());
		this.option = option;
	}

	@Override
	public void init() {
		super.init();
		addDrawableChild(new VanillaButtonWidget(width/2-75, height-40, 150, 20, I18n.translate("gui.back"),
			button -> MinecraftClient.getInstance().setScreen(parent)));

		chroma = new BooleanOption("option.chroma", option.get().isChroma(), val -> option.get().setChroma(val));
		alpha = new IntegerOption("option.alpha", option.get().getAlpha(), val -> option.get().setAlpha(val), 0, 255);

		selectorRadius = Math.max(Math.min(width/4-10, (height)/2-60), 75) ;
		selectorX = width/4f-selectorRadius;
		selectorY = height/2f-selectorRadius;

		buttonsX = (int) Math.max(width/2f+25, selectorX+selectorRadius*2 + 10);
		addDrawableChild(new BooleanWidget(buttonsX, 120, 150, 20, chroma));
		addDrawableChild(new IntegerWidget(buttonsX, 165, 150, 20, alpha));
	}

	@Override
	public void render(long ctx, double mouseX, double mouseY, float delta) {
		drawCenteredString(client.textRenderer, title, width/2, 20, Colors.WHITE.toInt());

		MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
		DrawableHelper.drawTexture((int) selectorX, (int) selectorY, 0, 0, selectorRadius*2, selectorRadius*2, selectorRadius*2, selectorRadius*2);

		DrawUtil.outlineRect((int) selectorX, (int) selectorY, selectorRadius*2, selectorRadius*2, Colors.BLACK.toInt());

		client.textRenderer.draw(I18n.translate("option.current"), buttonsX, 40, Colors.WHITE.toInt());

		DrawUtil.fillRect(buttonsX, 55, 150, 40, option.get().get().toInt());
		DrawUtil.outlineRect(buttonsX, 55, 150, 40, Colors.BLACK.toInt());

		client.textRenderer.draw(I18n.translate("option.chroma"), buttonsX, 105, Colors.WHITE.toInt());
		client.textRenderer.draw(I18n.translate("option.alpha"), buttonsX, 150, Colors.WHITE.toInt());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (button == 0) {
			if (mouseX >= selectorX && mouseX <= selectorX + selectorRadius*2 &&
				mouseY >= selectorY && mouseY <= selectorY + selectorRadius*2) {

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
		Window window = new Window(MinecraftClient.getInstance());
		return (int) (x * window.getScaleFactor());
	}

	private int toGlCoordsY(double y) {
		Window window = new Window(MinecraftClient.getInstance());
		int scale = window.getScaleFactor();
		return Math.round((float)(MinecraftClient.getInstance().height - y * scale - scale));
	}
}
