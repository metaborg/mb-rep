package org.spoofax.terms.typesmart;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.metaborg.util.log.ILogger;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.Term;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.AbstractWrappedTermFactory;
import org.spoofax.terms.typesmart.types.SortType;

/**
 * When constructing an application term, this term factory looks for the existence of a type-smart constructor. If such
 * constructor exists, it is used for the construction of the term. Otherwise, a standard build is performed using the
 * base factory.
 * 
 * @author Sebastian Erdweg
 */
public class TypesmartTermFactory extends AbstractWrappedTermFactory {

    public int checkInvokations = 0;
    public long totalTimeMillis = 0l;

    private final ITermFactory baseFactory;

    private final ILogger logger;
    private final TypesmartContext context;

    public TypesmartTermFactory(ITermFactory baseFactory, ILogger logger, TypesmartContext context) {
        super(baseFactory.getDefaultStorageType(), baseFactory);
        assert baseFactory
            .getDefaultStorageType() == IStrategoTerm.MUTABLE : "Typesmart factory needs to have a factory with MUTABLE terms";
        this.baseFactory = baseFactory;
        this.logger = logger;
        this.context = context;
    }

    public ITermFactory getBaseFactory() {
        return baseFactory;
    }

    @Override public IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoTerm[] kids, IStrategoList annotations) {
        IStrategoAppl term = super.makeAppl(ctr, kids, annotations);

        SortType[] sorts = checkConstruction(ctr, kids, term, null);
        if(sorts != null)
            TypesmartSortAttachment.put(term, sorts);
        return term;
    }

    /**
     * @return list of alternative result sorts; null indicates that the construction is illegal; the empty array
     *         indicates a special constructor.
     */
    private SortType[] checkConstruction(IStrategoConstructor ctr, IStrategoTerm[] kids, IStrategoTerm term,
        IStrategoTerm[] oldkids) {
        checkInvokations++;
        long start = System.currentTimeMillis();

        try {
            String cname = ctr.getName();
            if(cname.equals("") || cname.equals("None") || cname.equals("Cons"))
                return new SortType[0];

            Set<List<SortType>> sigs = context.getConstructorSignatures().get(cname);
            if(sigs == null) {
                // String message = "No signature for constructor found: " + annotateTerm(term, makeList());
                // logger.error(message);
                return null;
            }

            IStrategoTerm[] rebuildKids = new IStrategoTerm[kids.length];
            for(int i = 0, max = kids.length; i < max; i++) {
                /*
                 * TODO instead of rebuilding use TermConverter to convert explicitly when injecting terms from one
                 * factory to another.
                 */
                rebuildKids[i] = rebuildIfNecessary(kids[i]);
            }

            Set<SortType> resultingSorts = new HashSet<>();
            for(List<SortType> sig : sigs) {
                if(sig.size() - 1 == rebuildKids.length) {
                    // matching number of arguments
                    boolean matches = true;
                    for(int i = 0; i < rebuildKids.length; i++) {
                        if(!sig.get(i).matches(rebuildKids[i], context)) {
                            matches = false;
                            break;
                        }
                    }
                    if(matches) {
                        // all arguments match, so constructor is callable.
                        resultingSorts.add(sig.get(sig.size() - 1));
                    }
                }
            }

            if(resultingSorts.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                builder.append("Ill-formed constructor call, no signature matched: ").append(cname).append("\n");
                builder.append("  Signatures\t\t").append(cname).append(": ")
                    .append(TypesmartContext.printSignatures(sigs)).append("\n");
                builder.append("  Arguments\t\t").append(cname).append(":\n");
                printAppendArguments(builder, rebuildKids);
                if(oldkids != null) {
                    builder.append("\n  Old arguments\t").append(cname).append(":\n");
                    printAppendArguments(builder, oldkids);
                }
                throw new RuntimeException(builder.toString());
            }

            return resultingSorts.toArray(new SortType[resultingSorts.size()]);

        } finally {
            long end = System.currentTimeMillis();
            totalTimeMillis += (end - start);
        }
    }

    private IStrategoTerm rebuildIfNecessary(IStrategoTerm term) {
        if(term.getTermType() == IStrategoTerm.APPL) {
            IStrategoAppl appl = (IStrategoAppl) term;
            if(context.getConstructorSignatures().containsKey(appl.getConstructor().getName())
                && TypesmartSortAttachment.getSorts(appl) == null)
                return makeAppl(appl.getConstructor(), appl.getAllSubterms(), appl.getAnnotations());
            else
                return appl;
        } else {
            IStrategoTerm[] kids = new IStrategoTerm[term.getSubtermCount()];
            boolean changed = false;
            int i = 0;
            for(IStrategoTerm kid : term) {
                IStrategoTerm newkid = rebuildIfNecessary(kid);
                kids[i] = newkid;
                changed = changed || kid != newkid;
            }
            if(changed) {
                switch(term.getTermType()) {
                    case IStrategoTerm.LIST:
                        return makeList(kids, term.getAnnotations());
                    case IStrategoTerm.TUPLE:
                        return makeTuple(kids, term.getAnnotations());
                    default:
                        throw new IllegalStateException();
                }
            } else {
                return term;
            }
        }
    }

    private void printAppendArguments(StringBuilder builder, IStrategoTerm[] kids) {
        for(IStrategoTerm kid : kids) {
            SortType[] types = TypesmartSortAttachment.getSorts(kid);
            if(types == null)
                builder.append("\t\t[] -- ");
            else
                builder.append("\t\t").append(Arrays.toString(types)).append(" -- ");
            builder.append(Term.removeAnnotations(kid, baseFactory));
            builder.append("\n");
        }
    }

    /**
     * Identical to {@link TermFactory#annotateTerm(IStrategoTerm, IStrategoList)} except that it retains sort
     * attachments.
     */
    @Override public IStrategoTerm annotateTerm(IStrategoTerm term, IStrategoList annotations) {
        IStrategoTerm result = super.annotateTerm(term, annotations);
        TypesmartSortAttachment attach = TypesmartSortAttachment.get(term);
        if(attach != null)
            TypesmartSortAttachment.put(result, attach);

        return result;
    }

    public ITermFactory getFactoryWithStorageType(int storageType) {
        if(storageType != IStrategoTerm.MUTABLE)
            throw new RuntimeException("Typesmart factory cannot work with NON-MUTABLE terms");

        if(storageType == getDefaultStorageType())
            return this;

        return new TypesmartTermFactory(baseFactory.getFactoryWithStorageType(storageType), logger, context);
    }

    /**
     * Recheck invariant of typesmart constrcutor.
     */
    @Override public IStrategoAppl replaceAppl(IStrategoConstructor ctr, IStrategoTerm[] kids, IStrategoAppl old) {
        IStrategoAppl term = super.makeAppl(ctr, kids, old.getAnnotations());

        SortType[] sorts = checkConstruction(ctr, kids, term, old.getAllSubterms());
        if(sorts != null)
            TypesmartSortAttachment.put(term, sorts);
        return term;
    }
}
