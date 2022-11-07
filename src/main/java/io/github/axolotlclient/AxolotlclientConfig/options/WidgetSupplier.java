package io.github.axolotlclient.AxolotlclientConfig.options;

import net.minecraft.client.gui.widget.ButtonWidget;

/**
 * A named object able to generate a Widget.
 */
public interface WidgetSupplier extends Identifiable {

    ButtonWidget getWidget(int x, int y, int width, int height);
}
