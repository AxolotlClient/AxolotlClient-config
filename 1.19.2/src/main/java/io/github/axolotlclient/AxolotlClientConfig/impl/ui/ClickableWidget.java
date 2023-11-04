package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public abstract class ClickableWidget extends DrawUtil implements Drawable, Element, Widget, Selectable {

	//protected static final Identifier WIDGETS_LOCATION = ;

	@Getter @Setter
	private int x, y, width, height;
	private boolean focused;
	@Getter @Setter
	private String message;

	@Getter
	protected boolean hovered;
	public boolean active = true;
	public boolean visible = true;

	protected MinecraftClient client;

	public ClickableWidget(int x, int y, int width, int height, String message){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.message = message;
		client = MinecraftClient.getInstance();
	}

	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	@Override
	public boolean isFocused() {
		return focused;
	}

	@Override
	public void render(long ctx, int mouseX, int mouseY, float delta) {
		if (this.visible) {
			this.hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
			this.drawWidget(ctx, mouseX, mouseY, delta);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		if (this.visible) {
			this.hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
			this.drawWidget(mouseX, mouseY, delta);
		}
	}

	protected void drawWidget(long ctx, int mouseX, int mouseY, float delta){}
	protected void drawWidget(int mouseX, int mouseY, float delta){}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.active && this.visible) {
			if (this.isValidClickButton(button)) {
				boolean bl = this.clicked(mouseX, mouseY);
				if (bl) {
					this.playDownSound(MinecraftClient.getInstance().getSoundManager());
					this.onClick(mouseX, mouseY);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.active
			&& this.visible
			&& mouseX >= (double)this.getX()
			&& mouseY >= (double)this.getY()
			&& mouseX < (double)(this.getX() + this.width)
			&& mouseY < (double)(this.getY() + this.height);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.isValidClickButton(button)) {
			this.onRelease(mouseX, mouseY);
			return true;
		} else {
			return false;
		}
	}

	protected boolean isValidClickButton(int button) {
		return button == 0;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.isValidClickButton(button)) {
			this.onDrag(mouseX, mouseY, deltaX, deltaY);
			return true;
		} else {
			return false;
		}
	}

	protected boolean clicked(double mouseX, double mouseY) {
		return this.active
			&& this.visible
			&& mouseX >= (double)this.getX()
			&& mouseY >= (double)this.getY()
			&& mouseX < (double)(this.getX() + this.width)
			&& mouseY < (double)(this.getY() + this.height);
	}

	public void onClick(double mouseX, double mouseY) {
	}

	public void onRelease(double mouseX, double mouseY) {
	}

	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
	}

	public void playDownSound(SoundManager soundManager) {
		soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	@Override
	public SelectionType getType() {
		return hovered ? SelectionType.HOVERED : (isFocused() ? SelectionType.FOCUSED : SelectionType.NONE);
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, getMessage());
	}
}
