package io.github.axolotlclient.AxolotlClientConfig.api.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Colors {

	public final Color WHITE = new Color(-1).immutable();
	public final Color TURQUOISE = Color.parse("#009999").immutable();
	public final Color TRANSPARENT = new Color(0, 0, 0, 0).immutable();
	public final Color RED = new Color(255, 0, 0).immutable();
	public final Color GREEN = new Color(0, 255, 0).immutable();
	public final Color BLUE = new Color(0, 0, 255).immutable();
	public final Color DARK_YELLOW = new Color(155, 155, 0).immutable();
	public final Color YELLOW = new Color(255, 255, 0).immutable();
	public final Color BLACK = new Color(0, 0, 0).immutable();
	public final Color GRAY = new Color(127, 127, 127).immutable();
	public final Color DARK_GRAY = new Color(50, 50, 50).immutable();
}
