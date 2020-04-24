package org.spoofax.terms.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

/** Tests the {@link TAFTermReader} class, using plain String representations. */
@DisplayName("TAFTermReader")
public final class TAFTermReaderTests extends AbstractTermReaderTests {

    private final TAFTermReader termReader = new TAFTermReader(factory);

    @Override protected void testString(String str) {
        String quoted = "\"" + str + "\"";
        assertEquals(factory.makeString(str), termReader.parseFromString(quoted));
        assertEquals(str, ((IStrategoString) termReader.parseFromString(quoted)).stringValue());
    }

    @Override protected void testInt(int i) {
        String str = String.valueOf(i);
        assertEquals(factory.makeInt(i), termReader.parseFromString(str));
        assertEquals(i, ((IStrategoInt) termReader.parseFromString(str)).intValue());
    }

    @Override protected void testReal(String str) {
        double real = Double.parseDouble(str);
        assertEquals(factory.makeReal(real), termReader.parseFromString(str));
        assertEquals(real, ((IStrategoReal) termReader.parseFromString(str)).realValue());
    }

    @Override protected void testFullTerm(IStrategoTerm term, String str) {
        assertEquals(term, termReader.parseFromString(str));
    }

    @Test public void testPlaceholder() {
        assertEquals(factory.makePlaceholder(factory.makeInt(42)), termReader.parseFromString("<42>"));
    }

}
