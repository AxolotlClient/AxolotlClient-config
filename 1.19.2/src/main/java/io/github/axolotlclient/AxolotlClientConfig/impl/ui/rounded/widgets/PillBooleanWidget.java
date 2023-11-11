package io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.Updatable;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.NVGHolder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ScreenTexts;
import net.minecraft.util.Util;

public class PillBooleanWidget extends RoundedButtonWidget implements Updatable {

	protected static final int HANDLE_MARGIN = 3;
	protected static final int OFF_POSITION = HANDLE_MARGIN;
	private final BooleanOption option;
	protected int handleWidth;
	protected int onPosition;
	private boolean state;
	private boolean targetState;
	private double progress;
	private long tickTime = Util.getEpochTimeMs();
	private int notWidth;

	public PillBooleanWidget(int x, int y, int width, int height, BooleanOption option) {
		super(x, y, 40, height, option.get() ? ScreenTexts.ON : ScreenTexts.OFF, widget -> {
			option.set(!option.get());
			widget.setMessage(option.get() ? ScreenTexts.ON : ScreenTexts.OFF);
		});

		this.option = option;

		state = targetState = option.get();

		if (state) {
			progress = 1f;
		}

		handleWidth = height - HANDLE_MARGIN * 2;
		onPosition = super.getWidth() - handleWidth - HANDLE_MARGIN;

		this.notWidth = width;
	}

	@Override
	public int getWidth() {
		return notWidth;
	}

	@Override
	public void setWidth(int value) {
		setX(getX() + value - 40);
		this.notWidth = value;
	}

	public void setX(int value) {
		this.x = value;
	}

	@Override
	public void renderButton(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		fillRoundedRect(NVGHolder.getContext(), getX(), getY(), super.getWidth(), getHeight(), Colors.GRAY, Math.min(getHeight(), super.getWidth()) / 2f);

		if (((Util.getEpochTimeMs() - tickTime) / 300L) % 2L == 0L) {
			tickTime = Util.getEpochTimeMs();
			if (state != targetState) {
				if (targetState) {
					progress = Math.min(1, progress + 0.05f);
				} else {
					progress = Math.max(0, progress - 0.05f);
				}
				if (progress >= 1 || progress <= 0) {
					state = targetState;
				}
			}
		}

		double x = getX() + OFF_POSITION + (onPosition - OFF_POSITION) * progress;
		double widthProgress = progress > 0.5f ? 1 - progress : progress;
		drawHandle(NVGHolder.getContext(), (float) x, getY(), (float) (handleWidth + (handleWidth * widthProgress)));
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
	}

	public void update() {
		targetState = option.get();
		setMessage(option.get() ? ScreenTexts.ON : ScreenTexts.OFF);
	}
}
