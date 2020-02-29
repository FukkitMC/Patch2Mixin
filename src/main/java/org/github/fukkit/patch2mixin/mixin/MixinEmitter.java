package org.github.fukkit.patch2mixin.mixin;

import org.objectweb.asm.tree.ClassNode;
import java.util.Map;

public class MixinEmitter {
	private final String namespace;

	public MixinEmitter(String namespace) {this.namespace = namespace;}

	public Map<String, ClassNode> convert() {

	}
}
