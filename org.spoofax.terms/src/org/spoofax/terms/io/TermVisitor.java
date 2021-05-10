package org.spoofax.terms.io;

/**
 * Term visitor for ATerm readers.
 * 
 * <pre>
 * visit = visitInt visitAnnotation* endInt
 *       | visitReal visitAnnotation* endReal
 *       | visitString visitAnnotation* endString
 *       | visitAppl visitSubTerm* visitAnnotation* endAppl
 *       | visitTuple visitSubTerm* visitAnnotation* endTuple
 *       | visitList visitSubTerm* visitAnnotation* endList
 *       | visitPlaceholder
 * </pre>
 * 
 */
public interface TermVisitor {

    static TermVisitor NOOP = new TermVisitor() {

        @Override public void visitInt(int value) {
        }

        @Override public void endInt() {
        }


        @Override public void visitReal(double value) {
        }

        @Override public void endReal() {
        }


        @Override public void visitString(String value) {
        }

        @Override public void endString() {
        }


        @Override public void visitAppl(String name) {
        }

        @Override public void endAppl() {
        }


        @Override public void visitTuple() {
        }

        @Override public void endTuple() {
        }


        @Override public void visitList() {
        }

        @Override public void endList() {
        }


        @Override public TermVisitor visitPlaceholder() {
            return NOOP;
        }

        @Override public TermVisitor visitSubTerm() {
            return NOOP;
        }

        @Override public TermVisitor visitAnnotation() {
            return NOOP;
        }

    };

    void visitInt(int value);

    void endInt();


    void visitReal(double value);

    void endReal();


    void visitString(String value);

    void endString();


    void visitAppl(String name);

    void endAppl();


    void visitTuple();

    void endTuple();


    void visitList();

    void endList();


    TermVisitor visitPlaceholder();

    TermVisitor visitSubTerm();

    TermVisitor visitAnnotation();

}