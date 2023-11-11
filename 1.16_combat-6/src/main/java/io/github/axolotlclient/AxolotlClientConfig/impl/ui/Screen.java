package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.util.List;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public abstract class Screen extends net.minecraft.client.gui.screen.Screen {

	protected final String title;
	private final List<Drawable> drawables = Lists.newArrayList();
	protected MinecraftClient client;

	public Screen(String title) {
		super(new TranslatableText(title));
		this.title = title;
	}

	@Override
	public void init() {
		client = MinecraftClient.getInstance();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		drawables.forEach(drawable -> drawable.render(matrices, mouseX, mouseY, delta));
	}

	protected <T extends Element & net.minecraft.client.gui.Drawable> T addDrawableChild(T drawableElement) {
		this.drawables.add(drawableElement);
		return this.addChild(drawableElement);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		drawables.clear();
		super.resize(client, width, height);
	}
}
