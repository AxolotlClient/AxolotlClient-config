package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
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

	@ClientOnly
	public abstract static class Entry<E extends Entry<E>>
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

		public void setFocused(@Nullable net.minecraft.client.gui.Element child) {
			if (this.focused != null) {
				this.focused.setFocused(false);
			}

			if (child != null) {
				child.changeFocus(true);
			}

			this.focused = (Element) child;
		}

		@Nullable
		@Override
		public Element getFocused() {
			return this.focused;
		}
	}
}
