package io.github.axolotlclient.AxolotlclientConfig.options;

import net.minecraft.client.gui.widget.ButtonWidget;

/**
 * A named object able to generate a Widget.
 */
public interface WidgetSupplier extends Identifiable {

    /**
     * Create this Identifiable's Widget.
     *
     * @param x horizontal position
     * @param y vertical position
     * @param width Width of the Widget
     * @param height Height of the widget
     * @return the Widget
     */
    ButtonWidget getWidget(int x, int y, int width, int height);
}
