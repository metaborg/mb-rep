package org.spoofax.terms.io;

import org.junit.jupiter.api.Test;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermFactory;

public abstract class AbstractTermReaderTests {
    protected final TermFactory factory = new TermFactory();

    @Test public void testString() {
        testString("a");
        testString("\u4200");
        testString("\uD83D\uDE00"); // Two UTF-16 code points representing 😀
        testString("😀");
    }

    protected abstract void testString(String str);

    @Test public void testInt() {
        testInt(-1);
        testInt(0);
        testInt(1);
        testInt(42);
    }

    protected abstract void testInt(int i);

    @Test public void testReal() {
        testReal("-1.0");
        testReal("0.0");
        testReal("1.0");
        testReal("42.42e42");
        testReal("-42.42E-42");
    }

    protected abstract void testReal(String str);

    @Test public void testFullTerm() {
        IStrategoTerm term = factory.annotateTerm( //
            factory.makeAppl( //
                factory.makeConstructor("Big", 2), //
                factory.makeString("str"), //
                factory.makeTuple(factory.makeInt(42), factory.makeList(factory.makeReal(3.14)))),
            factory.makeList(factory.makeInt(1)));
        testFullTerm(term, "Big(\"str\", (42, [3.14])){1}");
    }

    protected abstract void testFullTerm(IStrategoTerm term, String str);
}
