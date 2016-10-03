package org.metaborg.scopegraph.experimental;

import java.io.Serializable;

public interface IScopeGraph extends Serializable {

    IScopeGraph addDecl(IOccurrence declaration, IScope scope);

    IScopeGraph addRef(IOccurrence reference, IScope scope);

    IScopeGraph addLink(IScope source, ILabel label, IScope target);

    IScopeGraph addExport(IOccurrence declaration, ILabel label, IScope scope);

    IScopeGraph addImport(IOccurrence reference, ILabel label, IScope scope);

}