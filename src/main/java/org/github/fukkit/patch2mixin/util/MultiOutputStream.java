package org.github.fukkit.patch2mixin.util;

import java.io.IOException;
import java.io.OutputStream;

public class MultiOutputStream extends OutputStream {
	private final OutputStream[] streams;

	public MultiOutputStream(OutputStream ...streams) {
		this.streams = streams;
	}

	@Override
	public void write(int b) throws IOException {
		for (OutputStream stream : this.streams) {
			stream.write(b);
		}
	}
}
