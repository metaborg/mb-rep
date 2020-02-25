package org.spoofax.terms.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.StrategoConstructor;
import org.spoofax.terms.TermFactory;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/** Tests the {@link TermUtils} class. */
@DisplayName("TermUtils")
public final class TermUtilsTests {

    public TermFactory factory = new TermFactory();

    /** Tests the {@link TermUtils#isString} methods. */
    @Nested
    @DisplayName("isString(..)")
    public final class IsStringTests {

        @Test
        @DisplayName("when term is a string term, returns true")
        public void whenTermIsAStringTerm_returnsTrue() {
            assertTrue(TermUtils.isString(factory.makeString("")));
            assertTrue(TermUtils.isString(factory.makeString("abc")));
            assertTrue(TermUtils.isString(factory.makeString("DEF")));
        }

        @Test
        @DisplayName("when term is not a string term, returns false")
        public void whenTermIsNotAStringTerm_returnsFalse() {
            assertFalse(TermUtils.isString(factory.makeInt(42)));
            assertFalse(TermUtils.isString(factory.makeReal(4.2)));
            assertFalse(TermUtils.isString(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))));
            assertFalse(TermUtils.isString(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))));
            assertFalse(TermUtils.isString(factory.makeList(factory.makeString("abc"), factory.makeString("def"))));
        }

        @Test
        @DisplayName("when term is a string term and value matches, returns true")
        public void whenTermIsAStringTermAndValueMatches_returnsTrue() {
            assertTrue(TermUtils.isString(factory.makeString(""), ""));
            assertTrue(TermUtils.isString(factory.makeString("abc"), "abc"));
            assertTrue(TermUtils.isString(factory.makeString("DEF"), "DEF"));
        }

        @Test
        @DisplayName("when term is a string term but value does not match, returns false")
        public void whenTermIsAStringTermButValueDoesNotMatch_returnsFalse() {
            assertFalse(TermUtils.isString(factory.makeString(""), "XYZ"));
            assertFalse(TermUtils.isString(factory.makeString("abc"), "XYZ"));
            assertFalse(TermUtils.isString(factory.makeString("DEF"), "XYZ"));
        }

    }


    /** Tests the {@link TermUtils#isInt} methods. */
    @Nested
    @DisplayName("isInt(..)")
    public final class IsIntTests {

        @Test
        @DisplayName("when term is an int term, returns true")
        public void whenTermIsAnIntTerm_returnsTrue() {
            assertTrue(TermUtils.isInt(factory.makeInt(0)));
            assertTrue(TermUtils.isInt(factory.makeInt(-10)));
            assertTrue(TermUtils.isInt(factory.makeInt(42)));
        }

        @Test
        @DisplayName("when term is not an int term, returns false")
        public void whenTermIsNotAnIntTerm_returnsFalse() {
            assertFalse(TermUtils.isInt(factory.makeString("abc")));
            assertFalse(TermUtils.isInt(factory.makeReal(4.2)));
            assertFalse(TermUtils.isInt(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))));
            assertFalse(TermUtils.isInt(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))));
            assertFalse(TermUtils.isInt(factory.makeList(factory.makeString("abc"), factory.makeString("def"))));
        }

        @Test
        @DisplayName("when term is an int term and value matches, returns true")
        public void whenTermIsAnIntTermAndValueMatches_returnsTrue() {
            assertTrue(TermUtils.isInt(factory.makeInt(0), 0));
            assertTrue(TermUtils.isInt(factory.makeInt(-10), -10));
            assertTrue(TermUtils.isInt(factory.makeInt(42), 42));
        }

        @Test
        @DisplayName("when term is an int term but value does not match, returns false")
        public void whenTermIsAnIntTermButValueDoesNotMatch_returnsFalse() {
            assertFalse(TermUtils.isInt(factory.makeInt(0), 1337));
            assertFalse(TermUtils.isInt(factory.makeInt(-10), 1337));
            assertFalse(TermUtils.isInt(factory.makeInt(42), 1337));
        }

    }


    /** Tests the {@link TermUtils#isReal} methods. */
    @Nested
    @DisplayName("isReal(..)")
    public final class IsRealTests {

        @Test
        @DisplayName("when term is a real term, returns true")
        public void whenTermIsARealTerm_returnsTrue() {
            assertTrue(TermUtils.isReal(factory.makeReal(0.0)));
            assertTrue(TermUtils.isReal(factory.makeReal(-10.2)));
            assertTrue(TermUtils.isReal(factory.makeReal(4.2)));
        }

        @Test
        @DisplayName("when term is not a real term, returns false")
        public void whenTermIsNotARealTerm_returnsFalse() {
            assertFalse(TermUtils.isReal(factory.makeString("abc")));
            assertFalse(TermUtils.isReal(factory.makeInt(42)));
            assertFalse(TermUtils.isReal(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))));
            assertFalse(TermUtils.isReal(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))));
            assertFalse(TermUtils.isReal(factory.makeList(factory.makeString("abc"), factory.makeString("def"))));
        }

        @Test
        @DisplayName("when term is a real term and value matches, returns true")
        public void whenTermIsARealTermAndValueMatches_returnsTrue() {
            assertTrue(TermUtils.isReal(factory.makeReal(0.0), 0.0));
            assertTrue(TermUtils.isReal(factory.makeReal(-10.2), -10.2));
            assertTrue(TermUtils.isReal(factory.makeReal(4.2), 4.2));
        }

        @Test
        @DisplayName("when term is a real term but value does not match, returns false")
        public void whenTermIsARealTermButValueDoesNotMatch_returnsFalse() {
            assertFalse(TermUtils.isReal(factory.makeReal(0.0), 133.7));
            assertFalse(TermUtils.isReal(factory.makeReal(-10.2), 133.7));
            assertFalse(TermUtils.isReal(factory.makeReal(4.2), 133.7));
        }

    }


    /** Tests the {@link TermUtils#isAppl} methods. */
    @Nested
    @DisplayName("isAppl(..)")
    public final class IsApplTests {

        private final StrategoConstructor Nil = factory.makeConstructor("Nil", 0);
        private final StrategoConstructor Singleton = factory.makeConstructor("Singleton", 1);
        private final StrategoConstructor Pair = factory.makeConstructor("Pair", 2);
        private final StrategoConstructor Triplet = factory.makeConstructor("Triplet", 3);

        @Test
        @DisplayName("when term is an appl term, returns true")
        public void whenTermIsAnApplTerm_returnsTrue() {
            assertTrue(TermUtils.isAppl(factory.makeAppl(Nil)));
            assertTrue(TermUtils.isAppl(factory.makeAppl(Singleton, factory.makeString("abc"))));
            assertTrue(TermUtils.isAppl(factory.makeAppl(Pair, factory.makeString("abc"), factory.makeString("def"))));
        }

        @Test
        @DisplayName("when term is not an appl term, returns false")
        public void whenTermIsNotAnApplTerm_returnsFalse() {
            assertFalse(TermUtils.isAppl(factory.makeString("abc")));
            assertFalse(TermUtils.isAppl(factory.makeInt(42)));
            assertFalse(TermUtils.isAppl(factory.makeReal(4.2)));
            assertFalse(TermUtils.isAppl(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))));
            assertFalse(TermUtils.isAppl(factory.makeList(factory.makeString("abc"), factory.makeString("def"))));
        }

        @Test
        @DisplayName("when term is an appl term and constructor matches, returns true")
        public void whenTermIsAnApplTermAndConstructorMatches_returnsTrue() {
            assertTrue(TermUtils.isAppl(factory.makeAppl(Nil), Nil));
            assertTrue(TermUtils.isAppl(factory.makeAppl(Singleton, factory.makeString("abc")), Singleton));
            assertTrue(TermUtils.isAppl(factory.makeAppl(Pair, factory.makeString("abc"), factory.makeString("def")), Pair));
        }

        @Test
        @DisplayName("when term is an appl term and constructor name and arity match, returns true")
        public void whenTermIsAnApplTermAndConstructornameAndArityMatch_returnsTrue() {
            assertTrue(TermUtils.isAppl(factory.makeAppl(factory.makeConstructor("Nil", 0)), "Nil", 0));
            assertTrue(TermUtils.isAppl(factory.makeAppl(factory.makeConstructor("Singleton", 1), factory.makeString("abc")), "Singleton", 1));
            assertTrue(TermUtils.isAppl(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")), "Pair", 2));
        }

        @Test
        @DisplayName("when term is an appl term but constructor does not match, returns false")
        public void whenTermIsAnApplTermButConstructorDoesNotMatch_returnsFalse() {
            assertFalse(TermUtils.isAppl(factory.makeAppl(factory.makeConstructor("Nil", 0)), Triplet));
            assertFalse(TermUtils.isAppl(factory.makeAppl(factory.makeConstructor("Singleton", 1), factory.makeString("abc")), Triplet));
            assertFalse(TermUtils.isAppl(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")), Triplet));
        }

        @Test
        @DisplayName("when term is an appl term but constructor arity does not match, returns false")
        public void whenTermIsAnApplTermButConstructorArityDoesNotMatch_returnsFalse() {
            assertFalse(TermUtils.isAppl(factory.makeAppl(factory.makeConstructor("Nil", 0)), "Nil", 3));
            assertFalse(TermUtils.isAppl(factory.makeAppl(factory.makeConstructor("Singleton", 1), factory.makeString("abc")), "Singleton", 3));
            assertFalse(TermUtils.isAppl(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")), "Pair", 3));
        }
        
        @Test
        @DisplayName("when term is an appl term but constructor name does not match, returns false")
        public void whenTermIsAnApplTermButConstructorNameDoesNotMatch_returnsFalse() {
            assertFalse(TermUtils.isAppl(factory.makeAppl(factory.makeConstructor("Nil", 0)), "Xyz", 0));
            assertFalse(TermUtils.isAppl(factory.makeAppl(factory.makeConstructor("Singleton", 1), factory.makeString("abc")), "Xyz", 1));
            assertFalse(TermUtils.isAppl(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")), "Xyz", 2));
        }

    }


    /** Tests the {@link TermUtils#isList} methods. */
    @Nested
    @DisplayName("isList(..)")
    public final class IsListTests {

        @Test
        @DisplayName("when term is a list term, returns true")
        public void whenTermIsAListTerm_returnsTrue() {
            assertTrue(TermUtils.isList(factory.makeList()));
            assertTrue(TermUtils.isList(factory.makeList(factory.makeString("abc"))));
            assertTrue(TermUtils.isList(factory.makeList(factory.makeString("abc"), factory.makeString("def"))));
        }

        @Test
        @DisplayName("when term is not a list term, returns false")
        public void whenTermIsNotAListTerm_returnsFalse() {
            assertFalse(TermUtils.isList(factory.makeString("abc")));
            assertFalse(TermUtils.isList(factory.makeInt(42)));
            assertFalse(TermUtils.isList(factory.makeReal(4.2)));
            assertFalse(TermUtils.isList(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))));
            assertFalse(TermUtils.isList(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))));
        }

        @Test
        @DisplayName("when term is a list term and size matches, returns true")
        public void whenTermIsAListTermAndSizeMatches_returnsTrue() {
            assertTrue(TermUtils.isList(factory.makeList(), 0));
            assertTrue(TermUtils.isList(factory.makeList(factory.makeString("abc")), 1));
            assertTrue(TermUtils.isList(factory.makeList(factory.makeString("abc"), factory.makeString("def")), 2));
        }

        @Test
        @DisplayName("when term is a list term but size does not match, returns false")
        public void whenTermIsAListTermButSizeDoesNotMatch_returnsFalse() {
            assertFalse(TermUtils.isList(factory.makeList(), 3));
            assertFalse(TermUtils.isList(factory.makeList(factory.makeString("abc")), 3));
            assertFalse(TermUtils.isList(factory.makeList(factory.makeString("abc"), factory.makeString("def")), 3));
        }

    }



    /** Tests the {@link TermUtils#asString(IStrategoTerm)} method. */
    @Nested
    @DisplayName("asString(IStrategoTerm)")
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public final class AsStringTests {

        @Test
        @DisplayName("when term is a string term, returns the term")
        public void whenTermIsAStringTerm_returnsTheTerm() {
            IStrategoString t1 = factory.makeString("");
            IStrategoString t2 = factory.makeString("abc");
            IStrategoString t3 = factory.makeString("DEF");

            assertEquals(t1, TermUtils.asString(t1).get());
            assertEquals(t2, TermUtils.asString(t2).get());
            assertEquals(t3, TermUtils.asString(t3).get());
        }

        @Test
        @DisplayName("when term is not a string term, returns nothing")
        public void whenTermIsNotAStringTerm_returnsNothing() {
            assertFalse(TermUtils.asString(factory.makeInt(42)).isPresent());
            assertFalse(TermUtils.asString(factory.makeReal(4.2)).isPresent());
            assertFalse(TermUtils.asString(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))).isPresent());
            assertFalse(TermUtils.asString(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))).isPresent());
            assertFalse(TermUtils.asString(factory.makeList(factory.makeString("abc"), factory.makeString("def"))).isPresent());
        }

    }
    
    /** Tests the {@link TermUtils#asInt(IStrategoTerm)} method. */
    @Nested
    @DisplayName("asInt(IStrategoTerm)")
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public final class AsIntTests {

        @Test
        @DisplayName("when term is an int term, returns the term")
        public void whenTermIsAnIntTerm_returnsTheTerm() {
            IStrategoInt t1 = factory.makeInt(0);
            IStrategoInt t2 = factory.makeInt(-10);
            IStrategoInt t3 = factory.makeInt(42);

            assertEquals(t1, TermUtils.asInt(t1).get());
            assertEquals(t2, TermUtils.asInt(t2).get());
            assertEquals(t3, TermUtils.asInt(t3).get());
        }

        @Test
        @DisplayName("when term is not an int term, returns nothing")
        public void whenTermIsNotAnIntTerm_returnsNothing() {
            assertFalse(TermUtils.asInt(factory.makeString("abc")).isPresent());
            assertFalse(TermUtils.asInt(factory.makeReal(4.2)).isPresent());
            assertFalse(TermUtils.asInt(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))).isPresent());
            assertFalse(TermUtils.asInt(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))).isPresent());
            assertFalse(TermUtils.asInt(factory.makeList(factory.makeString("abc"), factory.makeString("def"))).isPresent());
        }

    }
    
    /** Tests the {@link TermUtils#asReal(IStrategoTerm)} method. */
    @Nested
    @DisplayName("asReal(IStrategoTerm)")
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public final class AsRealTests {

        @Test
        @DisplayName("when term is a real term, returns the term")
        public void whenTermIsARealTerm_returnsTheTerm() {
            IStrategoReal t1 = factory.makeReal(0.0);
            IStrategoReal t2 = factory.makeReal(-10.2);
            IStrategoReal t3 = factory.makeReal(4.2);

            assertEquals(t1, TermUtils.asReal(t1).get());
            assertEquals(t2, TermUtils.asReal(t2).get());
            assertEquals(t3, TermUtils.asReal(t3).get());
        }

        @Test
        @DisplayName("when term is not a real term, returns nothing")
        public void whenTermIsNotARealTerm_returnsNothing() {
            assertFalse(TermUtils.asReal(factory.makeString("abc")).isPresent());
            assertFalse(TermUtils.asReal(factory.makeInt(42)).isPresent());
            assertFalse(TermUtils.asReal(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))).isPresent());
            assertFalse(TermUtils.asReal(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))).isPresent());
            assertFalse(TermUtils.asReal(factory.makeList(factory.makeString("abc"), factory.makeString("def"))).isPresent());
        }

    }
    
    /** Tests the {@link TermUtils#asAppl(IStrategoTerm)} method. */
    @Nested
    @DisplayName("asAppl(IStrategoTerm)")
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public final class AsApplTests {

        @Test
        @DisplayName("when term is an appl term, returns the term")
        public void whenTermIsAnApplTerm_returnsTheTerm() {
            IStrategoAppl t1 = factory.makeAppl(factory.makeConstructor("Nil", 0));
            IStrategoAppl t2 = factory.makeAppl(factory.makeConstructor("Singleton", 1), factory.makeString("abc"));
            IStrategoAppl t3 = factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"));

            assertEquals(t1, TermUtils.asAppl(t1).get());
            assertEquals(t2, TermUtils.asAppl(t2).get());
            assertEquals(t3, TermUtils.asAppl(t3).get());
        }

        @Test
        @DisplayName("when term is not an appl term, returns nothing")
        public void whenTermIsNotAnApplTerm_returnsNothing() {
            assertFalse(TermUtils.asAppl(factory.makeString("abc")).isPresent());
            assertFalse(TermUtils.asAppl(factory.makeInt(42)).isPresent());
            assertFalse(TermUtils.asInt(factory.makeReal(4.2)).isPresent());
            assertFalse(TermUtils.asAppl(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))).isPresent());
            assertFalse(TermUtils.asAppl(factory.makeList(factory.makeString("abc"), factory.makeString("def"))).isPresent());
        }

    }

    /** Tests the {@link TermUtils#asList(IStrategoTerm)} method. */
    @Nested
    @DisplayName("asList(IStrategoTerm)")
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public final class AsListTests {

        @Test
        @DisplayName("when term is a list term, returns the term")
        public void whenTermIsAListTerm_returnsTheTerm() {
            IStrategoList t1 = factory.makeList();
            IStrategoList t2 = factory.makeList(factory.makeString("abc"));
            IStrategoList t3 = factory.makeList(factory.makeString("abc"), factory.makeString("def"));

            assertEquals(t1, TermUtils.asList(t1).get());
            assertEquals(t2, TermUtils.asList(t2).get());
            assertEquals(t3, TermUtils.asList(t3).get());
        }

        @Test
        @DisplayName("when term is not a list term, returns nothing")
        public void whenTermIsNotAListTerm_returnsNothing() {
            assertFalse(TermUtils.asList(factory.makeString("abc")).isPresent());
            assertFalse(TermUtils.asList(factory.makeInt(42)).isPresent());
            assertFalse(TermUtils.asInt(factory.makeReal(4.2)).isPresent());
            assertFalse(TermUtils.asReal(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))).isPresent());
            assertFalse(TermUtils.asList(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))).isPresent());
        }

    }


    
    /** Tests the {@link TermUtils#toString(IStrategoTerm)} method. */
    @Nested
    @DisplayName("toString(IStrategoTerm)")
    public final class ToStringTests {

        @Test
        @DisplayName("when term is a string term, returns the term")
        public void whenTermIsAStringTerm_returnsTheTerm() {
            IStrategoString t1 = factory.makeString("");
            IStrategoString t2 = factory.makeString("abc");
            IStrategoString t3 = factory.makeString("DEF");

            assertEquals(t1, TermUtils.toString(t1));
            assertEquals(t2, TermUtils.toString(t2));
            assertEquals(t3, TermUtils.toString(t3));
        }

        @Test
        @DisplayName("when term is not a string term, throws exception")
        @SuppressWarnings("ResultOfMethodCallIgnored")
        public void whenTermIsNotAStringTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toString(factory.makeInt(42)));
            assertThrows(ClassCastException.class, () -> TermUtils.toString(factory.makeReal(4.2)));
            assertThrows(ClassCastException.class, () -> TermUtils.toString(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toString(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toString(factory.makeList(factory.makeString("abc"), factory.makeString("def"))));
        }

    }

    /** Tests the {@link TermUtils#toInt(IStrategoTerm)} method. */
    @Nested
    @DisplayName("toInt(IStrategoTerm)")
    public final class ToIntTests {

        @Test
        @DisplayName("when term is an int term, returns the term")
        public void whenTermIsAnIntTerm_returnsTheTerm() {
            IStrategoInt t1 = factory.makeInt(0);
            IStrategoInt t2 = factory.makeInt(-10);
            IStrategoInt t3 = factory.makeInt(42);

            assertEquals(t1, TermUtils.toInt(t1));
            assertEquals(t2, TermUtils.toInt(t2));
            assertEquals(t3, TermUtils.toInt(t3));
        }

        @Test
        @DisplayName("when term is not an int term, throws exception")
        public void whenTermIsNotAnIntTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toInt(factory.makeString("abc")));
            assertThrows(ClassCastException.class, () -> TermUtils.toInt(factory.makeReal(4.2)));
            assertThrows(ClassCastException.class, () -> TermUtils.toInt(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toInt(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toInt(factory.makeList(factory.makeString("abc"), factory.makeString("def"))));
        }

    }

    /** Tests the {@link TermUtils#toReal(IStrategoTerm)} method. */
    @Nested
    @DisplayName("toReal(IStrategoTerm)")
    public final class ToRealTests {

        @Test
        @DisplayName("when term is a real term, returns the term")
        public void whenTermIsARealTerm_returnsTheTerm() {
            IStrategoReal t1 = factory.makeReal(0.0);
            IStrategoReal t2 = factory.makeReal(-10.2);
            IStrategoReal t3 = factory.makeReal(4.2);

            assertEquals(t1, TermUtils.toReal(t1));
            assertEquals(t2, TermUtils.toReal(t2));
            assertEquals(t3, TermUtils.toReal(t3));
        }

        @Test
        @DisplayName("when term is not a real term, throws exception")
        public void whenTermIsNotARealTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toReal(factory.makeString("abc")));
            assertThrows(ClassCastException.class, () -> TermUtils.toReal(factory.makeInt(42)));
            assertThrows(ClassCastException.class, () -> TermUtils.toReal(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toReal(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toReal(factory.makeList(factory.makeString("abc"), factory.makeString("def"))));
        }

    }

    /** Tests the {@link TermUtils#toAppl(IStrategoTerm)} method. */
    @Nested
    @DisplayName("toAppl(IStrategoTerm)")
    public final class ToApplTests {

        @Test
        @DisplayName("when term is an appl term, returns the term")
        public void whenTermIsAnApplTerm_returnsTheTerm() {
            IStrategoAppl t1 = factory.makeAppl(factory.makeConstructor("Nil", 0));
            IStrategoAppl t2 = factory.makeAppl(factory.makeConstructor("Singleton", 1), factory.makeString("abc"));
            IStrategoAppl t3 = factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"));

            assertEquals(t1, TermUtils.toAppl(t1));
            assertEquals(t2, TermUtils.toAppl(t2));
            assertEquals(t3, TermUtils.toAppl(t3));
        }

        @Test
        @DisplayName("when term is not an appl term, throws exception")
        public void whenTermIsNotAnApplTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toAppl(factory.makeString("abc")));
            assertThrows(ClassCastException.class, () -> TermUtils.toAppl(factory.makeInt(42)));
            assertThrows(ClassCastException.class, () -> TermUtils.toAppl(factory.makeReal(4.2)));
            assertThrows(ClassCastException.class, () -> TermUtils.toAppl(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toAppl(factory.makeList(factory.makeString("abc"), factory.makeString("def"))));
        }

    }

    /** Tests the {@link TermUtils#toList(IStrategoTerm)} method. */
    @Nested
    @DisplayName("toList(IStrategoTerm)")
    public final class ToListTests {

        @Test
        @DisplayName("when term is a list term, returns the term")
        public void whenTermIsAListTerm_returnsTheTerm() {
            IStrategoList t1 = factory.makeList();
            IStrategoList t2 = factory.makeList(factory.makeString("abc"));
            IStrategoList t3 = factory.makeList(factory.makeString("abc"), factory.makeString("def"));

            assertEquals(t1, TermUtils.toList(t1));
            assertEquals(t2, TermUtils.toList(t2));
            assertEquals(t3, TermUtils.toList(t3));
        }

        @Test
        @DisplayName("when term is not a list term, throws exception")
        public void whenTermIsNotAListTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toList(factory.makeString("abc")));
            assertThrows(ClassCastException.class, () -> TermUtils.toList(factory.makeInt(42)));
            assertThrows(ClassCastException.class, () -> TermUtils.toList(factory.makeReal(4.2)));
            assertThrows(ClassCastException.class, () -> TermUtils.toList(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toList(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))));
        }

    }



    /** Tests the {@link TermUtils#toJavaString(IStrategoTerm)} method. */
    @Nested
    @DisplayName("toJavaString(IStrategoTerm)")
    public final class ToJavaStringTests {

        @Test
        @DisplayName("when term is a string term, returns the value")
        public void whenTermIsAStringTerm_returnsTheValue() {
            assertEquals("", TermUtils.toJavaString(factory.makeString("")));
            assertEquals("abc", TermUtils.toJavaString(factory.makeString("abc")));
            assertEquals("DEF", TermUtils.toJavaString(factory.makeString("DEF")));
        }

        @Test
        @DisplayName("when term is not a string term, throws exception")
        public void whenTermIsNotAStringTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaString(factory.makeInt(42)));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaString(factory.makeReal(4.2)));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaString(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaString(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaString(factory.makeList(factory.makeString("abc"), factory.makeString("def"))));
        }

    }

    /** Tests the {@link TermUtils#toJavaInt(IStrategoTerm)} method. */
    @Nested
    @DisplayName("toJavaInt(IStrategoTerm)")
    public final class ToJavaIntTests {

        @Test
        @DisplayName("when term is an int term, returns the value")
        public void whenTermIsAnIntTerm_returnsTheValue() {
            assertEquals(0, TermUtils.toJavaInt(factory.makeInt(0)));
            assertEquals(-10, TermUtils.toJavaInt(factory.makeInt(-10)));
            assertEquals(42, TermUtils.toJavaInt(factory.makeInt(42)));
        }

        @Test
        @DisplayName("when term is not an int term, throws exception")
        public void whenTermIsNotAnIntTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaInt(factory.makeString("abc")));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaInt(factory.makeReal(4.2)));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaInt(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaInt(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaInt(factory.makeList(factory.makeString("abc"), factory.makeString("def"))));
        }

    }

    /** Tests the {@link TermUtils#toJavaReal(IStrategoTerm)} method. */
    @Nested
    @DisplayName("toJavaReal(IStrategoTerm)")
    public final class ToJavaRealTests {

        @Test
        @DisplayName("when term is a real term, returns the value")
        public void whenTermIsARealTerm_returnsTheValue() {
            assertEquals(0.0, TermUtils.toJavaReal(factory.makeReal(0.0)));
            assertEquals(-10.2, TermUtils.toJavaReal(factory.makeReal(-10.2)));
            assertEquals(4.2, TermUtils.toJavaReal(factory.makeReal(4.2)));
        }

        @Test
        @DisplayName("when term is not a real term, throws exception")
        public void whenTermIsNotARealTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaReal(factory.makeString("abc")));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaReal(factory.makeInt(42)));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaReal(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaReal(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaReal(factory.makeList(factory.makeString("abc"), factory.makeString("def"))));
        }

    }

    /** Tests the {@link TermUtils#toJavaList(IStrategoTerm)} method. */
    @Nested
    @DisplayName("toJavaList(IStrategoTerm)")
    public final class ToJavaListTests {

        @Test
        @DisplayName("when term is a list term, returns a list of subterms")
        public void whenTermIsAListTerm_returnsAListOfSubterms() {
            IStrategoString e0 = factory.makeString("abc");
            IStrategoString e1 = factory.makeString("def");

            assertEquals(Collections.emptyList(), TermUtils.toJavaList(factory.makeList()));
            assertEquals(Collections.singletonList(e0), TermUtils.toJavaList(factory.makeList(e0)));
            assertEquals(Arrays.asList(e0, e1), TermUtils.toJavaList(factory.makeList(e0, e1)));
        }

        @Test
        @DisplayName("when term is not a list term, throws exception")
        public void whenTermIsNotAListTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaList(factory.makeString("abc")));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaList(factory.makeInt(42)));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaList(factory.makeReal(4.2)));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaList(factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def"))));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaList(factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))));
        }

    }

    // ----- //


    /** Tests the {@link TermUtils#isStringAt} methods. */
    @Nested
    @DisplayName("isStringAt(..)")
    public final class IsStringAtTests {

        private final IStrategoTuple testTerm = factory.makeTuple(
                factory.makeString(""),
                factory.makeString("abc"),
                factory.makeString("DEF"),

                factory.makeInt(42),
                factory.makeReal(4.2),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is a string term, returns true")
        public void whenSubtermIsAStringTerm_returnsTrue() {
            assertTrue(TermUtils.isStringAt(testTerm, 0));
            assertTrue(TermUtils.isStringAt(testTerm, 1));
            assertTrue(TermUtils.isStringAt(testTerm, 2));
        }

        @Test
        @DisplayName("when subterm is not a string term, returns false")
        public void whenSubtermIsNotAStringTerm_returnsFalse() {
            assertFalse(TermUtils.isStringAt(testTerm, 4));
            assertFalse(TermUtils.isStringAt(testTerm, 5));
            assertFalse(TermUtils.isStringAt(testTerm, 6));
            assertFalse(TermUtils.isStringAt(testTerm, 7));
            assertFalse(TermUtils.isStringAt(testTerm, 8));
        }

        @Test
        @DisplayName("when subterm is a string term and value matches, returns true")
        public void whenSubtermIsAStringTermAndValueMatches_returnsTrue() {
            assertTrue(TermUtils.isStringAt(testTerm, 0, ""));
            assertTrue(TermUtils.isStringAt(testTerm, 1, "abc"));
            assertTrue(TermUtils.isStringAt(testTerm, 2, "DEF"));
        }

        @Test
        @DisplayName("when subterm is a string term but value does not match, returns false")
        public void whenSubtermIsAStringTermButValueDoesNotMatch_returnsFalse() {
            assertFalse(TermUtils.isStringAt(testTerm, 0, "XYZ"));
            assertFalse(TermUtils.isStringAt(testTerm, 1, "XYZ"));
            assertFalse(TermUtils.isStringAt(testTerm, 2, "XYZ"));
        }

        @Test
        @DisplayName("when index is out of bounds, returns false")
        public void whenIndexIsOutOfBounds_returnsFalse() {
            assertFalse(TermUtils.isStringAt(testTerm, -1));
            assertFalse(TermUtils.isStringAt(testTerm, 10));
            assertFalse(TermUtils.isStringAt(testTerm, -1,  "abc"));
            assertFalse(TermUtils.isStringAt(testTerm, 10, "abc"));
        }

    }


    /** Tests the {@link TermUtils#isIntAt} methods. */
    @Nested
    @DisplayName("isIntAt(..)")
    public final class IsIntAtTests {

        private final IStrategoTuple testTerm = factory.makeTuple(
                factory.makeInt(0),
                factory.makeInt(-10),
                factory.makeInt(42),

                factory.makeString("abc"),
                factory.makeReal(4.2),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is an int term, returns true")
        public void whenSubtermIsAnIntTerm_returnsTrue() {
            assertTrue(TermUtils.isIntAt(testTerm, 0));
            assertTrue(TermUtils.isIntAt(testTerm, 1));
            assertTrue(TermUtils.isIntAt(testTerm, 2));
        }

        @Test
        @DisplayName("when subterm is not an int term, returns false")
        public void whenSubtermIsNotAnIntTerm_returnsFalse() {
            assertFalse(TermUtils.isIntAt(testTerm, 4));
            assertFalse(TermUtils.isIntAt(testTerm, 5));
            assertFalse(TermUtils.isIntAt(testTerm, 6));
            assertFalse(TermUtils.isIntAt(testTerm, 7));
            assertFalse(TermUtils.isIntAt(testTerm, 8));
        }

        @Test
        @DisplayName("when subterm is an int term and value matches, returns true")
        public void whenSubtermIsAnIntTermAndValueMatches_returnsTrue() {
            assertTrue(TermUtils.isIntAt(testTerm, 0, 0));
            assertTrue(TermUtils.isIntAt(testTerm, 1, -10));
            assertTrue(TermUtils.isIntAt(testTerm, 2, 42));
        }

        @Test
        @DisplayName("when subterm is an int term but value does not match, returns false")
        public void whenSubtermIsAnIntTermButValueDoesNotMatch_returnsFalse() {
            assertFalse(TermUtils.isIntAt(testTerm, 0, 1337));
            assertFalse(TermUtils.isIntAt(testTerm, 1, 1337));
            assertFalse(TermUtils.isIntAt(testTerm, 2, 1337));
        }

        @Test
        @DisplayName("when index is out of bounds, returns false")
        public void whenIndexIsOutOfBounds_returnsFalse() {
            assertFalse(TermUtils.isIntAt(testTerm, -1));
            assertFalse(TermUtils.isIntAt(testTerm, 10));
            assertFalse(TermUtils.isIntAt(testTerm, -1,  42));
            assertFalse(TermUtils.isIntAt(testTerm, 10, 42));
        }

    }


    /** Tests the {@link TermUtils#isRealAt} methods. */
    @Nested
    @DisplayName("isRealAt(..)")
    public final class IsRealAtTests {

        private final IStrategoTuple testTerm = factory.makeTuple(
                factory.makeReal(0.0),
                factory.makeReal(-10.2),
                factory.makeReal(4.2),

                factory.makeString("abc"),
                factory.makeInt(42),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is a real term, returns true")
        public void whenSubtermIsARealTerm_returnsTrue() {
            assertTrue(TermUtils.isRealAt(testTerm, 0));
            assertTrue(TermUtils.isRealAt(testTerm, 1));
            assertTrue(TermUtils.isRealAt(testTerm, 2));
        }

        @Test
        @DisplayName("when subterm is not a real term, returns false")
        public void whenSubtermIsNotARealTerm_returnsFalse() {
            assertFalse(TermUtils.isRealAt(testTerm, 4));
            assertFalse(TermUtils.isRealAt(testTerm, 5));
            assertFalse(TermUtils.isRealAt(testTerm, 6));
            assertFalse(TermUtils.isRealAt(testTerm, 7));
            assertFalse(TermUtils.isRealAt(testTerm, 8));
        }

        @Test
        @DisplayName("when subterm is a real term and value matches, returns true")
        public void whenSubtermIsARealTermAndValueMatches_returnsTrue() {
            assertTrue(TermUtils.isRealAt(testTerm, 0, 0.0));
            assertTrue(TermUtils.isRealAt(testTerm, 1, -10.2));
            assertTrue(TermUtils.isRealAt(testTerm, 2, 4.2));
        }

        @Test
        @DisplayName("when subterm is a real term but value does not match, returns false")
        public void whenSubtermIsARealTermButValueDoesNotMatch_returnsFalse() {
            assertFalse(TermUtils.isRealAt(testTerm, 0, 133.7));
            assertFalse(TermUtils.isRealAt(testTerm, 1, 133.7));
            assertFalse(TermUtils.isRealAt(testTerm, 2, 133.7));
        }

        @Test
        @DisplayName("when index is out of bounds, returns false")
        public void whenIndexIsOutOfBounds_returnsFalse() {
            assertFalse(TermUtils.isRealAt(testTerm, -1));
            assertFalse(TermUtils.isRealAt(testTerm, 10));
            assertFalse(TermUtils.isRealAt(testTerm, -1,  4.2));
            assertFalse(TermUtils.isRealAt(testTerm, 10, 4.2));
        }

    }


    /** Tests the {@link TermUtils#isApplAt} methods. */
    @Nested
    @DisplayName("isApplAt(..)")
    public final class IsApplAtTests {

        private final StrategoConstructor Nil = factory.makeConstructor("Nil", 0);
        private final StrategoConstructor Singleton = factory.makeConstructor("Singleton", 1);
        private final StrategoConstructor Pair = factory.makeConstructor("Pair", 2);
        private final StrategoConstructor Triplet = factory.makeConstructor("Triplet", 3);
        
        private final IStrategoTuple testTerm = factory.makeTuple(
                factory.makeAppl(Nil),
                factory.makeAppl(Singleton, factory.makeString("abc")),
                factory.makeAppl(Pair, factory.makeString("abc"), factory.makeString("def")),

                factory.makeString("abc"),
                factory.makeInt(42),
                factory.makeReal(4.2),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );
        
        @Test
        @DisplayName("when subterm is an appl term, returns true")
        public void whenSubtermIsAnApplTerm_returnsTrue() {
            assertTrue(TermUtils.isApplAt(testTerm, 0));
            assertTrue(TermUtils.isApplAt(testTerm, 1));
            assertTrue(TermUtils.isApplAt(testTerm, 2));
        }

        @Test
        @DisplayName("when subterm is not an appl term, returns false")
        public void whenSubtermIsNotAnApplTerm_returnsFalse() {
            assertFalse(TermUtils.isApplAt(testTerm, 4));
            assertFalse(TermUtils.isApplAt(testTerm, 5));
            assertFalse(TermUtils.isApplAt(testTerm, 6));
            assertFalse(TermUtils.isApplAt(testTerm, 7));
            assertFalse(TermUtils.isApplAt(testTerm, 8));
        }

        @Test
        @DisplayName("when subterm is an appl term and constructor matches, returns true")
        public void whenSubtermIsAnApplTermAndConstructorMatches_returnsTrue() {
            assertTrue(TermUtils.isApplAt(testTerm, 0, Nil));
            assertTrue(TermUtils.isApplAt(testTerm, 1, Singleton));
            assertTrue(TermUtils.isApplAt(testTerm, 2, Pair));
        }

        @Test
        @DisplayName("when subterm is an appl term but constructor does not match, returns false")
        public void whenSubtermIsAnApplTermButConstructorDoesNotMatch_returnsFalse() {
            assertFalse(TermUtils.isApplAt(testTerm, 0, Triplet));
            assertFalse(TermUtils.isApplAt(testTerm, 1, Triplet));
            assertFalse(TermUtils.isApplAt(testTerm, 2, Triplet));
        }

        @Test
        @DisplayName("when subterm is an appl term and constructor name and arity match, returns true")
        public void whenSubtermIsAnApplTermAndConstructorNameAndArityMatch_returnsTrue() {
            assertTrue(TermUtils.isApplAt(testTerm, 0, "Nil", 0));
            assertTrue(TermUtils.isApplAt(testTerm, 1, "Singleton", 1));
            assertTrue(TermUtils.isApplAt(testTerm, 2, "Pair", 2));
        }
        
        @Test
        @DisplayName("when subterm is an appl term but constructor arity does not match, returns false")
        public void whenSubtermIsAnApplTermButConstructorArityDoesNotMatch_returnsFalse() {
            assertFalse(TermUtils.isApplAt(testTerm, 0, "Nil", 3));
            assertFalse(TermUtils.isApplAt(testTerm, 1, "Singleton", 3));
            assertFalse(TermUtils.isApplAt(testTerm, 2, "Pair", 3));
        }

        @Test
        @DisplayName("when subterm is an appl term but constructor name does not match, returns false")
        public void whenSubtermIsAnApplTermButConstructorNameDoesNotMatch_returnsFalse() {
            assertFalse(TermUtils.isApplAt(testTerm, 0, "Xyz", 0));
            assertFalse(TermUtils.isApplAt(testTerm, 1, "Xyz", 1));
            assertFalse(TermUtils.isApplAt(testTerm, 2, "Xyz", 2));
        }

        @Test
        @DisplayName("when index is out of bounds, returns false")
        public void whenIndexIsOutOfBounds_returnsFalse() {
            assertFalse(TermUtils.isApplAt(testTerm, -1));
            assertFalse(TermUtils.isApplAt(testTerm, 10));
            assertFalse(TermUtils.isApplAt(testTerm, -1,  Pair));
            assertFalse(TermUtils.isApplAt(testTerm, 10, Pair));
        }

    }


    /** Tests the {@link TermUtils#isListAt} methods. */
    @Nested
    @DisplayName("isListAt(..)")
    public final class IsListAtTests {

        private final IStrategoTuple testTerm = factory.makeTuple(
                factory.makeList(),
                factory.makeList(factory.makeString("abc")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def")),

                factory.makeString("abc"),
                factory.makeInt(42),
                factory.makeReal(4.2),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is a list term, returns true")
        public void whenSubtermIsAListTerm_returnsTrue() {
            assertTrue(TermUtils.isListAt(testTerm, 0));
            assertTrue(TermUtils.isListAt(testTerm, 1));
            assertTrue(TermUtils.isListAt(testTerm, 2));
        }

        @Test
        @DisplayName("when subterm is not a list term, returns false")
        public void whenSubtermIsNotAListTerm_returnsFalse() {
            assertFalse(TermUtils.isListAt(testTerm, 4));
            assertFalse(TermUtils.isListAt(testTerm, 5));
            assertFalse(TermUtils.isListAt(testTerm, 6));
            assertFalse(TermUtils.isListAt(testTerm, 7));
            assertFalse(TermUtils.isListAt(testTerm, 8));
        }

        @Test
        @DisplayName("when subterm is a list term and size matches, returns true")
        public void whenSubtermIsAListTermAndSizeMatches_returnsTrue() {
            assertTrue(TermUtils.isListAt(testTerm, 0, 0));
            assertTrue(TermUtils.isListAt(testTerm, 1, 1));
            assertTrue(TermUtils.isListAt(testTerm, 2, 2));
        }

        @Test
        @DisplayName("when subterm is a list term but size does not match, returns false")
        public void whenSubtermIsAListTermButSizeDoesNotMatch_returnsFalse() {
            assertFalse(TermUtils.isListAt(testTerm, 0, 3));
            assertFalse(TermUtils.isListAt(testTerm, 1, 3));
            assertFalse(TermUtils.isListAt(testTerm, 2, 3));
        }

        @Test
        @DisplayName("when index is out of bounds, returns false")
        public void whenIndexIsOutOfBounds_returnsFalse() {
            assertFalse(TermUtils.isListAt(testTerm, -1));
            assertFalse(TermUtils.isListAt(testTerm, 10));
            assertFalse(TermUtils.isListAt(testTerm, -1,  2));
            assertFalse(TermUtils.isListAt(testTerm, 10, 2));
        }

    }



    /** Tests the {@link TermUtils#asStringAt(IStrategoTerm, int)} method. */
    @Nested
    @DisplayName("asStringAt(IStrategoTerm)")
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public final class AsStringAtTests {

        private final IStrategoString t1 = factory.makeString("");
        private final IStrategoString t2 = factory.makeString("abc");
        private final IStrategoString t3 = factory.makeString("DEF");

        private final IStrategoTuple testTerm = factory.makeTuple(
                t1, t2, t3,

                factory.makeInt(42),
                factory.makeReal(4.2),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is a string term, returns the subterm")
        public void whenSubtermIsAStringTerm_returnsTheSubterm() {
            assertEquals(t1, TermUtils.asStringAt(testTerm, 0).get());
            assertEquals(t2, TermUtils.asStringAt(testTerm, 1).get());
            assertEquals(t3, TermUtils.asStringAt(testTerm, 2).get());
        }

        @Test
        @DisplayName("when subterm is not a string term, returns nothing")
        public void whenSubtermIsNotAStringTerm_returnsNothing() {
            assertFalse(TermUtils.asStringAt(testTerm, 3).isPresent());
            assertFalse(TermUtils.asStringAt(testTerm, 4).isPresent());
            assertFalse(TermUtils.asStringAt(testTerm, 5).isPresent());
            assertFalse(TermUtils.asStringAt(testTerm, 6).isPresent());
            assertFalse(TermUtils.asStringAt(testTerm, 7).isPresent());
        }

        @Test
        @DisplayName("when index is out of bounds, returns nothing")
        public void whenIndexIsOutOfBounds_returnsNothing() {
            assertFalse(TermUtils.asStringAt(testTerm, -1).isPresent());
            assertFalse(TermUtils.asStringAt(testTerm, 10).isPresent());
        }

    }

    /** Tests the {@link TermUtils#asIntAt(IStrategoTerm, int)} method. */
    @Nested
    @DisplayName("asIntAt(IStrategoTerm, int)")
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public final class AsIntAtTests {

        private final IStrategoInt t1 = factory.makeInt(0);
        private final IStrategoInt t2 = factory.makeInt(-10);
        private final IStrategoInt t3 = factory.makeInt(42);

        private final IStrategoTuple testTerm = factory.makeTuple(
                t1, t2, t3,

                factory.makeString("abc"),
                factory.makeReal(4.2),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is an int term, returns the subterm")
        public void whenSubtermIsAnIntTerm_returnsTheSubterm() {
            assertEquals(t1, TermUtils.asIntAt(testTerm, 0).get());
            assertEquals(t2, TermUtils.asIntAt(testTerm, 1).get());
            assertEquals(t3, TermUtils.asIntAt(testTerm, 2).get());
        }

        @Test
        @DisplayName("when subterm is not an int term, returns nothing")
        public void whenSubtermIsNotAnIntTerm_returnsNothing() {
            assertFalse(TermUtils.asIntAt(testTerm, 3).isPresent());
            assertFalse(TermUtils.asIntAt(testTerm, 4).isPresent());
            assertFalse(TermUtils.asIntAt(testTerm, 5).isPresent());
            assertFalse(TermUtils.asIntAt(testTerm, 6).isPresent());
            assertFalse(TermUtils.asIntAt(testTerm, 7).isPresent());
        }

        @Test
        @DisplayName("when index is out of bounds, returns nothing")
        public void whenIndexIsOutOfBounds_returnsNothing() {
            assertFalse(TermUtils.asIntAt(testTerm, -1).isPresent());
            assertFalse(TermUtils.asIntAt(testTerm, 10).isPresent());
        }

    }

    /** Tests the {@link TermUtils#asRealAt(IStrategoTerm, int)} method. */
    @Nested
    @DisplayName("asRealAt(IStrategoTerm)")
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public final class AsRealAtTests {

        private final IStrategoReal t1 = factory.makeReal(0.0);
        private final IStrategoReal t2 = factory.makeReal(-10.2);
        private final IStrategoReal t3 = factory.makeReal(4.2);

        private final IStrategoTuple testTerm = factory.makeTuple(
                t1, t2, t3,

                factory.makeString("abc"),
                factory.makeInt(42),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is a real term, returns the subterm")
        public void whenSubtermIsARealTerm_returnsTheSubterm() {
            assertEquals(t1, TermUtils.asRealAt(testTerm, 0).get());
            assertEquals(t2, TermUtils.asRealAt(testTerm, 1).get());
            assertEquals(t3, TermUtils.asRealAt(testTerm, 2).get());
        }

        @Test
        @DisplayName("when subterm is not a real term, returns nothing")
        public void whenSubtermIsNotARealTerm_returnsNothing() {
            assertFalse(TermUtils.asRealAt(testTerm, 3).isPresent());
            assertFalse(TermUtils.asRealAt(testTerm, 4).isPresent());
            assertFalse(TermUtils.asRealAt(testTerm, 5).isPresent());
            assertFalse(TermUtils.asRealAt(testTerm, 6).isPresent());
            assertFalse(TermUtils.asRealAt(testTerm, 7).isPresent());
        }

        @Test
        @DisplayName("when index is out of bounds, returns nothing")
        public void whenIndexIsOutOfBounds_returnsNothing() {
            assertFalse(TermUtils.asRealAt(testTerm, -1).isPresent());
            assertFalse(TermUtils.asRealAt(testTerm, 10).isPresent());
        }

    }

    /** Tests the {@link TermUtils#asApplAt(IStrategoTerm, int)} method. */
    @Nested
    @DisplayName("asApplAt(IStrategoTerm, int)")
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public final class AsApplAtTests {

        private final StrategoConstructor Nil = factory.makeConstructor("Nil", 0);
        private final StrategoConstructor Singleton = factory.makeConstructor("Singleton", 1);
        private final StrategoConstructor Pair = factory.makeConstructor("Pair", 2);

        private final IStrategoAppl t1 = factory.makeAppl(Nil);
        private final IStrategoAppl t2 = factory.makeAppl(Singleton, factory.makeString("abc"));
        private final IStrategoAppl t3 = factory.makeAppl(Pair, factory.makeString("abc"), factory.makeString("def"));

        private final IStrategoTuple testTerm = factory.makeTuple(
                t1, t2, t3,

                factory.makeString("abc"),
                factory.makeInt(42),
                factory.makeReal(4.2),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is an appl term, returns the subterm")
        public void whenSubtermIsAnApplTerm_returnsTheSubterm() {
            assertEquals(t1, TermUtils.asApplAt(testTerm, 0).get());
            assertEquals(t2, TermUtils.asApplAt(testTerm, 1).get());
            assertEquals(t3, TermUtils.asApplAt(testTerm, 2).get());
        }

        @Test
        @DisplayName("when subterm is not an appl term, returns nothing")
        public void whenSubtermIsNotAnApplTerm_returnsNothing() {
            assertFalse(TermUtils.asApplAt(testTerm, 3).isPresent());
            assertFalse(TermUtils.asApplAt(testTerm, 4).isPresent());
            assertFalse(TermUtils.asApplAt(testTerm, 5).isPresent());
            assertFalse(TermUtils.asApplAt(testTerm, 6).isPresent());
            assertFalse(TermUtils.asApplAt(testTerm, 7).isPresent());
        }

        @Test
        @DisplayName("when index is out of bounds, returns nothing")
        public void whenIndexIsOutOfBounds_returnsNothing() {
            assertFalse(TermUtils.asApplAt(testTerm, -1).isPresent());
            assertFalse(TermUtils.asApplAt(testTerm, 10).isPresent());
        }

    }

    /** Tests the {@link TermUtils#asListAt(IStrategoTerm, int)} method. */
    @Nested
    @DisplayName("asListAt(IStrategoTerm, int)")
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public final class AsListAtTests {

        private final IStrategoList t1 = factory.makeList();
        private final IStrategoList t2 = factory.makeList(factory.makeString("abc"));
        private final IStrategoList t3 = factory.makeList(factory.makeString("abc"), factory.makeString("def"));

        private final IStrategoTuple testTerm = factory.makeTuple(
                t1, t2, t3,

                factory.makeString("abc"),
                factory.makeInt(42),
                factory.makeReal(4.2),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is a list term, returns the subterm")
        public void whenSubtermIsAListTerm_returnsTheSubterm() {
            assertEquals(t1, TermUtils.asListAt(testTerm, 0).get());
            assertEquals(t2, TermUtils.asListAt(testTerm, 1).get());
            assertEquals(t3, TermUtils.asListAt(testTerm, 2).get());
        }

        @Test
        @DisplayName("when subterm is not a list term, returns nothing")
        public void whenSubtermIsNotAListTerm_returnsNothing() {
            assertFalse(TermUtils.asListAt(testTerm, 3).isPresent());
            assertFalse(TermUtils.asListAt(testTerm, 4).isPresent());
            assertFalse(TermUtils.asListAt(testTerm, 5).isPresent());
            assertFalse(TermUtils.asListAt(testTerm, 6).isPresent());
            assertFalse(TermUtils.asListAt(testTerm, 7).isPresent());
        }

        @Test
        @DisplayName("when index is out of bounds, returns nothing")
        public void whenIndexIsOutOfBounds_returnsNothing() {
            assertFalse(TermUtils.asListAt(testTerm, -1).isPresent());
            assertFalse(TermUtils.asListAt(testTerm, 10).isPresent());
        }

    }



    /** Tests the {@link TermUtils#toStringAt(IStrategoTerm, int)} method. */
    @Nested
    @DisplayName("toStringAt(IStrategoTerm, int)")
    public final class ToStringAtTests {

        private final IStrategoString t1 = factory.makeString("");
        private final IStrategoString t2 = factory.makeString("abc");
        private final IStrategoString t3 = factory.makeString("DEF");

        private final IStrategoTuple testTerm = factory.makeTuple(
                t1, t2, t3,

                factory.makeInt(42),
                factory.makeReal(4.2),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is a string term, returns the subterm")
        public void whenSubtermIsAStringTerm_returnsTheSubterm() {
            assertEquals(t1, TermUtils.toStringAt(testTerm, 0));
            assertEquals(t2, TermUtils.toStringAt(testTerm, 1));
            assertEquals(t3, TermUtils.toStringAt(testTerm, 2));
        }

        @Test
        @DisplayName("when subterm is not a string term, throws exception")
        public void whenSubtermIsNotAStringTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toStringAt(testTerm, 3));
            assertThrows(ClassCastException.class, () -> TermUtils.toStringAt(testTerm, 4));
            assertThrows(ClassCastException.class, () -> TermUtils.toStringAt(testTerm, 5));
            assertThrows(ClassCastException.class, () -> TermUtils.toStringAt(testTerm, 6));
            assertThrows(ClassCastException.class, () -> TermUtils.toStringAt(testTerm, 7));
        }

        @Test
        @DisplayName("when index is out of bounds, throws exception")
        public void whenIndexIsOutOfBounds_throwsException() {
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toStringAt(testTerm, -1));
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toStringAt(testTerm, 10));
        }

    }

    /** Tests the {@link TermUtils#toIntAt(IStrategoTerm, int)} method. */
    @Nested
    @DisplayName("toIntAt(IStrategoTerm, int)")
    public final class ToIntAtTests {

        private final IStrategoInt t1 = factory.makeInt(0);
        private final IStrategoInt t2 = factory.makeInt(-10);
        private final IStrategoInt t3 = factory.makeInt(42);

        private final IStrategoTuple testTerm = factory.makeTuple(
                t1, t2, t3,

                factory.makeString("abc"),
                factory.makeReal(4.2),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is an int term, returns the subterm")
        public void whenSubtermIsAnIntTerm_returnsTheSubterm() {
            assertEquals(t1, TermUtils.toIntAt(testTerm, 0));
            assertEquals(t2, TermUtils.toIntAt(testTerm, 1));
            assertEquals(t3, TermUtils.toIntAt(testTerm, 2));
        }

        @Test
        @DisplayName("when subterm is not an int term, throws exception")
        public void whenSubtermIsNotAnIntTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toIntAt(testTerm, 3));
            assertThrows(ClassCastException.class, () -> TermUtils.toIntAt(testTerm, 4));
            assertThrows(ClassCastException.class, () -> TermUtils.toIntAt(testTerm, 5));
            assertThrows(ClassCastException.class, () -> TermUtils.toIntAt(testTerm, 6));
            assertThrows(ClassCastException.class, () -> TermUtils.toIntAt(testTerm, 7));
        }

        @Test
        @DisplayName("when index is out of bounds, throws exception")
        public void whenIndexIsOutOfBounds_throwsException() {
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toIntAt(testTerm, -1));
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toIntAt(testTerm, 10));
        }

    }

    /** Tests the {@link TermUtils#toRealAt(IStrategoTerm, int)} method. */
    @Nested
    @DisplayName("toRealAt(IStrategoTerm, int)")
    public final class ToRealAtTests {

        private final IStrategoReal t1 = factory.makeReal(0.0);
        private final IStrategoReal t2 = factory.makeReal(-10.2);
        private final IStrategoReal t3 = factory.makeReal(4.2);

        private final IStrategoTuple testTerm = factory.makeTuple(
                t1, t2, t3,

                factory.makeString("abc"),
                factory.makeInt(42),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is a real term, returns the subterm")
        public void whenSubtermIsARealTerm_returnsTheSubterm() {
            assertEquals(t1, TermUtils.toRealAt(testTerm, 0));
            assertEquals(t2, TermUtils.toRealAt(testTerm, 1));
            assertEquals(t3, TermUtils.toRealAt(testTerm, 2));
        }

        @Test
        @DisplayName("when subterm is not a real term, throws exception")
        public void whenSubtermIsNotARealTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toRealAt(testTerm, 3));
            assertThrows(ClassCastException.class, () -> TermUtils.toRealAt(testTerm, 4));
            assertThrows(ClassCastException.class, () -> TermUtils.toRealAt(testTerm, 5));
            assertThrows(ClassCastException.class, () -> TermUtils.toRealAt(testTerm, 6));
            assertThrows(ClassCastException.class, () -> TermUtils.toRealAt(testTerm, 7));
        }

        @Test
        @DisplayName("when index is out of bounds, throws exception")
        public void whenIndexIsOutOfBounds_throwsException() {
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toRealAt(testTerm, -1));
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toRealAt(testTerm, 10));
        }

    }

    /** Tests the {@link TermUtils#toApplAt(IStrategoTerm, int)} method. */
    @Nested
    @DisplayName("toApplAt(IStrategoTerm, int)")
    public final class ToApplAtTests {

        private final StrategoConstructor Nil = factory.makeConstructor("Nil", 0);
        private final StrategoConstructor Singleton = factory.makeConstructor("Singleton", 1);
        private final StrategoConstructor Pair = factory.makeConstructor("Pair", 2);

        private final IStrategoAppl t1 = factory.makeAppl(Nil);
        private final IStrategoAppl t2 = factory.makeAppl(Singleton, factory.makeString("abc"));
        private final IStrategoAppl t3 = factory.makeAppl(Pair, factory.makeString("abc"), factory.makeString("def"));

        private final IStrategoTuple testTerm = factory.makeTuple(
                t1, t2, t3,

                factory.makeString("abc"),
                factory.makeInt(42),
                factory.makeReal(4.2),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is an appl term, returns the subterm")
        public void whenSubtermIsAnApplTerm_returnsTheSubterm() {
            assertEquals(t1, TermUtils.toApplAt(testTerm, 0));
            assertEquals(t2, TermUtils.toApplAt(testTerm, 1));
            assertEquals(t3, TermUtils.toApplAt(testTerm, 2));
        }

        @Test
        @DisplayName("when subterm is not an appl term, throws exception")
        public void whenSubtermIsNotAnApplTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toApplAt(testTerm, 3));
            assertThrows(ClassCastException.class, () -> TermUtils.toApplAt(testTerm, 4));
            assertThrows(ClassCastException.class, () -> TermUtils.toApplAt(testTerm, 5));
            assertThrows(ClassCastException.class, () -> TermUtils.toApplAt(testTerm, 6));
            assertThrows(ClassCastException.class, () -> TermUtils.toApplAt(testTerm, 7));
        }

        @Test
        @DisplayName("when index is out of bounds, throws exception")
        public void whenIndexIsOutOfBounds_throwsException() {
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toApplAt(testTerm, -1));
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toApplAt(testTerm, 10));
        }

    }

    /** Tests the {@link TermUtils#toListAt(IStrategoTerm, int)} method. */
    @Nested
    @DisplayName("toListAt(IStrategoTerm, int)")
    public final class ToListAtTests {

        private final IStrategoList t1 = factory.makeList();
        private final IStrategoList t2 = factory.makeList(factory.makeString("abc"));
        private final IStrategoList t3 = factory.makeList(factory.makeString("abc"), factory.makeString("def"));

        private final IStrategoTuple testTerm = factory.makeTuple(
                t1, t2, t3,

                factory.makeString("abc"),
                factory.makeInt(42),
                factory.makeReal(4.2),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is a list term, returns the subterm")
        public void whenSubtermIsAListTerm_returnsTheSubterm() {
            assertEquals(t1, TermUtils.toListAt(testTerm, 0));
            assertEquals(t2, TermUtils.toListAt(testTerm, 1));
            assertEquals(t3, TermUtils.toListAt(testTerm, 2));
        }

        @Test
        @DisplayName("when suberm is not a list term, throws exception")
        public void whenSubtermIsNotAListTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toListAt(testTerm, 3));
            assertThrows(ClassCastException.class, () -> TermUtils.toListAt(testTerm, 4));
            assertThrows(ClassCastException.class, () -> TermUtils.toListAt(testTerm, 5));
            assertThrows(ClassCastException.class, () -> TermUtils.toListAt(testTerm, 6));
            assertThrows(ClassCastException.class, () -> TermUtils.toListAt(testTerm, 7));
        }

        @Test
        @DisplayName("when index is out of bounds, throws exception")
        public void whenIndexIsOutOfBounds_throwsException() {
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toListAt(testTerm, -1));
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toListAt(testTerm, 10));
        }

    }


    /** Tests the {@link TermUtils#toJavaStringAt(IStrategoTerm, int)} method. */
    @Nested
    @DisplayName("toJavaStringAt(IStrategoTerm, int)")
    public final class ToJavaStringAtTests {

        private final IStrategoString t1 = factory.makeString("");
        private final IStrategoString t2 = factory.makeString("abc");
        private final IStrategoString t3 = factory.makeString("DEF");

        private final IStrategoTuple testTerm = factory.makeTuple(
                t1, t2, t3,

                factory.makeInt(42),
                factory.makeReal(4.2),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is a string term, returns the subterm's value")
        public void whenSubtermIsAStringTerm_returnsTheSubtermsValue() {
            assertEquals("", TermUtils.toJavaStringAt(testTerm, 0));
            assertEquals("abc", TermUtils.toJavaStringAt(testTerm, 1));
            assertEquals("DEF", TermUtils.toJavaStringAt(testTerm, 2));
        }

        @Test
        @DisplayName("when subterm is not a string term, throws exception")
        public void whenSubtermIsNotAStringTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaStringAt(testTerm, 3));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaStringAt(testTerm, 4));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaStringAt(testTerm, 5));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaStringAt(testTerm, 6));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaStringAt(testTerm, 7));
        }

        @Test
        @DisplayName("when index is out of bounds, throws exception")
        public void whenIndexIsOutOfBounds_throwsException() {
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toJavaStringAt(testTerm, -1));
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toJavaStringAt(testTerm, 10));
        }

    }

    /** Tests the {@link TermUtils#toJavaIntAt(IStrategoTerm, int)} method. */
    @Nested
    @DisplayName("toJavaInt(IStrategoTerm, int)")
    public final class ToJavaIntAtTests {

        private final IStrategoInt t1 = factory.makeInt(0);
        private final IStrategoInt t2 = factory.makeInt(-10);
        private final IStrategoInt t3 = factory.makeInt(42);

        private final IStrategoTuple testTerm = factory.makeTuple(
                t1, t2, t3,

                factory.makeString("abc"),
                factory.makeReal(4.2),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is an int term, returns the subterm's value")
        public void whenSubtermIsAnIntTerm_returnsTheSubtermsValue() {
            assertEquals(0, TermUtils.toJavaIntAt(testTerm, 0));
            assertEquals(-10, TermUtils.toJavaIntAt(testTerm, 1));
            assertEquals(42, TermUtils.toJavaIntAt(testTerm, 2));
        }

        @Test
        @DisplayName("when subterm is not an int term, throws exception")
        public void whenSubtermIsNotAnIntTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaIntAt(testTerm, 3));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaIntAt(testTerm, 4));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaIntAt(testTerm, 5));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaIntAt(testTerm, 6));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaIntAt(testTerm, 7));
        }

        @Test
        @DisplayName("when index is out of bounds, throws exception")
        public void whenIndexIsOutOfBounds_throwsException() {
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toJavaIntAt(testTerm, -1));
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toJavaIntAt(testTerm, 10));
        }

    }

    /** Tests the {@link TermUtils#toJavaRealAt(IStrategoTerm, int)} method. */
    @Nested
    @DisplayName("toJavaRealAt(IStrategoTerm, int)")
    public final class ToJavaRealAtTests {

        private final IStrategoReal t1 = factory.makeReal(0.0);
        private final IStrategoReal t2 = factory.makeReal(-10.2);
        private final IStrategoReal t3 = factory.makeReal(4.2);

        private final IStrategoTuple testTerm = factory.makeTuple(
                t1, t2, t3,

                factory.makeString("abc"),
                factory.makeInt(42),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def")),
                factory.makeList(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is a real term, returns the subterm's value")
        public void whenSubtermIsARealTerm_returnsTheSubtermsValue() {
            assertEquals(0.0, TermUtils.toJavaRealAt(testTerm, 0));
            assertEquals(-10.2, TermUtils.toJavaRealAt(testTerm, 1));
            assertEquals(4.2, TermUtils.toJavaRealAt(testTerm, 2));
        }

        @Test
        @DisplayName("when subterm is not a real term, throws exception")
        public void whenSubtermIsNotARealTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaRealAt(testTerm, 3));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaRealAt(testTerm, 4));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaRealAt(testTerm, 5));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaRealAt(testTerm, 6));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaRealAt(testTerm, 7));
        }

        @Test
        @DisplayName("when index is out of bounds, throws exception")
        public void whenIndexIsOutOfBounds_throwsException() {
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toJavaRealAt(testTerm, -1));
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toJavaRealAt(testTerm, 10));
        }

    }

    /** Tests the {@link TermUtils#toJavaListAt(IStrategoTerm, int)} method. */
    @Nested
    @DisplayName("toJavaListAt(IStrategoTerm, int)")
    public final class ToJavaListAtTests {

        private final IStrategoString e0 = factory.makeString("abc");
        private final IStrategoString e1 = factory.makeString("def");

        private final IStrategoList t1 = factory.makeList();
        private final IStrategoList t2 = factory.makeList(e0);
        private final IStrategoList t3 = factory.makeList(e0, e1);

        private final IStrategoTuple testTerm = factory.makeTuple(
                t1, t2, t3,

                factory.makeString("abc"),
                factory.makeInt(42),
                factory.makeReal(4.2),
                factory.makeAppl(factory.makeConstructor("Pair", 2), factory.makeString("abc"), factory.makeString("def")),
                factory.makeTuple(factory.makeString("abc"), factory.makeString("def"))
        );

        @Test
        @DisplayName("when subterm is a list term, returns a list of subsubterms")
        public void whenSubtermIsAListTerm_returnsAListOfSubsubterms() {
            assertEquals(Collections.emptyList(), TermUtils.toJavaListAt(testTerm, 0));
            assertEquals(Collections.singletonList(e0), TermUtils.toJavaListAt(testTerm, 1));
            assertEquals(Arrays.asList(e0, e1), TermUtils.toJavaListAt(testTerm, 2));
        }

        @Test
        @DisplayName("when subterm is not a list term, throws exception")
        public void whenSubtermIsNotAListTerm_throwsException() {
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaListAt(testTerm, 3));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaListAt(testTerm, 4));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaListAt(testTerm, 5));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaListAt(testTerm, 6));
            assertThrows(ClassCastException.class, () -> TermUtils.toJavaListAt(testTerm, 7));
        }

        @Test
        @DisplayName("when index is out of bounds, throws exception")
        public void whenIndexIsOutOfBounds_throwsException() {
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toJavaListAt(testTerm, -1));
            assertThrows(IndexOutOfBoundsException.class, () -> TermUtils.toJavaListAt(testTerm, 10));
        }

    }

}
