package org.spoofax.terms.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;

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

    /** Tests the {@link TermWriter#writeToString} method. */
    @DisplayName("writeToString()")
    interface WriteToStringTests extends Fixture {

        @Test
        @DisplayName("returns a string representation of an Appl term")
        default void returnsAStringRepresentationOfAnApplTerm() {
            // Arrange
            IStrategoTerm term = getFactory().makeAppl("MyCons", getFactory().makeString("mystring"));
            TermWriter sut = createTermWriter();

            // Act
            String result = sut.writeToString(term);

            // Assert
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("returns a string representation of a Tuple term")
        default void returnsAStringRepresentationOfATupleTerm() {
            // Arrange
            IStrategoTerm term = getFactory().makeTuple(getFactory().makeString("mystring"));
            TermWriter sut = createTermWriter();

            // Act
            String result = sut.writeToString(term);

            // Assert
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("returns a string representation of a List term")
        default void returnsAStringRepresentationOfAListTerm() {
            // Arrange
            IStrategoTerm term = getFactory().makeList(getFactory().makeString("mystring"));
            TermWriter sut = createTermWriter();

            // Act
            String result = sut.writeToString(term);

            // Assert
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("returns a string representation of a Placeholder term")
        default void returnsAStringRepresentationOfAPlaceholderTerm() {
            // Arrange
            IStrategoTerm term = getFactory().makePlaceholder(getFactory().makeString("mystring"));
            TermWriter sut = createTermWriter();

            // Act
            String result = sut.writeToString(term);

            // Assert
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("returns a string representation of a String term")
        default void returnsAStringRepresentationOfAStringTerm() {
            // Arrange
            IStrategoTerm term = getFactory().makeString("mystring");
            TermWriter sut = createTermWriter();

            // Act
            String result = sut.writeToString(term);

            // Assert
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("returns a string representation of an Int term")
        default void returnsAStringRepresentationOfAnIntTerm() {
            // Arrange
            IStrategoTerm term = getFactory().makeInt(42);
            TermWriter sut = createTermWriter();

            // Act
            String result = sut.writeToString(term);

            // Assert
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("returns a string representation of a Real term")
        default void returnsAStringRepresentationOfARealTerm() {
            // Arrange
            IStrategoTerm term = getFactory().makeReal(13.37);
            TermWriter sut = createTermWriter();

            // Act
            String result = sut.writeToString(term);

            // Assert
            assertFalse(result.isEmpty());
        }

    }


    /** Tests the {@link TermWriter#write(IStrategoTerm, OutputStream)} method. */
    @DisplayName("write(IStrategoTerm, OutputStream)")
    interface WriteTests1 extends Fixture {

        @Test
        @DisplayName("writes a string representation of an Appl term")
        default void writesAStringRepresentationOfAnApplTerm() throws IOException {
            // Arrange
            IStrategoTerm term = getFactory().makeAppl("MyCons", getFactory().makeString("mystring"));
            TermWriter sut = createTermWriter();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            // Act
            sut.write(term, stream);

            // Assert
            String result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("writes a string representation of a Tuple term")
        default void writesAStringRepresentationOfATupleTerm() throws IOException {
            // Arrange
            IStrategoTerm term = getFactory().makeTuple(getFactory().makeString("mystring"));
            TermWriter sut = createTermWriter();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            // Act
            sut.write(term, stream);

            // Assert
            String result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("writes a string representation of a List term")
        default void writesAStringRepresentationOfAListTerm() throws IOException {
            // Arrange
            IStrategoTerm term = getFactory().makeList(getFactory().makeString("mystring"));
            TermWriter sut = createTermWriter();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            // Act
            sut.write(term, stream);

            // Assert
            String result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("writes a string representation of a Placeholder term")
        default void writesAStringRepresentationOfAPlaceholderTerm() throws IOException {
            // Arrange
            IStrategoTerm term = getFactory().makePlaceholder(getFactory().makeString("mystring"));
            TermWriter sut = createTermWriter();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            // Act
            sut.write(term, stream);

            // Assert
            String result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("writes a string representation of a String term")
        default void writesAStringRepresentationOfAStringTerm() throws IOException {
            // Arrange
            IStrategoTerm term = getFactory().makeString("mystring");
            TermWriter sut = createTermWriter();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            // Act
            sut.write(term, stream);

            // Assert
            String result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("writes a string representation of an Int term")
        default void writesAStringRepresentationOfAnIntTerm() throws IOException {
            // Arrange
            IStrategoTerm term = getFactory().makeInt(42);
            TermWriter sut = createTermWriter();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            // Act
            sut.write(term, stream);

            // Assert
            String result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("writes a string representation of a Real term")
        default void writesAStringRepresentationOfARealTerm() throws IOException {
            // Arrange
            IStrategoTerm term = getFactory().makeReal(13.37);
            TermWriter sut = createTermWriter();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            // Act
            sut.write(term, stream);

            // Assert
            stream.flush();
            String result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
            assertFalse(result.isEmpty());
        }

    }

    /** Tests the {@link TermWriter#write(IStrategoTerm, Appendable)} method. */
    @DisplayName("write(IStrategoTerm, Appendable)")
    interface WriteTests2 extends Fixture {

        @Test
        @DisplayName("writes a string representation of an Appl term")
        default void writesAStringRepresentationOfAnApplTerm() throws IOException {
            // Arrange
            IStrategoTerm term = getFactory().makeAppl("MyCons", getFactory().makeString("mystring"));
            TermWriter sut = createTermWriter();
            StringBuilder sb = new StringBuilder();

            // Act
            sut.write(term, sb);

            // Assert
            String result = sb.toString();
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("writes a string representation of a Tuple term")
        default void writesAStringRepresentationOfATupleTerm() throws IOException {
            // Arrange
            IStrategoTerm term = getFactory().makeTuple(getFactory().makeString("mystring"));
            TermWriter sut = createTermWriter();
            StringBuilder sb = new StringBuilder();

            // Act
            sut.write(term, sb);

            // Assert
            String result = sb.toString();
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("writes a string representation of a List term")
        default void writesAStringRepresentationOfAListTerm() throws IOException {
            // Arrange
            IStrategoTerm term = getFactory().makeList(getFactory().makeString("mystring"));
            TermWriter sut = createTermWriter();
            StringBuilder sb = new StringBuilder();

            // Act
            sut.write(term, sb);

            // Assert
            String result = sb.toString();
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("writes a string representation of a Placeholder term")
        default void writesAStringRepresentationOfAPlaceholderTerm() throws IOException {
            // Arrange
            IStrategoTerm term = getFactory().makePlaceholder(getFactory().makeString("mystring"));
            TermWriter sut = createTermWriter();
            StringBuilder sb = new StringBuilder();

            // Act
            sut.write(term, sb);

            // Assert
            String result = sb.toString();
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("writes a string representation of a String term")
        default void writesAStringRepresentationOfAStringTerm() throws IOException {
            // Arrange
            IStrategoTerm term = getFactory().makeString("mystring");
            TermWriter sut = createTermWriter();
            StringBuilder sb = new StringBuilder();

            // Act
            sut.write(term, sb);

            // Assert
            String result = sb.toString();
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("writes a string representation of an Int term")
        default void writesAStringRepresentationOfAnIntTerm() throws IOException {
            // Arrange
            IStrategoTerm term = getFactory().makeInt(42);
            TermWriter sut = createTermWriter();
            StringBuilder sb = new StringBuilder();

            // Act
            sut.write(term, sb);

            // Assert
            String result = sb.toString();
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("writes a string representation of a Real term")
        default void writesAStringRepresentationOfARealTerm() throws IOException {
            // Arrange
            IStrategoTerm term = getFactory().makeReal(13.37);
            TermWriter sut = createTermWriter();
            StringBuilder sb = new StringBuilder();

            // Act
            sut.write(term, sb);

            // Assert
            String result = sb.toString();
            assertFalse(result.isEmpty());
        }

    }

}
