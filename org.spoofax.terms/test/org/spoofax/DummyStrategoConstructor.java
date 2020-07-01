package org.spoofax;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;


/**
 * A dummy term constructor.
 */
public class DummyStrategoConstructor extends DummyStrategoTerm implements IStrategoConstructor {

    public static IStrategoConstructor Dummy0 = new DummyStrategoConstructor("Dummy", 0);
    public static IStrategoConstructor Dummy1 = new DummyStrategoConstructor("Dummy", 1);
    public static IStrategoConstructor Dummy2 = new DummyStrategoConstructor("Dummy", 2);
    public static IStrategoConstructor Dummy3 = new DummyStrategoConstructor("Dummy", 3);
    public static IStrategoConstructor Dummy4 = new DummyStrategoConstructor("Dummy", 4);

    private final String name;
    private final int arity;

    public DummyStrategoConstructor(String name, int arity) {
        this.name = name;
        this.arity = arity;
    }

    @Override
    public IStrategoAppl instantiate(ITermFactory factory, IStrategoTerm... kids) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public IStrategoAppl instantiate(ITermFactory factory, IStrategoList kids) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getArity() {
        return this.arity;
    }

}
