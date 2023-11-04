package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.util.List;

import com.google.common.collect.Lists;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.screen.ConfigScreen;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.opengl.GL11;

public abstract class AbstractScreen extends Screen implements ParentElement, ConfigScreen {

	private final List<Drawable> drawables = Lists.newArrayList();
	protected final String title;
	protected final Screen parent;

	protected MinecraftClient client;

	@Getter
	private final String configName;

	protected boolean enableNanoVGRendering = true;
	protected boolean enableVanillaRendering = true;

	public AbstractScreen(String title, Screen parent, String configName){
		super(new LiteralText(title));
		this.title = title;
		this.parent = parent;
		this.configName = configName;
	}

	@Override
	public void init() {
		client = MinecraftClient.getInstance();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MatrixStackProvider.getInstance().setStack(matrices);

		renderBackground(matrices);

		if (enableNanoVGRendering) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			NVGMC.wrap(ctx -> {
				render(ctx, mouseX, mouseY, delta);

				drawables.forEach(drawable -> drawable.render(ctx, mouseX, mouseY, delta));
			});
			GL11.glPopAttrib();
		}

		if (enableVanillaRendering) {
			drawables.forEach(drawable -> drawable.render(mouseX, mouseY, delta));
		}
	}

	protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
		this.drawables.add(drawableElement);
		return this.addChild(drawableElement);
	}

	@Override
	public void removed(){
		save();
	}

	public void save(){
		AxolotlClientConfig.getInstance().getConfigManager(getConfigName()).save();
	}

	@Override
	public void tick() {

	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return ParentElement.super.mouseClicked(mouseX, mouseY, button);
	}
}
