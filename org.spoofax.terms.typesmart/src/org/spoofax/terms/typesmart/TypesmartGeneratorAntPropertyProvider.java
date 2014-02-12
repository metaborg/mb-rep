package org.spoofax.terms.typesmart;

import java.io.File;

import org.eclipse.ant.core.IAntPropertyValueProvider;

public class TypesmartGeneratorAntPropertyProvider implements
		IAntPropertyValueProvider {

	@Override
	public String getAntPropertyValue(String antPropertyName) {
		String result;
		result = new File(org.spoofax.terms.typesmart.generator.GenerateTypesmartConstructors.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParent();
		return result;
	}

}
