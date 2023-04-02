package io.github.axolotlclient.AxolotlClientConfig.options;

import net.minecraft.client.gui.widget.ClickableWidget;

/**
 * A named object being able to generate a Widget.
 */
public interface WidgetSupplier extends io.github.axolotlclient.AxolotlClientConfig.common.types.Identifiable {

    /**
     * Create this Identifiable's Widget.
     *
     * @param x      horizontal position
     * @param y      vertical position
     * @param width  Width of the Widget
     * @param height Height of the widget
     * @return the Widget
     */
    ClickableWidget getWidget(int x, int y, int width, int height);
}
