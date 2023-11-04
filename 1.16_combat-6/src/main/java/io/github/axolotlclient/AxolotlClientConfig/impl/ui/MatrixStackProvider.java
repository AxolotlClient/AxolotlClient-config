package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.math.MatrixStack;

public class MatrixStackProvider {

	@Getter
	private static final MatrixStackProvider instance = new MatrixStackProvider();

	@Getter @Setter
	private MatrixStack stack;
}
