package io.github.axolotlclient.AxolotlClientConfig.screen;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigConfig;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.common.types.Tooltippable;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.screen.overlay.Overlay;
import io.github.axolotlclient.AxolotlClientConfig.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ScreenTexts;
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

    protected Overlay overlay;
    protected TextFieldWidget searchWidget;

    private ButtonWidgetList list;

    public ButtonWidget backButton;

    private static boolean pickerWasOpened = false;

    public OptionsScreenBuilder(Screen parent, OptionCategory category, String modid){
	    super(Text.of(category.getTranslatedName()));
	    this.parent=parent;
        this.cat=category;
        this.modid = modid;
    }

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        if(MinecraftClient.getInstance().world!=null) DrawUtil.fill(matrices,0,0, width, height, 0xB0100E0E);
        else renderBackgroundTexture(0);


        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, cat.getTranslatedName(), width/2, 25, -1);

        if(overlay ==null) {
            list.renderTooltips(matrices, mouseX, mouseY);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    public void setOverlay(Overlay overlay){
        MinecraftClient.getInstance().setScreen(this.overlay = overlay);
    }

    public void closeOverlay(){
        AxolotlClientConfigManager.getInstance().saveCurrentConfig();
        pickerWasOpened = true;
        MinecraftClient.getInstance().setScreen(this);
        overlay =null;
    }

    public boolean isPickerOpen(){
        return overlay !=null;
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
        searchWidget.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button) || this.list.mouseClicked(mouseX, mouseY, button);

    }

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean li;
        if(list!=null){
            li = list.mouseReleased(mouseX, mouseY, button);
        } else {
            li = false;
        }
        return li || super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
    public void tick() {
        this.list.tick();
        searchWidget.tick();
    }

    @Override
    public void init() {
        if(!pickerWasOpened) {
            createWidgetList(cat);
        }  else {
            pickerWasOpened = false;
        }
            this.addSelectableChild(list);

            this.addDrawableChild(backButton = ButtonWidget.builder(ScreenTexts.BACK, buttonWidget -> {
				if (isPickerOpen()) {
					closeOverlay();
				} else {
					MinecraftClient.getInstance().setScreen(parent);
				}

				AxolotlClientConfigManager.getInstance().saveCurrentConfig();
			}).positionAndSize(this.width / 2 - 100, this.height - 40, 200, 20).build());

            this.addDrawableChild(searchWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, width - 120, 20, 100, 20, Text.translatable("search")) {

                @Override
                public boolean mouseClicked(double mouseX, double mouseY, int button) {
                    if (isMouseOver(mouseX, mouseY)) {
                        if (!isFocused() && cat.toString().toLowerCase(Locale.ROOT).contains(modid.toLowerCase(Locale.ROOT))) {
                            MinecraftClient.getInstance().setScreen(new OptionsScreenBuilder(OptionsScreenBuilder.this, getAllOptions(), modid));
                            return true;
                        }
                        setSuggestion("");
                        return super.mouseClicked(mouseX, mouseY, button);
                    }
                    setFocused(false);
                    if(getText().isEmpty()) {
                        setSuggestion(Formatting.ITALIC + Text.translatable("search").append("...").getString());
                    }
                    return false;
                }

                @Override
                public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                    if ((keyCode == InputUtil.KEY_SPACE_CODE || keyCode == InputUtil.KEY_ENTER_CODE) && cat.toString().toLowerCase(Locale.ROOT).contains(modid.toLowerCase(Locale.ROOT))) {
                        MinecraftClient.getInstance().setScreen(new OptionsScreenBuilder(OptionsScreenBuilder.this, getAllOptions(), modid));
                    }

                    return super.keyPressed(keyCode, scanCode, modifiers);
                }

                @Override
                public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                    super.renderButton(matrices, mouseX, mouseY, delta);

                    drawHorizontalLine(matrices, getX() - 5, getX() + width, getY() + 11, -1);
                }

                @Override
                public void updateNarration(NarrationMessageBuilder builder) {
                    super.updateNarration(builder);
                    builder.put(NarrationPart.USAGE, Text.translatable("narration.type_to_search"));
                }
            });

            searchWidget.setDrawsBackground(false);
            searchWidget.setSuggestion(Formatting.ITALIC + Text.translatable("search").append("...").getString());
            searchWidget.setChangedListener(s -> {
                if (s.isEmpty()) {
                    searchWidget.setSuggestion(Formatting.ITALIC + Text.translatable("search").append("...").getString());
                } else {
                    searchWidget.setSuggestion("");
                }
                list.filter(s);
            });
    }

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
        if(isPickerOpen()){
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
        AxolotlClientConfigManager.getInstance().saveCurrentConfig();
        boolean pickerOpened = pickerWasOpened;
        pickerWasOpened = false;
        super.resize(client, width, height);
        pickerWasOpened = pickerOpened;
    }

    protected void createWidgetList(OptionCategory category){
        this.list = new ButtonWidgetList(client, this.width, height, 50, height-50, 25, category);
    }

    protected OptionCategory getAllOptions(){
        OptionCategory temp = new OptionCategory("", false);

        for(io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory cat: AxolotlClientConfigManager.getInstance().getModConfig(modid).getCategories()) {
            setupOptionsList(temp, cat);
        }

        List<io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory> list = temp.getSubCategories();

        if(AxolotlClientConfigConfig.searchSort.get()){
            if(AxolotlClientConfigConfig.searchSortOrder.get().equals("ASCENDING")) {
                list.sort(new Tooltippable.AlphabeticalComparator());
            } else {
                list.sort(new Tooltippable.AlphabeticalComparator().reversed());
            }
        }

        return (OptionCategory) new OptionCategory("searchOptions", false)
                .addSubCategories(list);
    }

    protected void setupOptionsList(OptionCategory target, io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory cat){
        target.addSubCategory(cat);
        if(!cat.getSubCategories().isEmpty()){
            for(io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory sub : cat.getSubCategories()){
                setupOptionsList(target, sub);
            }
        }
    }
}
