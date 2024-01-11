package org.spoofax;

import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTermBuilder;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.io.TAFTermReader;

import jakarta.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Utility methods for tests.
 */
public final class TestUtils {

    public static final String TEST_INSTANCE_NOT_CREATED = "A test instance with the given parameters could not be " +
            "created.";

    private static final TermFactory termFactory = new TermFactory();

    public static IStrategoTermBuilder getTermBuilder() {
        return termFactory;
    }

    /**
     * Attempts to casts all elements in the specified iterable to a different type.
     *
     * @param input the iterable
     * @param <R>   the type to cast to
     * @return the cast list; or {@code null} when casting failed or the input was {@code null}
     */
    @Nullable
    public static <R> List<R> tryCastAll(@Nullable Iterable<?> input) {
        if (input == null) return null;
        try {
            //noinspection unchecked
            return StreamSupport.stream(input.spliterator(), false).map(e -> (R)e).collect(Collectors.toList());
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Puts all given annotations on the given term and returns the resulting term.
     *
     * @param term        the term to modify
     * @param termFactory the term factory to use
     * @param annotations the annotations to add; or {@code null}
     * @param <T>         the type of term
     * @return the resulting term
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends IStrategoTerm> T putAnnotations(@Nullable T term, ITermFactory termFactory,
                                                           @Nullable List<IStrategoTerm> annotations) {
        if (term == null) return null;
        return (T)termFactory.annotateTerm(term, termFactory.makeList(annotations));
    }

    /**
     * Puts all given attachments in the given term and returns the resulting term.
     * <p>
     * If there are multiple term attachments of the same type, the later attachments
     * override the earlier ones.
     *
     * @param term        the term to modify
     * @param attachments the attachments to attach; or {@code null}
     * @param <T>         the type of term
     * @return the resulting term
     */
    @Nullable
    public static <T extends ISimpleTerm> T putAttachments(@Nullable T term,
                                                           @Nullable List<ITermAttachment> attachments) {
        if (term == null) return null;
        if (attachments != null && !attachments.isEmpty()) {
            for (ITermAttachment attachment : attachments) {
                term.putAttachment(attachment);
            }
        }
        return term;
    }

    /**
     * Reads a term from a test resource.
     *
     * @param resourcePath the path to the test resource
     * @param termFactory the term factory
     * @return the read term
     * @throws IOException an I/O exception occurred
     */
    public static IStrategoTerm readTermFromTestResource(String resourcePath, ITermFactory termFactory) throws IOException {
        try(final @Nullable InputStream stream = TestUtils.class.getResourceAsStream(resourcePath)) {
            assertNotNull(stream, "Cannot find required test resource " + resourcePath);
            return new TAFTermReader(termFactory).parseFromStream(stream);
        }
    }

    /**
     * Reads a term from a test resource.
     *
     * @param resourcePath the path to the test resource
     * @return the read term
     * @throws IOException an I/O exception occurred
     */
    public static byte[] readBytesFromTestResource(String resourcePath) throws IOException {
        try(final @Nullable InputStream stream = TestUtils.class.getResourceAsStream(resourcePath)) {
            assertNotNull(stream, "Cannot find required test resource " + resourcePath);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int read;
            byte[] data = new byte[0x4000];
            while ((read = stream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, read);
            }
            return buffer.toByteArray();
        }
    }

}
