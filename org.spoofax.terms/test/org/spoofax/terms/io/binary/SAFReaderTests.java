package org.spoofax.terms.io.binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.annotation.Nullable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.spoofax.TestUtils;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.AbstractTermReaderTests;
import org.spoofax.terms.io.TAFTermReader;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/** Tests the {@link SAFReader} class. */
@DisplayName("SAFReader")
public final class SAFReaderTests extends AbstractTermReaderTests {

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

    @Test
    public void testTermFromFile() throws IOException {
        final IStrategoTerm ptTerm = TestUtils.readTermFromTestResource("/Sdf2.tbl", factory);
        serializeAndDeserialize(ptTerm);
    }


    @Test
    @DisplayName("reads expected term from stream")
    public void readsExpectedTermFromStream() throws IOException {
        // Arrange
        ITermFactory factory = new TermFactory();
        IStrategoTerm expected = TestUtils.readTermFromTestResource("/Sdf2.tbl", factory);
        ByteArrayInputStream bais = new ByteArrayInputStream(TestUtils.readBytesFromTestResource("/Sdf2.tbl.bin"));

        // Act
        IStrategoTerm result = SAFReader.readTermFromSAFStream(factory, bais);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("reads expected term from byte array")
    public void readsExpectedTermFromByteArray() throws IOException {
        // Arrange
        ITermFactory factory = new TermFactory();
        IStrategoTerm expected = TestUtils.readTermFromTestResource("/Sdf2.tbl", factory);
        byte[] inputBytes = TestUtils.readBytesFromTestResource("/Sdf2.tbl.bin");

        // Act
        IStrategoTerm result = SAFReader.readTermFromSAFString(factory, inputBytes);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("reads expected term from file")
    public void readsExpectedTermFromFile(@TempDir Path tempDir) throws IOException {
        // Arrange
        ITermFactory factory = new TermFactory();
        IStrategoTerm expected = TestUtils.readTermFromTestResource("/Sdf2.tbl", factory);
        Path tmpFilePath = tempDir.resolve("myfile.bin");
        try(final @Nullable InputStream stream = TestUtils.class.getResourceAsStream("/Sdf2.tbl.bin")) {
            assertNotNull(stream, "Cannot find required test resource " + "/Sdf2.tbl.bin");
            Files.copy(stream, tmpFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
        File tmpFile = tmpFilePath.toFile();

        // Act
        IStrategoTerm result = SAFReader.readTermFromSAFFile(factory, tmpFile);

        // Assert
        assertEquals(expected, result);
    }
}
