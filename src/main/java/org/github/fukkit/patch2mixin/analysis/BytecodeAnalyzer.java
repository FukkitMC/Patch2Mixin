package org.github.fukkit.patch2mixin.analysis;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class BytecodeAnalyzer {
	private final ZipFile unpatched;
	private final ZipFile patched;
	private final ZipOutputStream output;
	private final String namespace;

	public BytecodeAnalyzer(ZipFile unpatched, ZipFile patched, ZipOutputStream output, String namespace) {
		this.unpatched = unpatched;
		this.patched = patched;
		this.output = output;
		this.namespace = namespace.replace('.', '/') + "/";
	}

	public void analyze() throws IOException {
		Enumeration<ZipEntry> patchedEntries = (Enumeration<ZipEntry>) patched.entries();
		while (patchedEntries.hasMoreElements()) {
			ZipEntry patchedClass = patchedEntries.nextElement();
			if (!patchedClass.isDirectory() && patchedClass.getName().endsWith(".class")) { // must be class file
				String path = patchedClass.getName();
				ZipEntry unpatched = this.unpatched.getEntry(path);
				if (unpatched == null) { // added class (does not exist in src)
					ZipEntry entry = new ZipEntry(path); // leave in original location
					this.output.putNextEntry(entry);
					this.write(this.patched.getInputStream(patchedClass), this.output);
					this.output.closeEntry();
				} else if (unpatched.getCrc() != patchedClass.getCrc()) { // patched class
					// TODO: special handling for inner classes
					ClassReader reader = new ClassReader(this.patched.getInputStream(patchedClass)); // read patched class
					ClassNode patchedNode = new ClassNode();
					reader.accept(patchedNode, 0); // parse patched class file
					reader = new ClassReader(this.unpatched.getInputStream(unpatched));
					ClassNode vanillaNode = new ClassNode();
					reader.accept(vanillaNode, 0);

				} // else class is untouched and vanilla
			}
		}
	}

	private static final ThreadLocal<byte[]> BUFFERS = ThreadLocal.withInitial(() -> new byte[1024]);

	private void write(InputStream stream, OutputStream out) throws IOException {
		byte[] buff = BUFFERS.get();
		int len;
		while ((len = stream.read(buff)) > 0) out.write(buff, 0, len);
	}
}
