package org.spoofax.terms.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.opentest4j.TestAbortedException;
import org.spoofax.interpreter.terms.IStrategoTerm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.spoofax.terms.util.Assert.assertEquals;

/** Tests the {@link TextTermWriter} interface. */
@DisplayName("TextTermWriter")
public interface TextTermWriterTests {

    interface Fixture extends TermWriterTests.Fixture {
        /**
         * Creates a new instance of the {@link TextTermWriter} for testing.
         *
         * @return the created object
         * @throws TestAbortedException when an instance with the given parameters could not be created
         */
        @Override
        TextTermWriter createTermWriter();
    }

    /** Tests the {@link TextTermWriter#writeToString} method. */
    @DisplayName("writeToString()")
    interface WriteToStringTests extends Fixture {

        @TestFactory
        default Stream<DynamicTest> writesAStringRepresentation() throws IOException {
            return Stream.of(
                writesAStringRepresentationTest(getFactory().makeAppl("MyCons", getFactory().makeString("mystring"))),
                writesAStringRepresentationTest(getFactory().makeTuple(getFactory().makeString("mystring"))),
                writesAStringRepresentationTest(getFactory().makeList(getFactory().makeString("mystring"))),
                writesAStringRepresentationTest(getFactory().makePlaceholder(getFactory().makeString("mystring"))),
                writesAStringRepresentationTest(getFactory().makeString("mystring")),
                writesAStringRepresentationTest(getFactory().makeInt(42)),
                writesAStringRepresentationTest(getFactory().makeReal(13.37))
            );
        }

        default DynamicTest writesAStringRepresentationTest(IStrategoTerm term) throws IOException {
            return DynamicTest.dynamicTest("writes a string representation of " + term.toString(), () -> {
                // Arrange
                TextTermWriter sut = createTermWriter();

                // Act
                String result = sut.writeToString(term);

                // Assert
                assertFalse(result.isEmpty());
            });
        }

    }

    /** Tests the {@link TextTermWriter#write(IStrategoTerm, OutputStream)} method. */
    @DisplayName("write(IStrategoTerm, OutputStream)")
    interface WriteTests extends TermWriterTests.WriteTests, Fixture {

        @TestFactory
        default Stream<DynamicTest> writesAStringRepresentation() throws IOException {
            return Stream.of(
                writesAStringRepresentationTest(getFactory().makeAppl("MyCons", getFactory().makeString("mystring"))),
                writesAStringRepresentationTest(getFactory().makeTuple(getFactory().makeString("mystring"))),
                writesAStringRepresentationTest(getFactory().makeList(getFactory().makeString("mystring"))),
                writesAStringRepresentationTest(getFactory().makePlaceholder(getFactory().makeString("mystring"))),
                writesAStringRepresentationTest(getFactory().makeString("mystring")),
                writesAStringRepresentationTest(getFactory().makeInt(42)),
                writesAStringRepresentationTest(getFactory().makeReal(13.37))
            );
        }

        default DynamicTest writesAStringRepresentationTest(IStrategoTerm term) throws IOException {
            return DynamicTest.dynamicTest("writes an UTF-8 string representation of " + term.toString(), () -> {
                // Arrange
                TextTermWriter sut = createTermWriter();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                // Act
                sut.write(term, stream);

                // Assert
                stream.flush();
                String result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
                assertFalse(result.isEmpty());
            });
        }

    }

    /** Tests the {@link TextTermWriter#write(IStrategoTerm, OutputStream, Charset)} method. */
    @DisplayName("write(IStrategoTerm, OutputStream, Charset)")
    interface WriteCharsetTests extends TermWriterTests.WriteTests, Fixture {

        @TestFactory
        default Stream<DynamicTest> writesAnEncodedStringRepresentationInCharset() throws IOException {
            return Stream.of(
                writesAnEncodedStringRepresentationInCharsetTest(getFactory().makeAppl("MyCons", getFactory().makeString("mystring"))),
                writesAnEncodedStringRepresentationInCharsetTest(getFactory().makeTuple(getFactory().makeString("mystring"))),
                writesAnEncodedStringRepresentationInCharsetTest(getFactory().makeList(getFactory().makeString("mystring"))),
                writesAnEncodedStringRepresentationInCharsetTest(getFactory().makePlaceholder(getFactory().makeString("mystring"))),
                writesAnEncodedStringRepresentationInCharsetTest(getFactory().makeString("mystring")),
                writesAnEncodedStringRepresentationInCharsetTest(getFactory().makeInt(42)),
                writesAnEncodedStringRepresentationInCharsetTest(getFactory().makeReal(13.37))
            );
        }

        default DynamicTest writesAnEncodedStringRepresentationInCharsetTest(IStrategoTerm term) throws IOException {
            return DynamicTest.dynamicTest("writes an encoded string representation of " + term.toString(), () -> {
                // Arrange
                TextTermWriter sut = createTermWriter();
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                Charset charset1 = StandardCharsets.UTF_8;
                Charset charset2 = StandardCharsets.UTF_16BE;

                // Act
                sut.write(term, stream1, charset1);
                sut.write(term, stream2, charset2);

                // Assert
                stream1.flush();
                stream2.flush();
                String result1 = new String(stream1.toByteArray(), charset1);
                String result2 = new String(stream2.toByteArray(), charset2);
                assertEquals(result1, result2);
            });
        }

    }

    /** Tests the {@link TextTermWriter#write(IStrategoTerm, Appendable)} method. */
    @DisplayName("write(IStrategoTerm, Appendable)")
    interface WriteAppendableTests extends Fixture {

        @TestFactory
        default Stream<DynamicTest> writesAStringRepresentation() throws IOException {
            return Stream.of(
                writesAStringRepresentationTest(getFactory().makeAppl("MyCons", getFactory().makeString("mystring"))),
                writesAStringRepresentationTest(getFactory().makeTuple(getFactory().makeString("mystring"))),
                writesAStringRepresentationTest(getFactory().makeList(getFactory().makeString("mystring"))),
                writesAStringRepresentationTest(getFactory().makePlaceholder(getFactory().makeString("mystring"))),
                writesAStringRepresentationTest(getFactory().makeString("mystring")),
                writesAStringRepresentationTest(getFactory().makeInt(42)),
                writesAStringRepresentationTest(getFactory().makeReal(13.37))
            );
        }

        default DynamicTest writesAStringRepresentationTest(IStrategoTerm term) throws IOException {
            return DynamicTest.dynamicTest("writes a string representation of " + term.toString(), () -> {
                // Arrange
                TextTermWriter sut = createTermWriter();
                StringBuilder sb = new StringBuilder();

                // Act
                sut.write(term, sb);

                // Assert
                String result = sb.toString();
                assertFalse(result.isEmpty());
            });
        }

    }

}
