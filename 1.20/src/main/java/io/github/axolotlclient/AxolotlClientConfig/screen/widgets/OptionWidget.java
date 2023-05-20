package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import com.google.common.collect.ImmutableList;
import io.github.axolotlclient.AxolotlClientConfig.screen.overlay.Overlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ScreenArea;
import net.minecraft.client.gui.widget.Widget;

import java.util.List;

public interface OptionWidget extends Selectable, Drawable, Element, Widget {

	default boolean canHover() {
		return !(MinecraftClient.getInstance().currentScreen instanceof Overlay);
	}

	default void unfocus() {
	}

	default void tick() {
	}

	default List<? extends Selectable> selectableChildren() {
		return ImmutableList.of(this);
	}

	default ScreenArea getArea() {
		return Widget.super.getArea();
	}
}
