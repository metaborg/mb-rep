package org.spoofax.interpreter.library.language;

import org.spoofax.interpreter.terms.IStrategoAppl;

public class TemplateWithFileDescriptor {
	private final IStrategoAppl template;
	private final SemanticIndexFileDescriptor fileDescriptor;
	
	public TemplateWithFileDescriptor(IStrategoAppl template, SemanticIndexFileDescriptor fileDescriptor) {
		this.template = template;
		this.fileDescriptor = fileDescriptor;
	}
	
	public IStrategoAppl getTemplate() {
		return template;
	}
	
	public SemanticIndexFileDescriptor getFileDescriptor() {
		return fileDescriptor;
	}
}
