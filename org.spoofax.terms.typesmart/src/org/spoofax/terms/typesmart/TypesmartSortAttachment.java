package org.spoofax.terms.typesmart;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.terms.StrategoString;
import org.spoofax.terms.StrategoTerm;
import org.spoofax.terms.attachments.AbstractTermAttachment;
import org.spoofax.terms.attachments.OriginAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;
import org.spoofax.terms.typesmart.types.SortType;
import org.spoofax.terms.typesmart.types.TList;
import org.spoofax.terms.typesmart.types.TSort;
import org.spoofax.terms.util.ArrayIterator;

/**
 * @author Sebastian Erdweg
 */
public class TypesmartSortAttachment extends AbstractTermAttachment {

    private static final long serialVersionUID = -1071986538130031851L;

    public static final TermAttachmentType<TypesmartSortAttachment> TYPE =
        new TermAttachmentType<TypesmartSortAttachment>(TypesmartSortAttachment.class, "TypesmartSortAttachment", 1) {

            @Override protected IStrategoTerm[] toSubterms(ITermFactory factory, TypesmartSortAttachment attachment) {
                throw new UnsupportedOperationException();
            }

            @Override protected TypesmartSortAttachment fromSubterms(IStrategoTerm[] subterms) {
                throw new UnsupportedOperationException();
            }
        };

    static final class StrategoSortTypeArray extends StrategoTerm {
        private static final long serialVersionUID = 5983117664125726796L;

        private SortType[] ar;

        protected StrategoSortTypeArray(SortType[] ar) {
            super(null, IStrategoTerm.MUTABLE);
            this.ar = ar;
        }

        @Override public int getSubtermCount() {
            return ar.length;
        }

        @Override public IStrategoTerm getSubterm(int index) {
            return new StrategoString(ar[index].toString(), null, IStrategoTerm.MUTABLE);
        }

        @Override public IStrategoTerm[] getAllSubterms() {
            IStrategoTerm[] ts = new IStrategoTerm[ar.length];
            for(int i = 0; i < ar.length; i++)
                ts[i] = getSubterm(i);
            return ts;
        }

        @Override public int getTermType() {
            return IStrategoTerm.LIST;
        }

        @Override public void prettyPrint(ITermPrinter pp) {
            throw new UnsupportedOperationException();
        }

        @Override public void writeAsString(Appendable output, int maxDepth) throws IOException {
            output.append(Arrays.toString(ar));
        }

        @Override public Iterator<IStrategoTerm> iterator() {
            return new ArrayIterator<>(getAllSubterms());
        }

        @Override protected boolean doSlowMatch(IStrategoTerm second, int commonStorageType) {
            if(second instanceof StrategoSortTypeArray)
                return Arrays.equals(ar, ((StrategoSortTypeArray) second).ar);
            return false;
        }

        @Override protected int hashFunction() {
            return Arrays.hashCode(ar);
        }

        @Override public String toString() {
            return Arrays.toString(ar);
        }
    }

    public TermAttachmentType<?> getAttachmentType() {
        return TYPE;
    }

    public TypesmartSortAttachment(StrategoSortTypeArray sorts) {
        this.sorts = sorts;
    }

    StrategoSortTypeArray sorts;

    public StrategoSortTypeArray getSorts() {
        return sorts;
    }

    public static TypesmartSortAttachment get(ISimpleTerm term) {
        TypesmartSortAttachment attach = term.getAttachment(TYPE);
        if(attach != null)
            return attach;

        // String sort = ImploderAttachment.getSort(term);
        // if (sort != null) {
        // IStrategoTerm sortTerm = factory.makeAppl(
        // factory.makeConstructor("SortNoArgs", 1),
        // factory.makeString(sort));
        // return new TypesmartSortAttachment(sortTerm);
        // }

        return null;
    }

    public static SortType[] getSorts(ISimpleTerm term) {
        TypesmartSortAttachment attach = get(term);
        if(attach != null) {
            return attach.getSorts().ar;
        } else {
            ImploderAttachment imploderAttach = ImploderAttachment.get(term);
            if(imploderAttach != null) {
                if(imploderAttach.isSequenceAttachment()) {
                    return new SortType[] { new TList(new TSort(imploderAttach.getElementSort())) };
                } else {
                    return new SortType[] { new TSort(imploderAttach.getSort()) };
                }
            } else {
                IStrategoTerm origin = OriginAttachment.getOrigin(term);
                if(origin != null) {
                    return getSorts(origin);
                } else {
                    return null;
                }
            }
        }
    }

    public static void put(ISimpleTerm term, SortType[] sorts) {
        put(term, new StrategoSortTypeArray(sorts));
    }

    public static void put(ISimpleTerm term, StrategoSortTypeArray sorts) {
        term.putAttachment(new TypesmartSortAttachment(sorts));
    }

    public static void put(ISimpleTerm term, TypesmartSortAttachment attach) {
        term.putAttachment(attach);
    }

    @Override public String toString() {
        return "(sorts=" + sorts.toString() + ")";
    }
}
