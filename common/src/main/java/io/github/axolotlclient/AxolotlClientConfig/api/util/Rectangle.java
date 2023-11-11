package io.github.axolotlclient.AxolotlClientConfig.api.util;

import lombok.Data;
import lombok.With;
import lombok.experimental.Accessors;

@With
@Data
@Accessors(fluent = true)
public class Rectangle {
	private final int x, y, width, height;
}
