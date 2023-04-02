package io.github.axolotlclient.AxolotlClientConfig.screen;

import java.util.*;
import java.util.stream.Collectors;

import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigConfig;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.common.types.Tooltippable;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.screen.overlay.ColorSelectionWidget;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.BooleanWidget;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.CategoryWidget;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.OptionWidget;
import io.github.axolotlclient.AxolotlClientConfig.util.ConfigUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class ButtonWidgetList extends ElementListWidget<ButtonWidgetList.Pair> {

    public List<Pair> entries;

    private final OptionCategory category;

    public ButtonWidgetList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight, OptionCategory category) {
        super(minecraftClient, width, height, top, bottom, entryHeight);

        this.setRenderBackground(false);
        this.setRenderHorizontalShadows(false);
        this.setRenderHeader(false, 0);
        this.setRenderSelection(false);
        this.category=category;

        this.entries = constructEntries(category);
        for(Pair p:entries){
            addEntry(p);
        }
    }

	private ClickableWidget createCategoryWidget(int x, OptionCategory cat){
        if(cat==null) {
            return null;
        } else {
            return cat.getWidget(x, 0, 150, 20);
        }
    }

    private ClickableWidget createWidget(int x, Option<?> option) {
        if (option != null) {
            return option.getWidget(x, 0, 150, 20);
        }
        return null;
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
	public int getRowLeft() {
		return this.width / 2 -155;
	}

    public void renderTooltips(MatrixStack matrices, int mouseX, int mouseY){
        ConfigUtils.applyScissor(0, top, this.width, bottom-top);
        entries.forEach(pair -> pair.renderTooltips(matrices, mouseX, mouseY));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

	@Override
	protected void renderList(MatrixStack matrices, int x, int y, float delta) {
		ConfigUtils.applyScissor(0, top, this.width, bottom-top);
		super.renderList(matrices, x, y, delta);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        /*entries.stream().filter(pair -> pair instanceof OptionEntry).map(pair -> pair.left)
                .forEach(w -> {
                    if
                });*/
        Pair f = getFocused();
        if(f != null){
            if (f instanceof OptionEntry && f.left instanceof OptionWidget){
                ((OptionWidget)f.left).unfocus();
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 32;
    }

    public void tick(){
        for (Pair pair : entries) {
            if (pair.tickable) pair.tick();
        }
    }

    public void filter(final String searchTerm) {
        clearEntries();
        entries.clear();

        Collection<Tooltippable> children = getEntries();

        List<Tooltippable> matched = children.stream().filter(tooltippable -> {
            if(AxolotlClientConfigConfig.searchForOptions.get() && tooltippable instanceof OptionCategory){
                if(((OptionCategory) tooltippable).getOptions().stream().anyMatch(option -> passesSearch(option.toString(), searchTerm))){
                    return true;
                }
            }
            return passesSearch(tooltippable.toString(), searchTerm);
        }).collect(Collectors.toList());

        if(!searchTerm.isEmpty() && AxolotlClientConfigConfig.searchSort.get()){
            if(AxolotlClientConfigConfig.searchSortOrder.get().equals("ASCENDING")) {
                matched.sort(new Tooltippable.AlphabeticalComparator());
            } else {
                matched.sort(new Tooltippable.AlphabeticalComparator().reversed());
            }
        }

        OptionCategory filtered = new OptionCategory(category.getName(), false);
        for (Tooltippable tooltippable : matched) {

            if(tooltippable instanceof Option<?>){
                filtered.add((Option<?>) tooltippable);
            } else if (tooltippable instanceof OptionCategory){
                filtered.addSubCategory((OptionCategory) tooltippable);
            }
        }
        entries = constructEntries(filtered);
        for(Pair p:entries){
            addEntry(p);
        }

        if (getScrollAmount() > Math.max(0, this.getMaxPosition() - (bottom - this.top - 4))) {
            setScrollAmount(Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4)));
        }
    }

    protected boolean passesSearch(String string, String search){
        if(AxolotlClientConfigConfig.searchIgnoreCase.get()) {
            return string.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT));
        }
        return string.contains(search);
    }

    protected List<Tooltippable> getEntries(){
        List<Tooltippable> list = new ArrayList<>(category.getSubCategories());
        list.addAll(category.getOptions());
        return list;
    }

    protected List<Pair> constructEntries(OptionCategory category){
        List<Pair> entries = new ArrayList<>();
        if(!category.getSubCategories().isEmpty()) {
            for (int i = 0; i < category.getSubCategories().size(); i += 2) {
                OptionCategory subCat = (OptionCategory) category.getSubCategories().get(i);
                ClickableWidget buttonWidget = this.createCategoryWidget(width / 2 - 155, subCat);

                OptionCategory subCat2 = i < category.getSubCategories().size() - 1 ? (OptionCategory) category.getSubCategories().get(i + 1) : null;

                ClickableWidget buttonWidget2 = this.createCategoryWidget(width / 2 - 155 + 160, subCat2);


                entries.add(new CategoryPair(subCat, buttonWidget, subCat2, buttonWidget2));
            }
            entries.add(new Spacer());
        }

        for (int i = 0; i < (category.getOptions().size()); i ++) {

            Option<?> option = (Option<?>) category.getOptions().get(i);
            assert MinecraftClient.getInstance().currentScreen != null;
            List<String> names;

            if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder){
                names = AxolotlClientConfigManager.getInstance().getIgnoredNames(
                        ((OptionsScreenBuilder)MinecraftClient.getInstance().currentScreen)
                                .modid);
            } else if (MinecraftClient.getInstance().currentScreen instanceof ColorSelectionWidget){
                names = AxolotlClientConfigManager.getInstance().getIgnoredNames(
                        ((ColorSelectionWidget)MinecraftClient.getInstance().currentScreen)
                                .getModId());
            } else {
                names = Collections.emptyList();
            }

            if(names.contains(option.getName())){
                continue;
            }
            ClickableWidget buttonWidget = this.createWidget(width / 2 - 155+160, option);

            entries.add(new OptionEntry(buttonWidget, option, width));
        }
        return entries;
    }

    @ClientOnly
    public class Pair extends ElementListWidget.Entry<Pair> {
        protected final MinecraftClient client = MinecraftClient.getInstance();
        protected final ClickableWidget left;
        protected final ClickableWidget right;

		protected final boolean tickable;

        public Pair(ClickableWidget left, ClickableWidget right) {
			super();
	        this.left = left;
            this.right = right;
			tickable = left instanceof OptionWidget;
        }

	    @Override
	    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

            if (this.left != null) {
                this.left.setY(y);
                this.left.render(matrices, mouseX, mouseY, tickDelta);
            }

            if (this.right != null) {
                this.right.setY(y);
                this.right.render(matrices, mouseX, mouseY, tickDelta);
            }

        }

        public void renderTooltips(MatrixStack matrices, int mouseX, int mouseY){

        }

		protected void renderTooltip(MatrixStack matrices, Tooltippable option, int x, int y){
			if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
				option.getTooltip()!=null && y<bottom && y>top){
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).renderTooltip(matrices, option, x, y);
				ConfigUtils.applyScissor(0, top, width, bottom-top);
			}
		}

        @Override
        public List<? extends Element> children() {
            if(left != null) {
                if (right != null) {
                    return List.of(left, right);
                }
                return List.of(left);
            }
            return Collections.emptyList();
        }

        @Override
	    public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean bl = super.mouseClicked(mouseX, mouseY, button);
            if(bl){
                AxolotlClientConfigManager.getInstance().saveCurrentConfig();
            }
            return bl;
        }

		public void tick(){
			if(tickable) ((OptionWidget)left).tick();
		}

        @Override
        public List<? extends Selectable> selectableChildren() {
            if(left instanceof OptionWidget){
                return ((OptionWidget) left).selectableChildren();
            }
            if(left != null) {
                if (right != null) {
                    return List.of(left, right);
                }
                return List.of(left);
            }
            return Collections.emptyList();
        }
    }

	public class CategoryPair extends Pair {

		protected OptionCategory left;
		protected OptionCategory right;

		public CategoryPair(OptionCategory catLeft, ClickableWidget btnLeft, OptionCategory catRight, ClickableWidget btnRight) {
			super(btnLeft, btnRight);
            btnLeft.setFocused(false);
            if(btnRight != null) {
                btnRight.setFocused(false);
            }
			left = catLeft;
			right = catRight;
		}

        @Override
        public void renderTooltips(MatrixStack matrices, int mouseX, int mouseY) {
            if(AxolotlClientConfigConfig.showCategoryTooltips.get()) {
				if (super.left != null && super.left.isMouseOver(mouseX, mouseY)) {
                    if(AxolotlClientConfigConfig.showQuickToggles.get() && ((CategoryWidget)super.left).enabledButton != null && ((CategoryWidget)super.left).enabledButton.isMouseOver(mouseX, mouseY)){
                        renderTooltip(matrices, ((CategoryWidget) super.left).enabledButton.option, mouseX, mouseY);
                    } else {
                        renderTooltip(matrices, left, mouseX, mouseY);
                    }
				} else if (super.left != null && super.left.isFocused()) {
                    if(AxolotlClientConfigConfig.showQuickToggles.get() && ((CategoryWidget)super.left).enabledButton != null && ((CategoryWidget)super.left).enabledButton.isFocused()){
                        renderTooltip(matrices, ((CategoryWidget) super.left).enabledButton.option, super.left.getX() + super.left.getWidth()/2, super.left.getY());
                    } else {
                        renderTooltip(matrices, left, super.left.getX() + super.left.getWidth()/2, super.left.getY());
                    }
                }
                if (super.right != null && super.right.isMouseOver(mouseX, mouseY)) {
                    if(AxolotlClientConfigConfig.showQuickToggles.get() && ((CategoryWidget)super.right).enabledButton != null && ((CategoryWidget)super.right).enabledButton.isMouseOver(mouseX, mouseY)){
                        renderTooltip(matrices, ((CategoryWidget) super.right).enabledButton.option, mouseX, mouseY);
                    } else {
                        renderTooltip(matrices, right, mouseX, mouseY);
                    }
				} else if (super.right != null && super.right.isFocused()) {
                    if(AxolotlClientConfigConfig.showQuickToggles.get() && ((CategoryWidget)super.right).enabledButton != null && ((CategoryWidget)super.right).enabledButton.isFocused()){
                        renderTooltip(matrices, ((CategoryWidget) super.right).enabledButton.option, super.right.getX() + super.right.getWidth()/2, super.right.getY());
                    } else {
                        renderTooltip(matrices, right, super.right.getX() + super.right.getWidth()/2, super.right.getY());
                    }
                }
			}
		}
    }

    public class OptionEntry extends Pair {

        private final Option<?> option;
        protected int renderX;
        private final int nameWidth;

        private final String name;

        public OptionEntry(ClickableWidget left, Option<?> option, int width) {
            super(left, null);
            this.option = option;
            if(left instanceof BooleanWidget) left.setX(width / 2 + 5 + 57);
            else left.setX(width / 2 + 5);
            left.setFocused(false);
            this.name = option.getTranslatedName();
            nameWidth = MinecraftClient.getInstance().textRenderer.getWidth(name);
        }

	    @Override
	    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

            drawName(matrices, name, ButtonWidgetList.this.width/2-125, y+5,  ButtonWidgetList.this.width/2-10, y+5+client.textRenderer.fontHeight, -1);
            left.setY(y);
            left.render(matrices, mouseX, mouseY, tickDelta);

            renderX = ButtonWidgetList.this.width/2-125;
        }

        private void drawName(MatrixStack matrices, String text, int x, int y, int x2, int y2, int color){
            int n = client.textRenderer.getWidth(text);
            int o = (y + y2 - 9) / 2 + 1;
            int p = x2 - x;
            if (n > p) {
                int q = n - p;
                double d = (double) Util.getMeasuringTimeMs() / 1000.0;
                double e = Math.max((double)q * 0.5, 3.0);
                double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
                double g = MathHelper.lerp(f, 0.0, q);
                enableScissor(x, y, x2, y2);
                drawStringWithShadow(matrices, client.textRenderer, text, x - (int)g, o, color);
                disableScissor();
            } else {
                drawCenteredText(matrices, client.textRenderer, text, (x + x2) / 2, o, color);
            }
        }

        @Override
        public void renderTooltips(MatrixStack matrices, int mouseX, int mouseY) {
		    if(AxolotlClientConfigConfig.showOptionTooltips.get()){
                if(mouseX>=renderX && mouseX<=(renderX + nameWidth) && mouseY>= left.getY() && mouseY<= left.getY() + left.getHeight()||
                    (option instanceof BooleanOption && ((BooleanOption) option).getForceDisabled() &&
                            mouseY>= left.getY() && mouseY<= left.getY() + 20 && mouseX>=renderX && mouseX<= left.getX() +left.getWidth())) {
                    renderTooltip(matrices, option, mouseX, mouseY);
                } else if (left.isFocused()) {
                    renderTooltip(matrices, option, left.getX() +left.getWidth()/2, left.getY());
                }
		    }
        }

        @Override
        public List<? extends Element> children() {
            if(left instanceof ParentElement){
                return ((ParentElement) left).children();
            }
            return List.of(left);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return left instanceof OptionWidget ? ((OptionWidget) left).selectableChildren() : List.of(left);
        }
    }

    public class Spacer extends Pair {

        public Spacer() {
            super(null, null);
        }

	    @Override
	    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	    }
    }
}
