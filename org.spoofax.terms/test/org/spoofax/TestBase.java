package org.spoofax;

import org.spoofax.interpreter.terms.IStrategoTermBuilder;
import org.spoofax.terms.TermFactory;

import javax.annotation.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


/**
 * Base class for term tests.
 */
public abstract class TestBase {

    public static final String TEST_INSTANCE_NOT_CREATED = "A test instance with the given parameters could not be created.";

    public IStrategoTermBuilder getTermBuilder() {
        return new TermFactory();
    }

    /**
     * Attempts to casts all elements in the specified iterable to a different type.
     *
     * @param input the iterable
     * @param <R> the type to cast to
     * @return the cast list; or {@code null} when casting failed or the input was {@code null}
     */
    @Nullable public static <R> List<R> tryCastAll(@Nullable Iterable<?> input) {
        if (input == null) return null;
        try {
            //noinspection unchecked
            return StreamSupport.stream(input.spliterator(), false).map(e -> (R)e).collect(Collectors.toList());
        } catch (ClassCastException e) {
            return null;
        }
    }

}
