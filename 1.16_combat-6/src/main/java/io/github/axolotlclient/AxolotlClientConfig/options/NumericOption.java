package io.github.axolotlclient.AxolotlClientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.OptionSliderWidget;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;

public abstract class NumericOption<T extends Number> extends OptionBase<T>{

    protected final T min, max;

    public NumericOption(String name, T def, T min, T max) {
        super(name, def);
        this.min = min;
        this.max = max;
    }

    public NumericOption(String name, ChangedListener<T> onChange, T def, T min, T max) {
        super(name, onChange, def);
        this.min = min;
        this.max = max;
    }

    public NumericOption(String name, String tooltipKeyPrefix, T def, T min, T max) {
        super(name, tooltipKeyPrefix, def);
        this.min = min;
        this.max = max;
    }

    public NumericOption(String name, String tooltipKeyPrefix, ChangedListener<T> onChange, T def, T min, T max) {
        super(name, tooltipKeyPrefix, onChange, def);
        this.min = min;
        this.max = max;
    }

    public T getMin(){
        return min;
    }

    public T getMax(){
        return max;
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(option);
    }

    @Override
    public AbstractPressableButtonWidget getWidget(int x, int y, int width, int height) {
        return new OptionSliderWidget<>(x, y, this);
    }
}
