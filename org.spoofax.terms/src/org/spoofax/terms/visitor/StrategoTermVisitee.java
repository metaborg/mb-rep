package org.spoofax.terms.visitor;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.spoofax.interpreter.terms.*;

public class StrategoTermVisitee {
    public static void topdown(IStrategoTermVisitor visitor, IStrategoTerm initialTerm) {
        final Stack<IStrategoTerm> stack = new Stack<IStrategoTerm>();
        stack.push(initialTerm);
        while(!stack.empty()) {
            final IStrategoTerm term = stack.pop();
            if(!dispatch(visitor, term)) {
                continue;
            }
            for(int i = term.getSubtermCount() -1; i >= 0; --i) {
                stack.push(term.getSubterm(i));
            }
        }
    }
    
    public static void bottomup(IStrategoTermVisitor visitor, IStrategoTerm initialTerm) {
        final Stack<IStrategoTerm> stack = new Stack<IStrategoTerm>();
        final Set<IStrategoTerm> visited = new HashSet<IStrategoTerm>();
        stack.push(initialTerm);
        while(!stack.isEmpty()) {
            final IStrategoTerm term = stack.peek();
            if(term.getSubtermCount() == 0 || visited.contains(term)) {
                dispatch(visitor, term);
                stack.pop();
                visited.remove(term);
            } else {
                visited.add(term);
                for(int i = term.getSubtermCount() -1; i >= 0; --i) {
                    stack.push(term.getSubterm(i));
                }
            }
        }
    }

    private static boolean dispatch(IStrategoTermVisitor visitor, IStrategoTerm term) {
        switch(term.getType()) {
            case APPL:
                return visitor.visit((IStrategoAppl) term);
            case LIST:
                return visitor.visit((IStrategoList) term);
            case TUPLE:
                return visitor.visit((IStrategoTuple) term);
            case INT:
                visitor.visit((IStrategoInt) term);
                return false;
            case REAL:
                visitor.visit((IStrategoReal) term);
                return false;
            case STRING:
                visitor.visit((IStrategoString) term);
                return false;
            case REF:
                visitor.visit((IStrategoRef) term);
                return false;
            case PLACEHOLDER:
                return visitor.visit((IStrategoPlaceholder) term);
            default:
                return visitor.visit(term);
        }
    }
}
