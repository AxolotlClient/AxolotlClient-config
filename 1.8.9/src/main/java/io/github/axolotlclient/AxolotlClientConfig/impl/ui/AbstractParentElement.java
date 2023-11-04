package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import net.minecraft.client.gui.DrawableHelper;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractParentElement extends DrawableHelper implements ParentElement {
	@Nullable
	private Element focused;
	private boolean dragging;

	public AbstractParentElement() {
	}

	@Override
	public final boolean isDragging() {
		return this.dragging;
	}

	@Override
	public final void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	@Nullable
	@Override
	public Element getFocused() {
		return this.focused;
	}

	public void setFocusedChild(@Nullable Element child) {
		if (this.focused != null) {
			this.focused.setFocused(false);
		}

		if (child != null) {
			child.setFocused(true);
		}

		this.focused = child;
	}
}
