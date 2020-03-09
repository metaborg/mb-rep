package org.spoofax.terms.util;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoAppl;
import org.spoofax.terms.StrategoArrayList;
import org.spoofax.terms.StrategoInt;
import org.spoofax.terms.StrategoString;
import org.spoofax.terms.StrategoTuple;
import java.util.Collection;

/**
 * A Stratego Term building class. The static methods can build anything but stratego application constructors.
 * Compiled Stratego code uses cached constructors where the objects are compared with object identity, therefore when
 * you build your own, you should also use shared constructors in the factory.
 *
 * <b>N.B.</b> You can build Stratego strings with static methods from the class, but those will not be known to the
 * TermFactory and therefore will not be taken into consideration by Stratego in its new/newname strategies which are
 * supposed to create globally unique strings.
 */
public class B {
    public final ITermFactory tf;

    public B(ITermFactory tf) {
        this.tf = tf;
    }

    public static IStrategoAppl appl(IStrategoConstructor cons, IStrategoTerm... children) {
        assert cons.getArity() == children.length : "Expected constructor with arity " + children.length + ", but got arity " + cons.getArity();
        return new StrategoAppl(cons, children, null);
    }

    public static IStrategoTuple tuple(IStrategoTerm... children) {
        return new StrategoTuple(children, null);
    }

    public static IStrategoString string(String value) {
        return new StrategoString(value, null);
    }

    public static IStrategoInt integer(int value) {
        return new StrategoInt(value, null);
    }

    public static IStrategoList list(IStrategoTerm... terms) {
        return new StrategoArrayList(terms);
    }

    public static IStrategoList list(Collection<IStrategoTerm> terms) {
        return StrategoArrayList.fromList(terms);
    }

    public static FixedSizeListBuilder fixedSizeListBuilder(int size) {
        return new FixedSizeListBuilder(size);
    }

    public IStrategoAppl applShared(String cons, IStrategoTerm... children) {
        return tf.makeAppl(tf.makeConstructor(cons, children.length), children, null);
    }

    public IStrategoConstructor consShared(String cons, int children) {
        return tf.makeConstructor(cons, children);
    }

    public IStrategoTuple tupleShared(IStrategoTerm... children) {
        return tf.makeTuple(children);
    }

    public IStrategoString stringShared(String value) {
        return tf.makeString(value);
    }

    public IStrategoInt integerShared(int value) {
        return tf.makeInt(value);
    }

    public static class FixedSizeListBuilder {
        private final IStrategoTerm[] array;
        private int index = 0;
        private boolean built = false;

        public FixedSizeListBuilder(int size) {
            this.array = new IStrategoTerm[size];
        }

        public void add(IStrategoTerm term) {
            if(index >= array.length) {
                throw new ArrayIndexOutOfBoundsException();
            }
            if(built) {
                throw new UnsupportedOperationException("Cannot add to a built list.");
            }
            array[index] = term;
            index++;
        }

        public IStrategoList build() {
            if(index != array.length) {
                throw new UnsupportedOperationException("Cannot build a partially filled list.");
            }
            built = true;
            return new StrategoArrayList(array);
        }
    }
}
