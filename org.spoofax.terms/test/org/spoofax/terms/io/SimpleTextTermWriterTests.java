package org.spoofax.terms.io;

import org.junit.jupiter.api.*;
import org.spoofax.DummyTermAttachment;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.ITermAttachment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.spoofax.TestUtils.putAnnotations;
import static org.spoofax.TestUtils.putAttachments;

/** Tests the {@link SimpleTextTermWriter} class. */
@DisplayName("TextTermWriter")
public class SimpleTextTermWriterTests {

    public interface Fixture extends TermWriterTests.Fixture {

        @Override
        SimpleTextTermWriter createTermWriter();

        SimpleTextTermWriter createTermWriter(int maxDepth);

        SimpleTextTermWriter createTermWriter(int maxDepth, boolean ignoreAnnotations, boolean ignoreAttachments);

        SimpleTextTermWriter createTermWriter(int maxDepth, boolean ignoreAnnotations, boolean ignoreAttachments, boolean ignoreListTailAttributes, boolean ignoreAnnotationAttributes);

    }

    public static class FixtureImpl implements Fixture {

        @Override
        public SimpleTextTermWriter createTermWriter() {
            return new SimpleTextTermWriter();
        }

        @Override
        public SimpleTextTermWriter createTermWriter(int maxDepth) {
            return new SimpleTextTermWriter(maxDepth);
        }

        @Override
        public SimpleTextTermWriter createTermWriter(int maxDepth, boolean ignoreAnnotations, boolean ignoreAttachments) {
            return new SimpleTextTermWriter(maxDepth, ignoreAnnotations, ignoreAttachments);
        }

        @Override
        public SimpleTextTermWriter createTermWriter(int maxDepth, boolean ignoreAnnotations, boolean ignoreAttachments, boolean ignoreListTailAttributes, boolean ignoreAnnotationAttributes) {
            return new SimpleTextTermWriter(maxDepth, ignoreAnnotations, ignoreAttachments, ignoreListTailAttributes, ignoreAnnotationAttributes);
        }

        @Override
        public ITermFactory getFactory() {
            return new TermFactory();
        }

    }

    /** Tests the {@link TextTermWriter#write(IStrategoTerm, Appendable)} method. */
    @DisplayName("write(IStrategoTerm, Appendable)")
    @Nested class WriteTests extends FixtureImpl {

        private final IStrategoAppl APPL0 = getFactory().makeAppl("MyCons");
        private final IStrategoAppl APPL1 = getFactory().makeAppl("MyCons", getFactory().makeInt(42));
        private final IStrategoAppl APPL2 = getFactory().makeAppl("MyCons", getFactory().makeInt(42), getFactory().makeString("mystring"));

        private final IStrategoList LIST0 = getFactory().makeList();
        private final IStrategoList LIST1 = getFactory().makeList(getFactory().makeInt(42));
        private final IStrategoList LIST2 = getFactory().makeList(getFactory().makeInt(42), getFactory().makeString("mystring"));

        private final IStrategoTuple TUPLE0 = getFactory().makeTuple();
        private final IStrategoTuple TUPLE1 = getFactory().makeTuple(getFactory().makeInt(42));
        private final IStrategoTuple TUPLE2 = getFactory().makeTuple(getFactory().makeInt(42), getFactory().makeString("mystring"));

        private final IStrategoInt INT = getFactory().makeInt(42);
        private final IStrategoReal REAL = getFactory().makeReal(13.37);
        private final IStrategoString STRING = getFactory().makeString("mystring");
        private final IStrategoPlaceholder PLACEHOLDER = getFactory().makePlaceholder(getFactory().makeInt(42));

        private final List<IStrategoTerm> ANNO0 = Collections.emptyList();
        private final List<IStrategoTerm> ANNO1 = Collections.singletonList(getFactory().makeString("anno"));
        private final List<IStrategoTerm> ANNO2 = Arrays.asList(getFactory().makeString("anno1"), getFactory().makeString("anno2"));

        private final List<ITermAttachment> ATTACH0 = Collections.emptyList();
        private final List<ITermAttachment> ATTACH1 = Collections.singletonList(new DummyTermAttachment(DummyTermAttachment.Type1));
        private final List<ITermAttachment> ATTACH2 = Arrays.asList(new DummyTermAttachment(DummyTermAttachment.Type1), new DummyTermAttachment(DummyTermAttachment.Type2));

        @TestFactory
        Stream<DynamicTest> write() {
            return Stream.of(
                write("MyCons()", APPL0),
                write("MyCons(42)", APPL1),
                write("MyCons(42,\"mystring\")", APPL2),
                write("[]", LIST0),
                write("[42]", LIST1),
                write("[42,\"mystring\"]", LIST2),
                write("()", TUPLE0),
                write("(42)", TUPLE1),
                write("(42,\"mystring\")", TUPLE2),
                write("42", INT),
                write("13.37", REAL),
                write("\"mystring\"", STRING),
                write("<42>", PLACEHOLDER)
            ).flatMap(s -> s);
        }

        Stream<DynamicTest> write(String expected, IStrategoTerm term) {
            return Stream.of(
                // Simple
                DynamicTest.dynamicTest("simple term: " + expected, () -> {
                    // Arrange
                    SimpleTextTermWriter sut = createTermWriter(Integer.MAX_VALUE, true, true);

                    // Act
                    String result = sut.writeToString(term);

                    // Assert
                    assertEquals(expected, result);
                }),
                // Annotations
                DynamicTest.dynamicTest("term with no annotations: " + expected, () -> {
                    // Arrange
                    SimpleTextTermWriter sut = createTermWriter(Integer.MAX_VALUE, false, true);

                    // Act
                    String result = sut.writeToString(putAnnotations(term, getFactory(), ANNO0));

                    // Assert
                    assertEquals(expected, result);
                }),
                DynamicTest.dynamicTest("term with one annotation: " + expected + "{\"anno\"}", () -> {
                    // Arrange
                    SimpleTextTermWriter sut = createTermWriter(Integer.MAX_VALUE, false, true);

                    // Act
                    String result = sut.writeToString(putAnnotations(term, getFactory(), ANNO1));

                    // Assert
                    assertEquals(expected + "{\"anno\"}", result);
                }),
                DynamicTest.dynamicTest("term with two annotations: " + expected + "{\"anno1\",\"anno2\"}", () -> {
                    // Arrange
                    SimpleTextTermWriter sut = createTermWriter(Integer.MAX_VALUE, false, true);

                    // Act
                    String result = sut.writeToString(putAnnotations(term, getFactory(), ANNO2));

                    // Assert
                    assertEquals(expected + "{\"anno1\",\"anno2\"}", result);
                }),
                // Attachments
                DynamicTest.dynamicTest("term with no attachments: " + expected, () -> {
                    // Arrange
                    SimpleTextTermWriter sut = createTermWriter(Integer.MAX_VALUE, true, false);

                    // Act
                    String result = sut.writeToString(putAttachments(term, ATTACH0));

                    // Assert
                    assertEquals(expected, result);
                }),
                DynamicTest.dynamicTest("term with one attachment: " + expected + "«DummyTermAttachment<Type1>»", () -> {
                    // Arrange
                    SimpleTextTermWriter sut = createTermWriter(Integer.MAX_VALUE, true, false);

                    // Act
                    String result = sut.writeToString(putAttachments(term, ATTACH1));

                    // Assert
                    assertEquals(expected + "«DummyTermAttachment<Type1>»", result);
                }),
                DynamicTest.dynamicTest("term with two attachments: " + expected + "«DummyTermAttachment<Type1>,DummyTermAttachment<Type2>»", () -> {
                    // Arrange
                    SimpleTextTermWriter sut = createTermWriter(Integer.MAX_VALUE, true, false);

                    // Act
                    String result = sut.writeToString(putAttachments(term, ATTACH2));

                    // Assert
                    assertEquals(expected + "«DummyTermAttachment<Type1>,DummyTermAttachment<Type2>»", result);
                }),
                // Annotations and Attachments
                DynamicTest.dynamicTest("term with two attachments and two annotations: " + expected + "{\"anno1\",\"anno2\"}«DummyTermAttachment<Type1>,DummyTermAttachment<Type2>»", () -> {
                    // Arrange
                    SimpleTextTermWriter sut = createTermWriter(Integer.MAX_VALUE, false, false);

                    // Act
                    String result = sut.writeToString(putAttachments(putAnnotations(term, getFactory(), ANNO2), ATTACH2));

                    // Assert
                    assertEquals(expected + "{\"anno1\",\"anno2\"}«DummyTermAttachment<Type1>,DummyTermAttachment<Type2>»", result);
                })
            );
        }

        @Test
        @DisplayName("uses correct depth")
        public void usesCorrectDepth() {
            // Arrange
            // A(B(C(){X(Y(Z()))}){X(Y(Z()))}){X(Y(Z()))}
            IStrategoList annotations = getFactory().makeList(getFactory().makeAppl("X", getFactory().makeAppl("Y", getFactory().makeAppl("Z"))));
            IStrategoTerm term = getFactory().makeAppl(getFactory().makeConstructor("A", 1),
                Collections.singletonList(getFactory().makeAppl(getFactory().makeConstructor("B", 1),
                    Collections.singletonList(getFactory().makeAppl(getFactory().makeConstructor("C", 0), new IStrategoTerm[0], annotations)).toArray(new IStrategoTerm[0]), annotations)).toArray(new IStrategoTerm[0]), annotations);
            SimpleTextTermWriter sut0 = createTermWriter(0, false, false);
            SimpleTextTermWriter sut1 = createTermWriter(1, false, false);
            SimpleTextTermWriter sut2 = createTermWriter(2, false, false);
            SimpleTextTermWriter sut3 = createTermWriter(3, false, false);

            // Act
            String result0 = sut0.writeToString(term);
            String result1 = sut1.writeToString(term);
            String result2 = sut2.writeToString(term);
            String result3 = sut3.writeToString(term);

            // Assert
            assertEquals("…", result0);
            assertEquals("A(…)", result1);  // Empty {…} are elided.
            assertEquals("A(B(…)){X(…)}", result2);
            assertEquals("A(B(C()){X(…)}){X(Y(…))}", result3);
        }

    }

    // @formatter:off
    // TermWriter
    @Nested class WriteAppendableTests    extends FixtureImpl implements TextTermWriterTests.WriteAppendableTests {}
    @Nested class WriteStreamCharsetTests extends FixtureImpl implements TextTermWriterTests.WriteStreamCharsetTests {}
    @Nested class WriteStreamTests        extends FixtureImpl implements TextTermWriterTests.WriteStreamTests {}
    @Nested class WriteToStringTests      extends FixtureImpl implements TextTermWriterTests.WriteToStringTests {}
    @Nested class WriteToBytesTests       extends FixtureImpl implements TermWriterTests.WriteToBytesTests {}
    // @formatter:on

}
