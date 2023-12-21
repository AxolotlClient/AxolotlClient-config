/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.AxolotlClientConfig.impl.ui;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import lombok.Getter;
import org.lwjgl.Version;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

/**
 * @author TheKodeToad
 */

public class NVGFont implements AutoCloseable {
	protected final int handle;
	protected final long nvg;
	private final ByteBuffer buffer;
	@Getter
	private int size = 8;

	NVGFont(long ctx, InputStream in) throws IOException {
		buffer = mallocAndRead(in);
		handle = createFont(ctx, buffer, "regular");
		this.nvg = ctx;
	}

	NVGFont(long ctx, int handle) {
		this.nvg = ctx;
		this.handle = handle;
		buffer = null;
	}

	protected int createFont(long ctx, ByteBuffer buffer, String name) {
		int imageHandle;
		String[] lwjglVersion = Version.getVersion().split(" ")[0]
			.split("-")[0]
			.split("\\+")[0]
			.split("\\.");
		int major = Integer.parseInt(lwjglVersion[0]);
		int minor = Integer.parseInt(lwjglVersion[1]);
		int patch = Integer.parseInt(lwjglVersion[2]);
		boolean requiresAlternative = false;
		if (major <= 3) {
			if (minor <= 3) {
				if (minor < 3) {
					requiresAlternative = true;
				} else if (patch <= 1) {
					requiresAlternative = true;
				}
			}
		}
		if (!requiresAlternative) {
			imageHandle = NanoVG.nvgCreateFontMem(ctx, name, buffer, false);
		} else {
			try {
				imageHandle = (int) MethodHandles.lookup()
					.findStatic(NanoVG.class, "nvgCreateFontMem",
						MethodType.methodType(int.class, long.class, CharSequence.class, ByteBuffer.class, int.class))
					.invoke(ctx, name, buffer, 0);
			} catch (Throwable t) {
				throw new IllegalStateException(t);
			}
		}
		return imageHandle;
	}

	protected ByteBuffer mallocAndRead(InputStream in) throws IOException {
		try (ReadableByteChannel channel = Channels.newChannel(in)) {
			ByteBuffer buffer = MemoryUtil.memAlloc(8192);

			while (channel.read(buffer) != -1)
				if (buffer.remaining() == 0)
					buffer = MemoryUtil.memRealloc(buffer, buffer.capacity() + buffer.capacity() * 3 / 2);

			((Buffer) buffer).flip();

			return buffer;
		}
	}

	public void withSize(int size, Runnable action) {
		int oldSize = this.size;
		this.size = size;
		action.run();
		this.size = oldSize;
	}

	public <T> T withSize(int size, Supplier<T> action) {
		AtomicReference<T> result = new AtomicReference<>();
		withSize(size, () -> result.set(action.get()));
		return result.get();
	}


	public void bind() {
		NanoVG.nvgFontFaceId(nvg, handle);
		NanoVG.nvgFontSize(nvg, size);
	}

	@Override
	public void close() {
		if (buffer != null) {
			MemoryUtil.memFree(buffer);
		}
	}

	public float getWidth(String string) {
		bind();
		float[] bounds = new float[4];
		NanoVG.nvgTextBounds(nvg, 0, 0, string, bounds);
		return bounds[2];
	}

	public String trimToWidth(String text, float width) {
		return trimToWidth(text, width, false);
	}

	public String trimToWidth(String text, float width, boolean backwards) {

		if (getWidth(text) <= width) {
			return text;
		}

		int i = backwards ? text.length() - 1 : 0;
		StringBuilder builder = new StringBuilder();
		while (getWidth(builder.toString()) < width) {
			builder.append(text.charAt(i));
			i += (backwards ? -1 : 1);
		}
		try {
			builder.delete(builder.length() - 1 - Character.charCount(builder.codePointAt(builder.length() - 1)), builder.length() - 1);
		} catch (Exception ignored){}
		if (backwards) {
			builder.reverse();
		}
		return builder.toString();
	}

	public float getLineHeight() {
		bind();
		float[] ascender = new float[1];
		float[] descender = new float[1];
		float[] lineh = new float[1];
		NanoVG.nvgTextMetrics(nvg, ascender, descender, lineh);
		return lineh[0];
	}

	public float renderString(String string, float x, float y) {
		return NanoVG.nvgText(nvg, x, y + getLineHeight(), string);
	}

}
