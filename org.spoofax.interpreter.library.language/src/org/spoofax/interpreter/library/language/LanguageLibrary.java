package org.spoofax.interpreter.library.language;

import org.spoofax.interpreter.library.AbstractStrategoOperatorRegistry;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LanguageLibrary extends AbstractStrategoOperatorRegistry {
	
	public static final String REGISTRY_NAME = "LANGUAGE";

	public LanguageLibrary() {
		// TODO Auto-generated constructor stub
	}

	public String getOperatorRegistryName() {
		return REGISTRY_NAME;
	}

}
