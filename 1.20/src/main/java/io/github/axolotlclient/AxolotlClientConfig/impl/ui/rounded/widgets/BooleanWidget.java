package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.text.CommonTexts;
import net.minecraft.util.Util;

public class BooleanWidget extends RoundedButtonWidget {

	protected static final int HANDLE_MARGIN = 3;
	protected static final int WIDGET_WIDTH = 60;
	protected static final int OFF_POSITION = HANDLE_MARGIN;
	protected int handleWidth;
	protected final int onOffset = 18;

	private boolean state;
	private boolean targetState;
	private float progress;

	private long progressStartTime = Util.getEpochTimeMs();

	public BooleanWidget(int x, int y, int width, int height, BooleanOption option) {
		super(x, y, width, height, option.get() ? CommonTexts.ON : CommonTexts.OFF, widget -> {
			option.set(!option.get());
			widget.setMessage(option.get() ? CommonTexts.ON : CommonTexts.OFF);
		});

		state = targetState = option.get();

		if (state) {
			progress = 1f;
		}

		handleWidth = height - HANDLE_MARGIN * 2;

		setX(getX() + getWidth() - WIDGET_WIDTH);
		setWidth(WIDGET_WIDTH);
	}

	@Override
	protected void drawWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		fillRoundedRect(NVGHolder.getContext(), getX(), getY(), getWidth(), getHeight(), Colors.GRAY, Math.min(getHeight(), getWidth()) / 2f);

		//if (targetState != state) {

			if (((Util.getEpochTimeMs() - progressStartTime) / 300L) % 2L == 0L) {
				if (state != targetState) {
					if (targetState) {
						progress = Math.min(1, progress + 0.04f);
					} else {
						progress = Math.max(0, progress - 0.04f);
					}
					if (progress == 1 || progress == 0) {
						state = targetState;
					}
				}
			}

			float x = getX() + OFF_POSITION + onOffset * progress;
			//System.out.println("Progress: "+progress+" X: "+x);
			float widthProgress = progress > 0.5f ? 1 - progress : progress;
			drawHandle(NVGHolder.getContext(), x, getY(), handleWidth + (handleWidth * widthProgress));


		/*} else {
			if (state) {
				drawHandle(NVGHolder.getContext(), getX() + OFF_POSITION + onOffset, getY(), handleWidth);
			} else {
				drawHandle(NVGHolder.getContext(), getX() + OFF_POSITION, getY(), handleWidth);
			}
		}*/
	}

	protected void drawHandle(long ctx, float x, float y, float width) {
		fillRoundedRect(ctx, x, y + HANDLE_MARGIN, width, getHeight() - HANDLE_MARGIN * 2,
			getWidgetColor(), Math.min(width, getHeight()) / 2f + HANDLE_MARGIN);

		if (isFocused()) {
			outlineRoundedRect(ctx, x, y + HANDLE_MARGIN, width, getHeight() - HANDLE_MARGIN * 2,
				Colors.WHITE, Math.min(width, getHeight()) / 2f + HANDLE_MARGIN, 1);
		}
	}

	@Override
	public void onPress() {
		super.onPress();
		state = targetState;
		targetState = !targetState;
		progressStartTime = Util.getEpochTimeMs();
	}
}
