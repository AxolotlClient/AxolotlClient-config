package io.github.axolotlclient.AxolotlclientConfig.screen;

import io.github.axolotlclient.AxolotlclientConfig.AxolotlClientConfigConfig;
import io.github.axolotlclient.AxolotlclientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlclientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.AxolotlclientConfig.options.Tooltippable;
import io.github.axolotlclient.AxolotlclientConfig.screen.widgets.ColorSelectionWidget;
import io.github.axolotlclient.AxolotlclientConfig.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OptionsScreenBuilder extends Screen {

    private final Screen parent;
    public String modid;
    protected OptionCategory cat;

    protected ColorSelectionWidget picker;
    protected TextFieldWidget searchWidget;

    private ButtonWidgetList list;

    public OptionsScreenBuilder(Screen parent, OptionCategory category, String modid){
	    super(Text.of(""));
	    this.parent=parent;
        this.cat=category;
        this.modid = modid;
    }

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(MinecraftClient.getInstance().world!=null) DrawUtil.fill(matrices,0,0, width, height, 0xB0100E0E);
        else renderBackgroundTexture(0);


        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, cat.getTranslatedName(), width/2, 25, -1);

        if(picker!=null){
            picker.render(matrices, mouseX, mouseY, delta);
        } else {
            list.renderTooltips(matrices, mouseX, mouseY);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    public void openColorPicker(ColorOption option){
        picker = new ColorSelectionWidget(option);
    }

    public void closeColorPicker(){
        AxolotlClientConfigManager.saveCurrentConfig();
        picker=null;
    }

    public boolean isPickerOpen(){
        return picker!=null;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(list.isMouseOver(mouseX, mouseY)) {
            return list.mouseScrolled(mouseX, mouseY, amount);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(isPickerOpen()){
            return false;
        }
        return list.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl = super.mouseClicked(mouseX, mouseY, button);

        if(isPickerOpen()){
            if(!picker.isMouseOver(mouseX, mouseY)) {
                closeColorPicker();
                this.list.mouseClicked(mouseX, mouseY, button);

            } else {
                picker.onClick(mouseX, mouseY);
            }
        } else {
            searchWidget.mouseClicked(mouseX, mouseY, button);
            return bl || this.list.mouseClicked(mouseX, mouseY, button);
        }
        return bl;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(isPickerOpen() && picker.mouseReleased(mouseX, mouseY, button)){
            return true;
        }
        return this.list.mouseReleased(mouseX, mouseY, button) || super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
    public void tick() {
        this.list.tick();
        searchWidget.tick();
        if(isPickerOpen()){
            picker.tick();
        }
    }

    @Override
    public void init() {
        createWidgetList(cat);

		this.addSelectableChild(list);

        this.addDrawableChild(new ButtonWidget(this.width/2-100, this.height-40, 200, 20, Text.translatable("back"), buttonWidget -> {
            if(isPickerOpen()){
                closeColorPicker();
            }

            AxolotlClientConfigManager.saveCurrentConfig();
            MinecraftClient.getInstance().setScreen(parent);
        }));

        this.addDrawableChild(searchWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, width - 120, 20, 100, 20, Text.translatable("search")){

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if(isMouseOver(mouseX, mouseY)) {
                    if (!isFocused() && cat.toString().toLowerCase(Locale.ROOT).contains(modid.toLowerCase(Locale.ROOT))) {
                        MinecraftClient.getInstance().setScreen(new OptionsScreenBuilder(MinecraftClient.getInstance().currentScreen, getAllOptions(), modid));
                        return true;
                    }
                    setSuggestion("");
                    return super.mouseClicked(mouseX, mouseY, button);
                }
                setFocused(false);
                setSuggestion(Formatting.ITALIC + Text.translatable("search").append("...").getString());
                return false;
            }

            @Override
            public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                super.renderButton(matrices, mouseX, mouseY, delta);

                drawHorizontalLine(matrices, x-5, x+width, y+11, -1);
            }
        });

        searchWidget.setDrawsBackground(false);
        searchWidget.setSuggestion(Formatting.ITALIC + Text.translatable("search").append("...").getString());
        searchWidget.setChangedListener(s -> {
            list.filter(s);

            /*if(!s.equals("")){
                searchWidget.setSuggestion("");
            } else {
                searchWidget.setSuggestion(Formatting.ITALIC + Text.translatable("search").append("...").getString());
            }*/
        });
    }

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(isPickerOpen() && picker.keyPressed(keyCode, scanCode, modifiers)){
            return true;
        }
		//return this.list.keyPressed(keyCode, scanCode, modifiers) ||
        return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
        if(isPickerOpen() && picker.charTyped(chr, modifiers)){
            return true;
        }
        return super.charTyped(chr, modifiers);
	}

	public void renderTooltip(MatrixStack matrices, Tooltippable option, int x, int y){
		List<Text> text = new ArrayList<>();
		String[] tooltip = Objects.requireNonNull(option.getTooltip()).split("<br>");
		for(String s:tooltip) text.add(Text.literal(s));
		this.renderTooltip(matrices, text, x, y);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if(isPickerOpen()){
            picker.init();
        }
        AxolotlClientConfigManager.saveCurrentConfig();
        super.resize(client, width, height);
    }

    protected void createWidgetList(OptionCategory category){
        this.list = new ButtonWidgetList(client, this.width, height, 50, height-50, 25, category);
    }

    protected OptionCategory getAllOptions(){
        OptionCategory temp = new OptionCategory("", false);

        for(OptionCategory cat: AxolotlClientConfigManager.getModConfig(modid).getCategories()) {
            setupOptionsList(temp, cat);
        }

        List<OptionCategory> list = temp.getSubCategories();

        if(AxolotlClientConfigConfig.searchSort.get()){
            if(AxolotlClientConfigConfig.searchSortOrder.get().equals("ASCENDING")) {
                list.sort(new Tooltippable.AlphabeticalComparator());
            } else {
                list.sort(new Tooltippable.AlphabeticalComparator().reversed());
            }
        }

        return new OptionCategory("searchOptions", false)
                .addSubCategories(list);
    }

    protected void setupOptionsList(OptionCategory target, OptionCategory cat){
        target.addSubCategory(cat);
        if(!cat.getSubCategories().isEmpty()){
            for(OptionCategory sub : cat.getSubCategories()){
                setupOptionsList(target, sub);
            }
        }
    }
}
