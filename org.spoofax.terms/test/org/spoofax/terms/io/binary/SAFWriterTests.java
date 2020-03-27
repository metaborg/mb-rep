package org.spoofax.terms.io.binary;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.spoofax.TestUtils;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/** Tests the {@link SAFWriter} class. */
@DisplayName("SAFWriter")
public class SAFWriterTests {

    @Test
    @DisplayName("writes expected bytes to stream")
    public void writesExpectedBytesToStream() throws IOException {
        // Arrange
        ITermFactory factory = new TermFactory();
        IStrategoTerm inputTerm = TestUtils.readTermFromTestResource("/Sdf2.tbl", factory);
        byte[] expected = TestUtils.readBytesFromTestResource("/Sdf2.tbl.bin");

        // Act
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SAFWriter.writeTermToSAFStream(inputTerm, baos);
        baos.flush();
        byte[] result = baos.toByteArray();

        // Assert
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("writes expected bytes to byte array")
    public void writesExpectedBytesToByteArray() throws IOException {
        // Arrange
        ITermFactory factory = new TermFactory();
        IStrategoTerm inputTerm = TestUtils.readTermFromTestResource("/Sdf2.tbl", factory);
        byte[] expected = TestUtils.readBytesFromTestResource("/Sdf2.tbl.bin");

        // Act
        byte[] result = SAFWriter.writeTermToSAFString(inputTerm);

        // Assert
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("writes expected bytes to file")
    public void writesExpectedBytesToFile(@TempDir Path tempDir) throws IOException {
        // Arrange
        ITermFactory factory = new TermFactory();
        IStrategoTerm inputTerm = TestUtils.readTermFromTestResource("/Sdf2.tbl", factory);
        byte[] expected = TestUtils.readBytesFromTestResource("/Sdf2.tbl.bin");
        Path tmpFilePath = tempDir.resolve("myfile.bin");
        File tmpFile = tmpFilePath.toFile();

        // Act
        SAFWriter.writeTermToSAFFile(inputTerm, tmpFile);

        // Assert
        byte[] result = Files.readAllBytes(tmpFilePath);
        assertArrayEquals(expected, result);
    }

}
