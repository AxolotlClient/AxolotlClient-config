package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Rectangle;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@SuppressWarnings("deprecated")
public abstract class EntryListWidget<E extends EntryListWidget.Entry<E>> extends AbstractParentElement implements Drawable, Selectable {
	protected final MinecraftClient client;
	protected final int itemHeight;
	private final Entries children = new Entries();
	protected int width;
	protected int height;
	protected int top;
	protected int bottom;
	protected int right;
	protected int left;
	protected boolean centerListVertically = true;
	private double scrollAmount;
	private boolean renderHeader;
	protected int headerHeight;
	private boolean scrolling;
	@Nullable
	private E selected;
	private boolean renderBackground = true;
	@Nullable
	private E hoveredEntry;

	public EntryListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		this.client = client;
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.itemHeight = itemHeight;
		this.left = 0;
		this.right = width;
	}

	protected void setRenderHeader(boolean renderHeader, int headerHeight) {
		this.renderHeader = renderHeader;
		this.headerHeight = headerHeight;
		if (!renderHeader) {
			this.headerHeight = 0;
		}
	}

	public int getRowWidth() {
		return 220;
	}

	@Nullable
	public E getSelectedOrNull() {
		return this.selected;
	}

	public void setSelected(@Nullable E entry) {
		this.selected = entry;
	}

	public E getFirstChild() {
		return this.children.get(0);
	}

	public void setRenderBackground(boolean renderBackground) {
		this.renderBackground = renderBackground;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public E getFocused() {
		return (E) super.getFocused();
	}

	@Override
	public final List<E> children() {
		return this.children;
	}

	protected void clearEntries() {
		this.children.clear();
		this.selected = null;
	}

	protected void replaceEntries(Collection<E> newEntries) {
		this.clearEntries();
		this.children.addAll(newEntries);
	}

	protected E getEntry(int index) {
		return this.children().get(index);
	}

	protected int addEntry(E entry) {
		this.children.add(entry);
		return this.children.size() - 1;
	}

	protected void addEntryToTop(E entry) {
		double d = (double) this.getMaxScroll() - this.getScrollAmount();
		this.children.add(0, entry);
		this.setScrollAmount((double) this.getMaxScroll() - d);
	}

	protected boolean removeEntryFromTop(E entry) {
		double d = (double) this.getMaxScroll() - this.getScrollAmount();
		boolean bl = this.removeEntry(entry);
		this.setScrollAmount((double) this.getMaxScroll() - d);
		return bl;
	}

	protected int getEntryCount() {
		return this.children().size();
	}

	protected boolean isSelectedEntry(int index) {
		return Objects.equals(this.getSelectedOrNull(), this.children().get(index));
	}

	@Nullable
	protected final E getEntryAtPosition(double x, double y) {
		int k = this.left + this.width / 2 - this.getRowWidth() / 2;
		int l = this.left + this.width / 2 + this.getRowWidth() / 2;
		int m = MathHelper.floor(y - (double) this.top) - this.headerHeight + (int)this.scrollAmount - 4;
		int n = m / this.itemHeight;
		return x < this.getScrollbarPositionX() && x >= k && x <= l && n >= 0 && m >= 0 && n < this.getEntryCount()
			? children.get(n) : null;
	}

	public void updateSize(int width, int height, int top, int bottom) {
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.left = 0;
		this.right = width;
	}

	public void setLeftPos(int left) {
		this.left = left;
		this.right = left + this.width;
	}

	protected int getMaxPosition() {
		return this.getEntryCount() * this.itemHeight + this.headerHeight;
	}

	protected void clickedHeader(int x, int y) {
	}

	protected void renderHeader(long ctx, int x, int y) {
	}

	protected void renderDecorations(long ctx, double mouseX, double mouseY) {
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		render(0, mouseX, mouseY, delta);
	}

	@Override
	public void render(long ctx, int mouseX, int mouseY, float delta) {
		this.hoveredEntry = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;
		if (this.renderBackground) {
			RenderSystem.setShaderColor(0.125F, 0.125F, 0.125F, 1.0F);
			int i = 32;
			client.getTextureManager().bindTexture(Screen.OPTIONS_BACKGROUND_TEXTURE);
			DrawableHelper.drawTexture(MatrixStackProvider.getInstance().getStack(),
				this.left,
				this.top,
				(float) this.right,
				(float) (this.bottom + (int) this.getScrollAmount()),
				this.right - this.left,
				this.bottom - this.top,
				32,
				32
			);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}

		this.enableScissor(ctx);
		if (this.renderHeader) {
			int i = this.getRowLeft();
			int j = this.top + 4 - (int) this.getScrollAmount();
			this.renderHeader(ctx, i, j);
		}

		this.renderList(ctx, mouseX, mouseY, delta);
		disableScissor(ctx);
		/*if (this.renderBackground) {
			int i = 4;
			NVGColor end = new Color(-16777216).toNVG();
			NVGColor start = new Color(0).toNVG();
			NVGPaint topGradient = NanoVG.nvgLinearGradient(ctx, left, top, right, top + 4, end, start, NVGPaint.create());
			NVGPaint bottomGradient = NanoVG.nvgLinearGradient(ctx, left, bottom - 4, right, bottom, start, end, NVGPaint.create());
			NanoVG.nvgFillPaint(ctx, topGradient);
			NanoVG.nvgRect(ctx, left, top, right, top + 4);
			NanoVG.nvgFillPaint(ctx, bottomGradient);
			NanoVG.nvgRect(ctx, left, bottom - 4, right, bottom);
			NanoVG.nvgFill(ctx);
			//graphics.fillGradient(RenderLayer.getGuiOverlay(), this.left, this.top, this.right, this.top + 4, -16777216, 0, 0);
			//graphics.fillGradient(RenderLayer.getGuiOverlay(), this.left, this.bottom - 4, this.right, this.bottom, 0, -16777216, 0);
		}*/

		int k = this.getScrollbarPositionX();

		int p = this.getMaxScroll();
		if (p > 0) {
			int q = (this.bottom - this.top) * (this.bottom - this.top) / this.getMaxPosition();
			q = MathHelper.clamp(q, 32, this.bottom - this.top - 8);
			int r = (int) this.scrollAmount * (this.bottom - this.top - q) / p + this.top;
			if (r < this.top) {
				r = this.top;
			}
			if (ctx == 0){
				renderScrollbar(k, r, 6, q);
			} else {
				renderScrollbar(ctx, k, r, 6, q);
			}
		}

		this.renderDecorations(ctx, mouseX, mouseY);
		RenderSystem.disableBlend();
	}

	protected void renderScrollbar(long ctx, int x, int y, int width, int height) {}
	protected void renderScrollbar(int x, int y, int width, int height) {

		int l = x + width;
		RenderSystem.disableTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();

		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(x, this.bottom, 0.0).uv(0.0F, 1.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(l, this.bottom, 0.0).uv(1.0F, 1.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(l, this.top, 0.0).uv(1.0F, 0.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(x, this.top, 0.0).uv(0.0F, 0.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(x, (y + height), 0.0).uv(0.0F, 1.0F).color(128, 128, 128, 255).next();
		bufferBuilder.vertex(l, (y + height), 0.0).uv(1.0F, 1.0F).color(128, 128, 128, 255).next();
		bufferBuilder.vertex(l, y, 0.0).uv(1.0F, 0.0F).color(128, 128, 128, 255).next();
		bufferBuilder.vertex(x, y, 0.0).uv(0.0F, 0.0F).color(128, 128, 128, 255).next();
		bufferBuilder.vertex(x, (y + height - 1), 0.0).uv(0.0F, 1.0F).color(192, 192, 192, 255).next();
		bufferBuilder.vertex((l - 1), (y + height - 1), 0.0).uv(1.0F, 1.0F).color(192, 192, 192, 255).next();
		bufferBuilder.vertex((l - 1), y, 0.0).uv(1.0F, 0.0F).color(192, 192, 192, 255).next();
		bufferBuilder.vertex(x, y, 0.0).uv(0.0F, 0.0F).color(192, 192, 192, 255).next();
		tessellator.draw();
		RenderSystem.enableTexture();

	}

	protected void enableScissor(long ctx) {
		if (ctx == 0){
			DrawUtil.pushScissor(this.left, this.top, this.right - this.left, this.bottom - this.top);
		} else {
			pushScissor(ctx, this.left, this.top, this.right - this.left, this.bottom - this.top);
		}
	}

	protected void disableScissor(long ctx) {
		if (ctx == 0) {
			DrawUtil.popScissor();
		} else {
			popScissor(ctx);
		}
	}

	protected void centerScrollOn(E entry) {
		this.setScrollAmount( (this.children().indexOf(entry) * this.itemHeight + this.itemHeight / 2 - (this.bottom - this.top) / 2));
	}

	protected void ensureVisible(E entry) {
		int i = this.getRowTop(this.children().indexOf(entry));
		int j = i - this.top - 4 - this.itemHeight;
		if (j < 0) {
			this.scroll(j);
		}

		int k = this.bottom - i - this.itemHeight - this.itemHeight;
		if (k < 0) {
			this.scroll(-k);
		}
	}

	private void scroll(int amount) {
		this.setScrollAmount(this.getScrollAmount() + (double) amount);
	}

	public double getScrollAmount() {
		return this.scrollAmount;
	}

	public void setScrollAmount(double amount) {
		this.scrollAmount = MathHelper.clamp(amount, 0.0,  this.getMaxScroll());
	}

	public int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
	}

	public int getScrollBottom() {
		return (int) this.getScrollAmount() - this.height - this.headerHeight;
	}

	protected void updateScrollingState(double mouseX, double mouseY, int button) {
		this.scrolling = button == 0 && mouseX >= (double) this.getScrollbarPositionX() && mouseX < (double) (this.getScrollbarPositionX() + 6);
	}

	protected int getScrollbarPositionX() {
		return this.width / 2 + 124;
	}

	protected boolean isValidClickButton(int i) {
		return i == 0;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!this.isValidClickButton(button)) {
			return false;
		} else {
			this.updateScrollingState(mouseX, mouseY, button);
			if (!this.isMouseOver(mouseX, mouseY)) {
				return false;
			} else {
				E entry = this.getEntryAtPosition(mouseX, mouseY);
				if (entry != null) {
					if (entry.mouseClicked(mouseX, mouseY, button)) {
						E entry2 = this.getFocused();
						if (entry2 != entry && entry2 instanceof ParentElement) {
							((ParentElement) entry2).setFocused(null);
						}

						this.setFocusedChild(entry);
						this.setDragging(true);
						return true;
					} else {
						return this.scrolling;
					}
				} else {
					this.clickedHeader(
						(int) (mouseX - (double) (this.left + this.width / 2 - this.getRowWidth() / 2)),
						(int) (mouseY - (double) this.top) + (int) this.getScrollAmount() - 4
					);
					return true;
				}
			}
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.getFocused() != null) {
			this.getFocused().mouseReleased(mouseX, mouseY, button);
		}

		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
			return true;
		} else if (button == 0 && this.scrolling) {
			if (mouseY < (double) this.top) {
				this.setScrollAmount(0.0);
			} else if (mouseY > (double) this.bottom) {
				this.setScrollAmount(this.getMaxScroll());
			} else {
				double d = Math.max(1, this.getMaxScroll());
				int i = this.bottom - this.top;
				int j = MathHelper.clamp((int) ((float) (i * i) / (float) this.getMaxPosition()), 32, i - 8);
				double e = Math.max(1.0, d / (double) (i - j));
				this.setScrollAmount(this.getScrollAmount() + deltaY * e);
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double d) {
		this.setScrollAmount(this.getScrollAmount() - d * (double) this.itemHeight / 2.0*3);
		return true;
	}

	public void setFocusedChild(@Nullable Element child) {
		super.setFocusedChild(child);
		int i = this.children.indexOf(child);
		if (i >= 0) {
			E entry = this.children.get(i);
			this.setSelected(entry);
		}
	}

	@Nullable
	protected E nextEntry(NavigationDirection direction) {
		return this.nextEntry(direction, e -> true);
	}

	@Nullable
	protected E nextEntry(NavigationDirection direction, Predicate<E> predicate) {
		return this.nextEntry(direction, predicate, this.getSelectedOrNull());
	}

	@Nullable
	protected E nextEntry(NavigationDirection direction, Predicate<E> predicate, @Nullable E currentEntry) {
		int i;
		switch (direction) {
			case UP:
				i = -1;
			case DOWN:
				i = 1;
			default:
				i = 0;
		}
		if (!this.children().isEmpty() && i != 0) {
			int j;
			if (currentEntry == null) {
				j = i > 0 ? 0 : this.children().size() - 1;
			} else {
				j = this.children().indexOf(currentEntry) + i;
			}

			for (int k = j; k >= 0 && k < this.children.size(); k += i) {
				E entry = this.children().get(k);
				if (predicate.test(entry)) {
					return entry;
				}
			}
		}

		return null;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseY >= (double) this.top && mouseY <= (double) this.bottom && mouseX >= (double) this.left && mouseX <= (double) this.right;
	}

	protected void renderList(long ctx, int mouseX, int mouseY, float delta) {
		int i = this.getRowLeft();
		int j = this.getRowWidth();
		int k = this.itemHeight - 4;
		int l = this.getEntryCount();

		for (int m = 0; m < l; ++m) {
			int n = this.getRowTop(m);
			int o = this.getRowBottom(m);
			if (o >= this.top && n <= this.bottom) {
				this.renderEntry(ctx, mouseX, mouseY, delta, m, i, n, j, k);
			}
		}
	}

	protected void renderEntry(long ctx, int mouseX, int mouseY, float delta, int index, int x, int y, int width, int height) {
		E entry = this.getEntry(index);
		if (ctx == 0) {
			entry.drawBorder(index, y, x, width, height, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
		} else {
			entry.drawBorder(ctx, index, y, x, width, height, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
		}
		if (this.isSelectedEntry(index)) {
			int i = this.isFocused() ? -1 : -8355712;
			this.drawEntrySelectionHighlight(ctx, y, width, height, i, -16777216);
		}

		if (ctx == 0){
			entry.render(index, y, x, width, height, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
		} else {
			entry.render(ctx, index, y, x, width, height, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
		}
	}

	protected void drawEntrySelectionHighlight(long ctx, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
		int i = this.left + (this.width - entryWidth) / 2;
		int j = this.left + (this.width + entryWidth) / 2;
		fill(MatrixStackProvider.getInstance().getStack(), i, y - 2, j, y + entryHeight + 2, borderColor);
		fill(MatrixStackProvider.getInstance().getStack(), i + 1, y - 1, j - 1, y + entryHeight + 1, fillColor);
	}

	public int getRowLeft() {
		return this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
	}

	public int getRowRight() {
		return this.getRowLeft() + this.getRowWidth();
	}

	protected int getRowTop(int index) {
		return this.top + 4 - (int) this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
	}

	protected int getRowBottom(int index) {
		return this.getRowTop(index) + this.itemHeight;
	}

	@Override
	public SelectionType getType() {
		if (this.isFocused()) {
			return SelectionType.FOCUSED;
		} else {
			return this.hoveredEntry != null ? SelectionType.HOVERED : SelectionType.NONE;
		}
	}

	@Nullable
	protected E remove(int index) {
		E entry = this.children.get(index);
		return this.removeEntry(this.children.get(index)) ? entry : null;
	}

	protected boolean removeEntry(E entry) {
		boolean bl = this.children.remove(entry);
		if (bl && entry == this.getSelectedOrNull()) {
			this.setSelected(null);
		}

		return bl;
	}

	@Nullable
	protected E getHoveredEntry() {
		return this.hoveredEntry;
	}

	void setEntryParentList(Entry<E> entry) {
		entry.parentList = this;
	}

	public Rectangle getArea() {
		return new Rectangle(this.left, this.top, this.right - this.left, this.bottom - this.top);
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {

	}

	@ClientOnly
	class Entries extends ArrayList<E> {
		Entries() {
			super();
		}

		@Override
		public E set(int i, E entry) {
			E entry2 = super.set(i, entry);
			EntryListWidget.this.setEntryParentList(entry);
			return entry2;
		}

		@Override
		public boolean add(E entry) {
			EntryListWidget.this.setEntryParentList(entry);
			return super.add(entry);
		}
	}

	@ClientOnly
	protected abstract static class Entry<E extends Entry<E>> implements Element {
		@Deprecated
		EntryListWidget<E> parentList;

		protected Entry() {
		}

		public void setFocused(boolean focused) {
		}

		public boolean isFocused() {
			return this.parentList.getFocused() == this;
		}

		public void render(
			long ctx, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
		){}

		public void render(
			int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
		){}

		public void drawBorder(
			long ctx, int index, int y, int x, int entryWidth, int entryHeight, double mouseX, double mouseY, boolean hovered, float tickDelta
		) {
		}

		public void drawBorder(
			int index, int y, int x, int entryWidth, int entryHeight, double mouseX, double mouseY, boolean hovered, float tickDelta
		) {
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return Objects.equals(this.parentList.getEntryAtPosition(mouseX, mouseY), this);
		}
	}
}
