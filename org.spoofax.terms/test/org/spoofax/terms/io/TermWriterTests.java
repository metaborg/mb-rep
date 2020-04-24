package org.spoofax.terms.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.opentest4j.TestAbortedException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests the {@link TermWriter} interface. */
@DisplayName("TermWriter")
public interface TermWriterTests {

    interface Fixture {
        /**
         * Creates a new instance of the {@link TermWriter} for testing.
         *
         * @return the created object
         * @throws TestAbortedException when an instance with the given parameters could not be created
         */
        TermWriter createTermWriter();

        /**
         * Gets the term factory to use.
         *
         * @return the term factory
         */
        ITermFactory getFactory();
    }

    /** Tests the {@link TermWriter#writeToBytes} method. */
    @DisplayName("writeToBytes()")
    interface WriteToBytesTests extends Fixture {

        @TestFactory
        default Stream<DynamicTest> returnsABinaryRepresentation() throws IOException {
            return Stream.of(
                returnsABinaryRepresentationTest(getFactory().makeAppl("MyCons", getFactory().makeString("mystring"))),
                returnsABinaryRepresentationTest(getFactory().makeTuple(getFactory().makeString("mystring"))),
                returnsABinaryRepresentationTest(getFactory().makeList(getFactory().makeString("mystring"))),
                returnsABinaryRepresentationTest(getFactory().makePlaceholder(getFactory().makeString("mystring"))),
                returnsABinaryRepresentationTest(getFactory().makeString("mystring")),
                returnsABinaryRepresentationTest(getFactory().makeInt(42)),
                returnsABinaryRepresentationTest(getFactory().makeReal(13.37))
            );
        }

        default DynamicTest returnsABinaryRepresentationTest(IStrategoTerm term) throws IOException {
            return DynamicTest.dynamicTest("writes a string representation of " + term.toString(), () -> {
                // Arrange
                TermWriter sut = createTermWriter();

                // Act
                byte[] result = sut.writeToBytes(term);

                // Assert
                assertTrue(result.length > 0);
            });
        }

    }


    /** Tests the {@link TermWriter#write(IStrategoTerm, OutputStream)} method. */
    @DisplayName("write(IStrategoTerm, OutputStream)")
    interface WriteTests extends Fixture {

        @TestFactory
        default Stream<DynamicTest> writesARepresentation() throws IOException {
            return Stream.of(
                writesARepresentationTest(getFactory().makeAppl("MyCons", getFactory().makeString("mystring"))),
                writesARepresentationTest(getFactory().makeTuple(getFactory().makeString("mystring"))),
                writesARepresentationTest(getFactory().makeList(getFactory().makeString("mystring"))),
                writesARepresentationTest(getFactory().makePlaceholder(getFactory().makeString("mystring"))),
                writesARepresentationTest(getFactory().makeString("mystring")),
                writesARepresentationTest(getFactory().makeInt(42)),
                writesARepresentationTest(getFactory().makeReal(13.37))
            );
        }

        default DynamicTest writesARepresentationTest(IStrategoTerm term) throws IOException {
            return DynamicTest.dynamicTest("writes a string representation of " + term.toString(), () -> {
                // Arrange
                TermWriter sut = createTermWriter();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                // Act
                sut.write(term, stream);

                // Assert
                stream.flush();
                byte[] result = stream.toByteArray();
                assertTrue(result.length > 0);
            });
        }

    }


}
