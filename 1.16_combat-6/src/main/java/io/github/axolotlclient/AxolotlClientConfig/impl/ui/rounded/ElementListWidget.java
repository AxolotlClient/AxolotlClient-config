package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded;

import java.util.List;

import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Selectable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import org.jetbrains.annotations.Nullable;

public abstract class ElementListWidget<E extends ElementListWidget.Entry<E>> extends EntryListWidget<E> {
	private boolean widgetFocused;

	public ElementListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	public boolean changeFocus(boolean lookForwards) {
		this.widgetFocused = super.changeFocus(lookForwards);
		if (this.widgetFocused) {
			this.ensureVisible(this.getFocused());
		}

		return this.widgetFocused;
	}

	public SelectionType getType() {
		return this.widgetFocused ? SelectionType.FOCUSED : super.getType();
	}

	protected boolean isSelectedEntry(int index) {
		return false;
	}

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
		public void setFocused(@Nullable Element focused) {
			this.focused = focused;
		}

		@Nullable
		@Override
		public Element getFocused() {
			return this.focused;
		}
	}
}
