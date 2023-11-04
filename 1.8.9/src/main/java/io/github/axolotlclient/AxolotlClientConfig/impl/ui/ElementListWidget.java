package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class ElementListWidget<E extends ElementListWidget.Entry<E>> extends EntryListWidget<E> {
	public ElementListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		super(client, width, height, top, bottom, itemHeight);
	}

	@Override
	public void setFocusedChild(@Nullable Element child) {
		super.setFocusedChild(child);
		if (child == null) {
			this.setSelected(null);
		}
	}

	@Override
	public SelectionType getType() {
		return this.isFocused() ? SelectionType.FOCUSED : super.getType();
	}

	@Override
	protected boolean isSelectedEntry(int index) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry<E extends ElementListWidget.Entry<E>>
		extends EntryListWidget.Entry<E>
		implements ParentElement {
		@Nullable
		private Element focused;
		private boolean dragging;

		public Entry() {
		}

		@Override
		public boolean isDragging() {
			return this.dragging;
		}

		@Override
		public void setDragging(boolean dragging) {
			this.dragging = dragging;
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			return ParentElement.super.mouseClicked(mouseX, mouseY, button);
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

		@Nullable
		@Override
		public Element getFocused() {
			return this.focused;
		}
	}
}
