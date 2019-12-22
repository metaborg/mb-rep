package org.spoofax.terms.typesmart;

import mb.terms.*;
import org.spoofax.terms.typesmart.types.SortType;

import java.util.*;

/**
 * When constructing an application term, this term factory looks for the existence of a type-smart constructor. If such
 * constructor exists, it is used for the construction of the term. Otherwise, a standard build is performed using the
 * base factory.
 *
 * @author Sebastian Erdweg
 */
public class TypesmartTermFactory extends AbstractWrappedTermFactory {

    private final TypesmartContext context;
    public int checkInvokations = 0;
    public long totalTimeMillis = 0l;

    public TypesmartTermFactory(AbstractTermFactory factory, TypesmartContext context) {
        super(factory);
        this.context = context;
    }

    @Override
    public ITermApplication newAppl(String cons, List<ITerm> children, List<ITerm> annotations) {
        SortType[] sorts = checkConstruction(cons, children, null);
        if (sorts != null) {
            return super.newAppl(cons, children, annotations).withAttachment(new TypesmartSortAttachment(sorts));
        } else {
            return super.newAppl(cons, children, annotations);
        }
    }

    /**
     * @return list of alternative result sorts; null indicates that the construction is illegal; the empty array
     * indicates a special constructor.
     */
    private SortType[] checkConstruction(String ctr, List<ITerm> kids, List<ITerm> oldkids) {
        checkInvokations++;
        long start = System.currentTimeMillis();

        try {
            if (ctr.equals("") || ctr.equals("None") || ctr.equals("Cons"))
                return new SortType[0];

            Set<List<SortType>> sigs = context.getConstructorSignatures().get(ctr);
            if (sigs == null) {
                // String message = "No signature for constructor found: " + annotateTerm(term, makeList());
                // logger.error(message);
                return null;
            }

            List<ITerm> rebuildKids = new ArrayList<>(kids.size());
            for (ITerm kid : kids) {
                rebuildKids.add(rebuildIfNecessary(kid));
            }

            Set<SortType> resultingSorts = new HashSet<>();
            for (List<SortType> sig : sigs) {
                if (sig.size() - 1 == rebuildKids.size()) {
                    // matching number of arguments
                    boolean matches = true;
                    for (int i = 0; i < rebuildKids.size(); i++) {
                        ITerm kid = rebuildKids.get(i);
                        if (!sig.get(i).matches(kid, context)) {
                            matches = false;
                            break;
                        }
                    }
                    if (matches) {
                        // all arguments match, so constructor is callable.
                        resultingSorts.add(sig.get(sig.size() - 1));
                    }
                }
            }

            if (resultingSorts.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                builder.append("Ill-formed constructor call, no signature matched: ").append(ctr).append("\n");
                builder.append("  Signatures\t\t").append(ctr).append(": ")
                        .append(TypesmartContext.printSignatures(sigs)).append("\n");
                builder.append("  Arguments\t\t").append(ctr).append(":\n");
                printAppendArguments(builder, rebuildKids);
                if (oldkids != null) {
                    builder.append("\n  Old arguments\t").append(ctr).append(":\n");
                    printAppendArguments(builder, oldkids);
                }
                throw new RuntimeException(builder.toString());
            }

            return resultingSorts.toArray(new SortType[0]);

        } finally {
            long end = System.currentTimeMillis();
            totalTimeMillis += (end - start);
        }
    }

    private ITerm rebuildIfNecessary(ITerm term) {
        switch (term.getTermKind()) {
            case Application:
                ITermApplication appl = (ITermApplication) term;
                String cons = appl.constructor();
                if (context.getConstructorSignatures().containsKey(cons)
                        && TypesmartSortAttachment.getSorts(appl) == null) {
                    SortType[] sorts = checkConstruction(cons, appl.children(), null);
                    if (sorts != null) {
                        return term.withAttachment(new TypesmartSortAttachment(sorts));
                    }
                }
                return term;
            case List:
                ITermList list = (ITermList) term;
                ArrayList<ITerm> listChildren = new ArrayList<>(list.children().size());
                for(ITerm child : list.children()) {
                    listChildren.add(rebuildIfNecessary(child));
                }
                return replaceList(list, listChildren);
            case Int:
            case Float:
            case String:
                return term;
            case Set:
                ITermSet set = (ITermSet) term;
                io.usethesource.capsule.Set.Transient<ITerm> setChildren = io.usethesource.capsule.Set.Transient.of();
                for(ITerm child : set.set()) {
                    setChildren.__insert(rebuildIfNecessary(child));
                }
                return replaceSet(set, setChildren.freeze());
            case Map:
                ITermMap map = (ITermMap) term;
                io.usethesource.capsule.Map.Transient<ITerm, ITerm> mapChildren = io.usethesource.capsule.Map.Transient.of();
                for(Map.Entry<ITerm, ITerm> entry : map.map().entrySet()) {
                    mapChildren.__put(rebuildIfNecessary(entry.getKey()), rebuildIfNecessary(entry.getValue()));
                }
                return replaceMap(map, mapChildren.freeze());
            case Extension:
                return term;
            default:
                throw new IllegalStateException();
        }
    }

    private void printAppendArguments(StringBuilder builder, List<ITerm> kids) {
        for (ITerm kid : kids) {
            SortType[] types = TypesmartSortAttachment.getSorts(kid);
            if (types == null)
                builder.append("\t\t[] -- ");
            else
                builder.append("\t\t").append(Arrays.toString(types)).append(" -- ");
            builder.append(kid.withAnnotations());
            builder.append("\n");
        }
    }

    /**
     * Recheck invariant of typesmart constrcutor.
     */
    @Override
    public ITermApplication replaceAppl(ITerm old, String cons, List<ITerm> children, List<ITerm> annotations) {
        SortType[] sorts;
        if (old instanceof ITermApplication) {
            sorts = checkConstruction(cons, children, ((ITermApplication) old).children());
        } else {
            sorts = checkConstruction(cons, children, null);
        }
        if (sorts != null) {
            return super.replaceAppl(old, cons, children, annotations).withAttachment(new TypesmartSortAttachment(sorts));
        } else {
            return super.replaceAppl(old, cons, children, annotations);
        }
    }
}
