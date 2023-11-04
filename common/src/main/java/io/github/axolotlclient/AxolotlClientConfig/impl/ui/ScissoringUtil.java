package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.util.Stack;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Rectangle;
import lombok.AccessLevel;
import lombok.Getter;

import static org.lwjgl.nanovg.NanoVG.*;

public class ScissoringUtil {


	@Getter(AccessLevel.PACKAGE)
	private static final ScissoringUtil instance = new ScissoringUtil();

	private final Stack<Rectangle> scissorStack = new Stack<>();

	public void push(long ctx, int x, int y, int width, int height){
		set(ctx, scissorStack.push(new Rectangle(x, y, width, height)));
	}

	private void set(long ctx, Rectangle rectangle){
		if(rectangle != null) {
			if (scissorStack.size() > 1) {
				nvgIntersectScissor(ctx, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
			} else {
				nvgScissor(ctx, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
			}
		} else {
			nvgResetScissor(ctx);
		}
	}

	public void pop(long ctx){
		scissorStack.pop();
		set(ctx, scissorStack.empty() ? null : scissorStack.peek());
	}
}
