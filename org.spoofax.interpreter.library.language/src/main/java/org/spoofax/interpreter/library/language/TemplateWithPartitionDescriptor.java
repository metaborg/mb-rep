package org.spoofax.interpreter.library.language;

import org.spoofax.interpreter.terms.IStrategoAppl;

public class TemplateWithPartitionDescriptor {
    private final IStrategoAppl template;
    private final IndexPartitionDescriptor fileDescriptor;

    public TemplateWithPartitionDescriptor(IStrategoAppl template, IndexPartitionDescriptor partitionDescriptor) {
        this.template = template;
        this.fileDescriptor = partitionDescriptor;
    }

    public IStrategoAppl getTemplate() {
        return template;
    }

    public IndexPartitionDescriptor getPartitionDescriptor() {
        return fileDescriptor;
    }
}
