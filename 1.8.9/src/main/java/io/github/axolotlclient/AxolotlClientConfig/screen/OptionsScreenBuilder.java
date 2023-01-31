package io.github.axolotlclient.AxolotlClientConfig.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigConfig;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.common.types.Tooltippable;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.screen.overlay.Overlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PagedEntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OptionsScreenBuilder extends Screen {

    private final Screen parent;
    protected OptionCategory cat;

    protected Overlay overlay;

    private ButtonWidgetList list;
    protected TextFieldWidget searchWidget;

    public final String modid;

    public OptionsScreenBuilder(Screen parent, OptionCategory category, String modid){
        this.parent=parent;
        this.cat=category;
        this.modid = modid;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        renderBackground();

        drawCenteredString(textRenderer, cat.getTranslatedName(), width/2, 25, -1);

        super.render(mouseX, mouseY, tickDelta);

        this.list.render(mouseX, mouseY, tickDelta);

        searchWidget.render();

        if(overlay !=null){
            GlStateManager.disableDepthTest();
            overlay.render(MinecraftClient.getInstance(), mouseX, mouseY);
            GlStateManager.enableDepthTest();
        } else {
            list.renderTooltips(mouseX, mouseY);
        }
    }

    public void setOverlay(Overlay overlay) {
        this.overlay = overlay;
        overlay.init();
    }

    public void closeOverlay() {
        AxolotlClientConfigManager.getInstance().saveCurrentConfig();
        overlay = null;
    }

    public boolean isOverlayOpen(){
        return overlay !=null;
    }
    @Override
    protected void mouseDragged(int i, int j, int k, long l) {
        if(!isOverlayOpen()) {
            super.mouseDragged(i, j, k, l);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if(isOverlayOpen()){
            if(!overlay.isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)) {
                closeOverlay();
                this.list.mouseClicked(mouseX, mouseY, button);

            } else {
                overlay.onClick(mouseX, mouseY);
            }
        } else {
            this.list.mouseClicked(mouseX, mouseY, button);
            searchWidget.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        if(list != null) {
            this.list.mouseReleased(mouseX, mouseY, button);
        }
        if(isOverlayOpen()) overlay.mouseReleased(mouseX, mouseY);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(button.id==0){
            AxolotlClientConfigManager.getInstance().saveCurrentConfig();
            if(isOverlayOpen()){
                closeOverlay();
            } else {
                MinecraftClient.getInstance().setScreen(parent);
            }
        }
    }

    @Override
    public void tick() {
        this.list.tick();
        searchWidget.tick();
        if(isOverlayOpen()){
            overlay.tick();
        }
    }

    @Override
    public void init() {
        Keyboard.enableRepeatEvents(true);
        createWidgetList(cat);

        searchWidget = new TextFieldWidget(123, MinecraftClient.getInstance().textRenderer, width - 120, 20, 100, 20){

            @Override
            public void mouseClicked(int mouseX, int mouseY, int button) {
                if(isHovered(mouseX, mouseY)) {
                    if(!isFocused() && cat.toString().toLowerCase(Locale.ROOT).contains(modid.toLowerCase(Locale.ROOT))){
                        MinecraftClient.getInstance().setScreen(new OptionsScreenBuilder(OptionsScreenBuilder.this, getAllOptions(), modid));
                        return;
                    }
                    super.mouseClicked(mouseX, mouseY, button);
                } else {
                    setFocused(false);
                }
            }

            @Override
            public void render() {
                if(!isFocused()) {
                    drawWithShadow(MinecraftClient.getInstance().textRenderer, Formatting.ITALIC + I18n.translate("search")+"...", x-1, y, -8355712);
                }

                super.render();
                drawHorizontalLine(x-5, x+100, y+11, -1);
            }

            public boolean isHovered(int mouseX, int mouseY){
                return mouseX >= this.x && mouseX < this.x + 100 && mouseY >= this.y && mouseY < this.y + 20;
            }

            @Override
            public boolean keyPressed(char character, int code) {
                return super.keyPressed(character, code);
            }
        };

        this.buttons.add(new ButtonWidget(0, this.width/2-100, this.height-40, 200, 20, I18n.translate("back")));

        searchWidget.setListener(new PagedEntryListWidget.Listener() {
            @Override
            public void setBooleanValue(int id, boolean value) {}

            @Override
            public void setFloatValue(int id, float value) {}

            @Override
            public void setStringValue(int id, String text) {
                list.filter(text);
            }
        });
        searchWidget.setHasBorder(false);
    }

    @Override
    public void handleMouse() {
        super.handleMouse();
        if(!isOverlayOpen()) {
            this.list.handleMouse();
        }
    }

    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyPressed(char character, int code) {
        if(!isOverlayOpen()) {
            if(searchWidget.isFocused()){
                searchWidget.keyPressed(character, code);
                return;
            }
            if(this.list.keyPressed(character, code)){
                return;
            }
        } else {
            overlay.keyPressed(character, code);
        }
        super.keyPressed(character, code);
    }

    @Override
    public boolean shouldPauseGame() {
        return false;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if(overlay !=null){
            overlay.init();
        }
        super.resize(client, width, height);
    }

    public void renderTooltip(Tooltippable tooltippable, int x, int y){
        String[] tooltip = Objects.requireNonNull(tooltippable.getTooltip()).split("<br>");
        this.renderTooltip(Arrays.asList(tooltip), x, y);
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
