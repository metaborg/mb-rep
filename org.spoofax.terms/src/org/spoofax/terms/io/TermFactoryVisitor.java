package org.spoofax.terms.io;

import static org.spoofax.terms.AbstractTermFactory.EMPTY_TERM_ARRAY;

import java.util.ArrayList;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public abstract class TermFactoryVisitor implements TermVisitor {

    private final ITermFactory factory;

    public TermFactoryVisitor(ITermFactory factory) {
        this.factory = factory;
    }

    public abstract void setTerm(IStrategoTerm term);


    private Integer i;

    @Override public void visitInt(int value) {
        visit();
        this.i = value;
    }

    @Override public void endInt() {
        setTerm(withAnnos(factory.makeInt(i)));
    }


    private Double d;

    @Override public void visitReal(double value) {
        visit();
        this.d = value;
    }

    @Override public void endReal() {
        setTerm(withAnnos(factory.makeReal(d)));
    }


    private String s;

    @Override public void visitString(String value) {
        visit();
        this.s = value;
    }

    @Override public void endString() {
        setTerm(withAnnos(factory.makeString(s)));
    }


    private String c;

    @Override public void visitAppl(String name) {
        visit();
        this.c = name;
    }

    @Override public void endAppl() {
        IStrategoConstructor c = factory.makeConstructor(this.c, subTerms.size());
        setTerm(withAnnos(factory.makeAppl(c, subTerms.toArray(EMPTY_TERM_ARRAY))));
    }


    @Override public void visitTuple() {
        visit();
    }

    @Override public void endTuple() {
        setTerm(withAnnos(factory.makeTuple(subTerms.toArray(EMPTY_TERM_ARRAY))));
    }


    @Override public void visitList() {
        visit();
    }

    @Override public void endList() {
        setTerm(withAnnos(factory.makeList(subTerms.toArray(EMPTY_TERM_ARRAY))));
    }


    @Override public TermVisitor visitPlaceholder() {
        visit();
        final TermFactoryVisitor outer = this;
        return new TermFactoryVisitor(factory) {
            @Override public void setTerm(IStrategoTerm term) {
                outer.setTerm(factory.makePlaceholder(term));
            }
        };
    }


    List<IStrategoTerm> subTerms = new ArrayList<>();

    @Override public TermVisitor visitSubTerm() {
        final TermFactoryVisitor outer = this;
        return new TermFactoryVisitor(factory) {
            @Override public void setTerm(IStrategoTerm subTerm) {
                outer.subTerms.add(subTerm);
            }
        };
    }


    List<IStrategoTerm> annos = new ArrayList<>();

    @Override public TermVisitor visitAnnotation() {
        final TermFactoryVisitor outer = this;
        return new TermFactoryVisitor(factory) {
            @Override public void setTerm(IStrategoTerm anno) {
                outer.annos.add(anno);
            }
        };
    }

    private IStrategoTerm withAnnos(IStrategoTerm term) {
        if(annos.isEmpty()) {
            return term;
        } else {
            return factory.annotateTerm(term, factory.makeList(annos));
        }
    }

    private void visit() {
        i = null;
        d = null;
        s = null;
        c = null;
        subTerms.clear();
        annos.clear();
    }

}