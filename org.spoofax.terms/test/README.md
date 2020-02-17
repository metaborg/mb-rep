# Tests
This document describes how the tests are setup. The hierarchy of test interfaces follow the hierarchy of
actual classes and interfaces being tested, and their contain interfaces testing their members.


## Testing a Class or Interface
It is important to write tests that ensure that any implementation of
an interface conforms to the interface. For example, given an interface:

    public interface IStrategoTerm extends ISimpleTerm

The corresponding test interface is:

    public interface IStrategoTermTests extends ISimpleTermTests

Similarly, it is important to test the behavior of abstract classes.
By writing the tests once, they apply to all classes that extend
the abstract class. For example, given an abstract class:

    public abstract class StrategoTerm extends AbstractSimpleTerm implements IStrategoTerm

The corresponding test interface is:

    public interface StrategoTermTests extends AbstractSimpleTermTests, IStrategoTermTests

Finally, when testing a concrete class, such as:

    public class StrategoInt extends StrategoTerm implements IStrategoInt

The corrsponding test interface is:

    public interface StrategoIntTests extends StrategoTermTests, IStrategoIntTests


## Construction Methods
To ensure the test classes and interfaces are parametric in the implementation,
each declares their own construction method, which is used to
create the Subject-Under-Test (SUT). Their signature template for testing a type `T` is:

    T createT(@Nullable A0 arg0, @Nullable A1 arg1, ..)

Whenever `null` is passed as an argument to this method, it means the
test does not care about its value and the construction method should
pick a suitable value. If the construction method is unable to construct
an object with the given parameters (for example, a `IStrategoTemplate`
has always exactly one subterm, and therefore it is impossible to
construct an instance with more or less than one subterm), then the
construction method throws:

    throw new TestAbortedException(TestBase.TEST_INSTANCE_NOT_CREATED)

This aborts the execution of the test without failing it.


## Testing a Class or Interface Member
For each member of the class or interface, a nested (not _inner_) `interface`
or `static class`
is defined in the test interface or class that extends it.
For example, for testing the `intValue()` getter of `IStrategoInt`,
the corresponding nested interface is:


    @DisplayName("intValue()")
    interface IntValueTests extends IStrategoIntTests {

        @Test
        @DisplayName("returns the value of the term")
        default void returnsTheValueOfTheTerm() {
            IStrategoInt sut = createStrategoInt(10, null, null);
            assertEquals(10, sut.intValue());
        }

    }

Also note the use of `default` to define the tests themselves, which is required
for tests in interfaces.



## Instantiating the Tests of a Member
Once defined, the tests of a member must be instantiated. This can only be
done for the non-interface non-abstract classes of the hierarchy, as these are
the only ones that can actually create new instances. This is done in the corresponding
non-interface non-abstract test class by implementing the constructor methods in a `Fixture` static class
and adding the member test interfaces as `@Inner class` members that extend the `Fixture` class
and implement their member test interface.
For example, in `StrategoIntTest` (the tests class for `StrategoInt` which is the concrete implementation of `IStrategoInt`)
the following line is added to 'import' the tests for `intValue()`:

    @Inner class IntValueTests extends Fixture implements StrategoIntTests.IntValueTests {} 

