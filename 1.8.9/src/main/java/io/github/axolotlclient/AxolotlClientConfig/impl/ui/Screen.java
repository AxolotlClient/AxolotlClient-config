package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.util.List;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Mouse;

public abstract class Screen extends net.minecraft.client.gui.screen.Screen implements ParentElement {

	private final List<Drawable> drawables = Lists.newArrayList();
	private final List<Element> children = Lists.newArrayList();
	private final List<Selectable> selectables = Lists.newArrayList();

	private Element focused;
	private boolean dragging;

	@Getter
	protected final String title;

	private int lastMouseDragPosX, lastMouseDragPosY = -1;

	public Screen(String title){
		this.title = title;
	}

	@Override
	public void init(MinecraftClient minecraftClient, int i, int j) {
		clearChildren();
		client = MinecraftClient.getInstance();
		super.init(minecraftClient, i, j);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		renderBackground();


		drawables.forEach(drawable -> drawable.render(mouseX, mouseY, delta));
	}

	@Override
	public List<? extends Element> children() {
		return children;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return ParentElement.super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		mouseClicked((double) mouseX, mouseY, button);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		mouseReleased((double) mouseX, mouseY, button);
	}

	@Override
	protected void mouseDragged(int mouseX, int mouseY, int button, long lastClick) {
		if (lastMouseDragPosX == -1 || lastMouseDragPosY == -1){
			lastMouseDragPosX = mouseX;
			lastMouseDragPosY = mouseY;
		}
		mouseDragged(mouseX, mouseY, button, mouseX- lastMouseDragPosX, mouseY- lastMouseDragPosY);
		lastMouseDragPosX = mouseX;
		lastMouseDragPosY = mouseY;
	}

	protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
		this.drawables.add(drawableElement);
		return this.addSelectableChild(drawableElement);
	}

	protected <T extends Drawable> T addDrawable(T drawable) {
		this.drawables.add(drawable);
		return drawable;
	}

	protected <T extends Element & Selectable> T addSelectableChild(T child) {
		this.children.add(child);
		this.selectables.add(child);
		return child;
	}

	protected void remove(Element child) {
		if (child instanceof Drawable) {
			this.drawables.remove((Drawable)child);
		}

		if (child instanceof Selectable) {
			this.selectables.remove((Selectable)child);
		}

		this.children.remove(child);
	}

	protected void clearChildren() {
		this.drawables.clear();
		this.children.clear();
		this.selectables.clear();
	}

	@Override
	public @Nullable Element getFocused() {
		return focused;
	}

	@Override
	public void setFocusedChild(@Nullable Element child) {
		if (this.focused != null){
			focused.setFocused(false);
		}
		this.focused = child;
		if (this.focused != null){
			focused.setFocused(true);
		}
	}

	@Override
	public boolean isDragging() {
		return dragging;
	}

	@Override
	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	@Override
	protected void keyPressed(char c, int i) {
		if (!keyPressed(i, 0, 0)) {
			super.keyPressed(c, i);
		}
		charTyped(c, 0);
	}

	@Override
	public void tick() {
		int scroll = Mouse.getDWheel();
		if (scroll != 0) {
			//mouseScrolled(client.mouse.x, client.mouse.y, 0, scroll < 0 ? -1 : 1);
			children.forEach(e -> e.mouseScrolled(client.mouse.x, client.mouse.y, 0, scroll < 0 ? -1 : 1));
		}
	}
}
