package io.github.axolotlclient.AxolotlclientConfig.options;

import net.minecraft.client.gui.widget.ClickableWidget;

/**
 * A named object being able to generate a Widget.
 */
public interface WidgetSupplier extends Identifiable {

    ClickableWidget getWidget(int x, int y, int width, int height);
}
