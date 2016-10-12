package org.metaborg.solver.constraints;

import org.metaborg.unification.ITerm;

public interface ICResolve extends IConstraint {

    ITerm getReference();
    
    ITerm getDeclaration();
    
}
