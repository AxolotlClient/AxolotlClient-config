/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sound.instance.SimpleSoundInstance;
import net.minecraft.client.sound.system.SoundManager;
import net.minecraft.resource.Identifier;

public abstract class ClickableWidget extends DrawUtil implements Drawable, Element, Widget, Selectable {

	protected static final Identifier WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");
	public boolean active = true;
	public boolean visible = true;
	@Getter
	protected boolean hovered;
	protected Minecraft client;
	@Getter
	@Setter
	private int x, y, width, height;
	private boolean focused;
	@Getter
	@Setter
	private String message;

	public ClickableWidget(int x, int y, int width, int height, String message) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.message = message;
		client = Minecraft.getInstance();
	}

	@Override
	public boolean isFocused() {
		return focused;
	}

	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		if (this.visible) {
			this.hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
			this.drawWidget(mouseX, mouseY, delta);
		}
	}

	protected void drawWidget(int mouseX, int mouseY, float delta) {
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.active && this.visible) {
			if (this.isValidClickButton(button)) {
				boolean bl = this.clicked(mouseX, mouseY);
				if (bl) {
					this.playDownSound(Minecraft.getInstance().getSoundManager());
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
			&& mouseX >= (double) this.getX()
			&& mouseY >= (double) this.getY()
			&& mouseX < (double) (this.getX() + this.width)
			&& mouseY < (double) (this.getY() + this.height);
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
			&& mouseX >= (double) this.getX()
			&& mouseY >= (double) this.getY()
			&& mouseX < (double) (this.getX() + this.width)
			&& mouseY < (double) (this.getY() + this.height);
	}

	public void onClick(double mouseX, double mouseY) {
	}

	public void onRelease(double mouseX, double mouseY) {
	}

	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
	}

	public void playDownSound(SoundManager soundManager) {
		soundManager.play(SimpleSoundInstance.of(new Identifier("gui.button.press"), 1.0F));
	}

	@Override
	public SelectionType getType() {
		return hovered ? SelectionType.HOVERED : (isFocused() ? SelectionType.FOCUSED : SelectionType.NONE);
	}
}
