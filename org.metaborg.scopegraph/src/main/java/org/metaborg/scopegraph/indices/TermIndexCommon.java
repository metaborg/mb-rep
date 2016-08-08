package org.metaborg.scopegraph.indices;

import org.metaborg.scopegraph.ScopeGraphException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.visitor.AStrategoTermVisitor;
import org.spoofax.terms.visitor.StrategoTermVisitee;

public class TermIndexCommon {

    private TermIndexCommon() {}
    
    public static void indexTerm(final String resource, final IStrategoTerm term) {
        StrategoTermVisitee.topdown(new AStrategoTermVisitor() {
            private int currentId = 0;
            @Override public boolean visit(IStrategoTerm term) {
                TermIndex.put(term, resource, ++currentId);
                if(term.isList()) {
                    currentId += term.getSubtermCount();
                }
                return true;
            }
        }, term);
    }

    public static void indexSublist(IStrategoTerm terms, IStrategoTerm iStrategoTerm) throws ScopeGraphException {
        if(!terms.isList()) {
            throw new ScopeGraphException("List term is not a list.");
        }
        if(!iStrategoTerm.isList()) {
            throw new ScopeGraphException("Sublist term is not a list.");
        }
        int listCount = terms.getSubtermCount();
        int sublistCount = iStrategoTerm.getSubtermCount();
        if(sublistCount > listCount) {
            throw new ScopeGraphException("Sublist cannot be longer than original list.");
        }

        TermIndex index = TermIndex.get(terms);
        if(index == null) {
            throw new ScopeGraphException("List has no index.");
        }

        int skip = listCount - sublistCount;
        TermIndex.put(iStrategoTerm, index.resource(), index.nodeId()+skip);
    }
    
}
