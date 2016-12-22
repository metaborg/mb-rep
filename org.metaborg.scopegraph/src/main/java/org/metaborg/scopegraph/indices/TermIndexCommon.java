package org.metaborg.scopegraph.indices;

import org.metaborg.scopegraph.ScopeGraphException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.visitor.AStrategoTermVisitor;
import org.spoofax.terms.visitor.StrategoTermVisitee;

public class TermIndexCommon {

    private TermIndexCommon() {
    }

    public static void indexTerm(final String resource, final IStrategoTerm term) {
        StrategoTermVisitee.topdown(new AStrategoTermVisitor() {

            private int currentId = 0;

            @Override public boolean visit(IStrategoTerm term) {
                TermIndex.put(term, resource, ++currentId);
                if (term.isList()) {
                    currentId += term.getSubtermCount();
                }
                return true;
            }
        }, term);
    }

    public static void indexSublist(IStrategoTerm list, IStrategoTerm sublist) throws ScopeGraphException {
        if (!list.isList()) {
            throw new ScopeGraphException("List term is not a list.");
        }
        if (!sublist.isList()) {
            throw new ScopeGraphException("Sublist term is not a list.");
        }
        int listCount = list.getSubtermCount();
        int sublistCount = sublist.getSubtermCount();
        if (sublistCount > listCount) {
            throw new ScopeGraphException("Sublist cannot be longer than original list.");
        }

        TermIndex index = TermIndex.get(list);
        if (index == null) {
            throw new ScopeGraphException("List has no index.");
        }

        int skip = listCount - sublistCount;
        TermIndex.put(sublist, index.resource(), index.nodeId() + skip);
    }

}
