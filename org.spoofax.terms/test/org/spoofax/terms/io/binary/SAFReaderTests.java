package org.spoofax.terms.io.binary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.io.AbstractTermReaderTests;

/** Tests the {@link SAFReader} class. */
@DisplayName("SAFReader")
public final class SAFReaderTests extends AbstractTermReaderTests {

    private final SAFReader termReader = new SAFReader(factory);

    @Override protected void testString(String str) {
        serializeAndDeserialize(factory.makeString(str));
    }

    @Override protected void testInt(int i) {
        serializeAndDeserialize(factory.makeInt(i));
    }

    @Override protected void testReal(String str) {
        double real = Double.parseDouble(str);
        serializeAndDeserialize(factory.makeReal(real));
    }

    @Override protected void testFullTerm(IStrategoTerm term, String str) {
        serializeAndDeserialize(term);
    }

    private void serializeAndDeserialize(IStrategoTerm term) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            SAFWriter.writeTermToSAFStream(term, out);
            byte[] serialized = out.toByteArray();
            IStrategoTerm deserialized = SAFReader.readTermFromSAFStream(factory, new ByteArrayInputStream(serialized));
            assertEquals(term, deserialized);
        } catch(IOException e) {
            fail(e);
        }
    }
}
