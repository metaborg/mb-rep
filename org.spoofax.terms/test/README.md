# Tests
This document describes how the tests are setup. The hierarchy of test classes and interfaces follow the hierarchy of
actual classes and interfaces being tested, and their contain interfaces testing their members. For example, tests for
interfaces are grouped in a test interface, whereas tests for abstract and concrete classes are grouped in
abstract and concrete test classes respectively. Each of these test classes or interfaces contain a `Fixture` interface
with construction methods. The abstract and concrete test classes also contain a `FixtureImpl` static (abstract) class
with implementation of those construction methods. Finally, the concrete test classes contain instantiations of the
member test interfaces.


## Testing an Interface
It is important to write tests that ensure that any implementation of
an interface conforms to the interface. For example, given an interface:

    public interface IStrategoTerm extends ISimpleTerm

The corresponding test interface is:

    @DisplayName("IStrategoTerm")
    public interface IStrategoTermTests {
    
        interface Fixture extends ISimpleTermTests.Fixture {
    
            IStrategoTerm createIStrategoTerm(
                @Nullable List<IStrategoTerm> subterms,
                @Nullable IStrategoList annotations,
                @Nullable List<ITermAttachment> attachments);
    
            // other construction methods            
                        
        }
    
        // member test interfaces    
        
    }

Note the `Fixture` interface, which extends the fixtures corresponding to the interfaces that are extended by
the interface being tested. See also: [Fixtures with Construction Methods](#fixtures-with-construction-methods).


## Testing an Abstract Class
Similarly, it is important to test the behavior of abstract classes.
By writing the tests once, they apply to all classes that extend
the abstract class. For example, given an abstract class:

    public abstract class StrategoTerm extends AbstractSimpleTerm implements IStrategoTerm

The corresponding abstract test class is:

    @DisplayName("StrategoTerm")
    public abstract class StrategoTermTests {
    
        public interface Fixture extends IStrategoTermTests.Fixture {
    
            @Override
            StrategoTerm createIStrategoTerm(
                @Nullable List<IStrategoTerm> subterms,
                @Nullable IStrategoList annotations,
                @Nullable List<ITermAttachment> attachments);
    
            // other construction methods
                
        }
    
        abstract static class FixtureImpl extends AbstractSimpleTermTests.FixtureImpl implements Fixture {
    
            @Override
            public abstract AbstractSimpleTerm createAbstractSimpleTerm(
                @Nullable List<ISimpleTerm> subterms,
                @Nullable List<ITermAttachment> attachments);
    
            @Override
            @Nullable
            public ISimpleTerm createISimpleTerm(
                @Nullable List<ISimpleTerm> subterms,
                @Nullable List<ITermAttachment> attachments) {
                // concrete implementation
            }
    
        }
    
        // member test interfaces    
    
    }

Note that apart from the [`Fixture` interface](#fixtures-with-construction-methods) extending the implemented interface's
test fixture, this abstract class also has a static abstract `FixtureImpl` class that implements some of the construction
methods. It extends the fixture corresponding to the class that was extended by the class being tested.
See also: [Fixture Implementations](#fixture-implementations).


## Testing a Concrete Class
Finally, when testing a concrete class, such as:

    public class StrategoInt extends StrategoTerm implements IStrategoInt

The corrsponding test interface is:

    @DisplayName("St splayName("StrategoInt")
    public class StrategoIntTests {
    
        public interface Fixture extends IStrategoIntTests.Fixture {
    
            @Override
            StrategoInt createIStrategoInt(
                @Nullable Integer value,
                @Nullable IStrategoList annotations,
                @Nullable List<ITermAttachment> attachments);
    
        }
    
    
        public static class FixtureImpl extends StrategoTermTests.FixtureImpl implements Fixture {
    
            @Override
            public StrategoInt createIStrategoInt(
                @Nullable Integer value,
                @Nullable IStrategoList annotations,
                @Nullable List<ITermAttachment> attachments) {
                // concrete implementation
            }
    
            @Override
            public StrategoTerm createIStrategoTerm(
                @Nullable List<IStrategoTerm> subterms,
                @Nullable IStrategoList annotations,
                @Nullable List<ITermAttachment> attachments) {
                // concrete implementation
            }
    
        }
        
        // member test interfaces
        
        // @formatter:off
        @Nested class IntValueTests          extends FixtureImpl implements IStrategoIntTests.IntValueTests {}
        @Nested class GetSubtermTests        extends FixtureImpl implements IStrategoTermTests.GetSubtermTests {}
        @Nested class RemoveAttachmentTests  extends FixtureImpl implements ISimpleTermTests.RemoveAttachmentTests {}
        // @formatter:on
    
    }

Note that this test class is not abstract, as the class being tested is also not abstract. Also note that
the [`FixtureImpl` class](#fixture-implementations) is also not abstract and therefore provides concrete implementations
of all construction methods. Finally, the `@Nested class` declarations are explained in:
[Instantiating Member Tests](#instantiating-member-tests).



## Fixtures with Construction Methods
To ensure the test classes and interfaces are parametric in the implementation,
each declares one or more construction methods, which are used to
create the Subject-Under-Test (SUT). They are declared in the `Fixture` interface of each test
class or interface, and their signature template for testing a type `T` is:

    T createT(@Nullable A0 arg0, @Nullable A1 arg1, ..)

Whenever `null` is passed to any argument to this method, it means the
test does not care about its value and the construction method should
pick a suitable value. If the construction method is unable to construct
an object with the given parameters (for example, a `IStrategoTemplate`
has always exactly one subterm, and therefore it is impossible to
construct an instance with more or less than one subterm), then the
construction method throws:

    throw new TestAbortedException(TestBase.TEST_INSTANCE_NOT_CREATED)

This aborts the execution of the test without failing it.


## Fixture Implementations
Tests for concrete and abstract classes have to provide a `FixtureImpl` (abstract) class.
Whenever the class being tested extends a class `C`, the `FixtureImpl` must extend
the corresponding fixture `CTests.Fixture`.

In abstract classes, the `FixtureImpl` must be abstract and some of the construction methods
are allowed to be abstract as well.



## Testing a Class or Interface Member
For each member of the class or interface, a nested `interface`
is defined in the test interface or class that extends it.
For example, for testing the `intValue()` getter of `IStrategoInt`,
the corresponding nested interface is:


    @DisplayName("intValue()")
    interface IntValueTests extends Fixture {

        @Test
        @DisplayName("returns the value of the term")
        default void returnsTheValueOfTheTerm() {
            IStrategoInt sut = createStrategoInt(10, null, null);
            assertEquals(10, sut.intValue());
        }

    }

Note that the interface extends the `Fixture` interface of the test class or interface.
Also note the use of `default` to define the tests themselves, which is required
for tests in interfaces.


## Extended Tests for a Class or Interface Member
To add additional tests for a particular class or interface member, the extra test
must be defined in a member test interface that extends the member test interface being extended.
For example, `IStrategoIntTests` has extra tests for the `getTermType()` method whose tests
are already defined in `IStrategoTermTests`. Therefore, to extend them we wrote:


    @DisplayName("getTermType()")
    interface GetTermTypeTests extends Fixture, IStrategoTermTests.GetTermTypeTests {

        // extra tests

    }



## Instantiating Member Tests
Once defined, the tests of a member must be instantiated. This is only done
in the test classes corresponding to the concrete classes of the hierarchy, as these are
the only ones that can actually create new instances. This is done in an `@Inner class`
that extends the `FixtureImpl` and implements the member test interface with the tests.

For example, in `StrategoIntTest` (the tests class for `StrategoInt` which is the concrete
implementation of `IStrategoInt`) the following line is added to 'import' the tests for `intValue()`:

    @Nested class IntValueTests extends FixtureImpl implements IStrategoIntTests.IntValueTests {} 

