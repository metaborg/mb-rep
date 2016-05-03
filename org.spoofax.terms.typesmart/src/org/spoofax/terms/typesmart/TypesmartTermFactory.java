package org.spoofax.terms.typesmart;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.util.log.ILogger;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.AbstractWrappedTermFactory;
import org.spoofax.terms.typesmart.types.SortType;

/**
 * When constructing an application term, this term factory looks for the existence of a type-smart constructor. If such
 * constructor exists, it is used for the construction of the term. Otherwise, a standard build is performed using the
 * base factory.
 * 
 * @author Sebastian Erdweg
 * @author Vlad Vergu
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

    public TypesmartTermFactory(ITermFactory baseFactory, ILogger logger, FileObject contextFile) {
        super(baseFactory.getDefaultStorageType(), baseFactory);
        assert baseFactory
            .getDefaultStorageType() == IStrategoTerm.MUTABLE : "Typesmart factory needs to have a factory with MUTABLE terms";
        this.baseFactory = baseFactory;
        this.logger = logger;
        
        TypesmartContext context;
        try(ObjectInputStream ois = new ObjectInputStream(contextFile.getContent().getInputStream())) {
            context = (TypesmartContext) ois.readObject();
        } catch(FileNotFoundException e) {
            logger.warn("Typesmart context file not found", e);
            context = new TypesmartContext(Collections.<String, Set<List<SortType>>>emptyMap(),
                Collections.<SortType>emptySet(), Collections.<Entry<SortType, SortType>>emptySet());
        } catch(IOException | ClassNotFoundException e) {
            logger.error("Error while loading typesmart term factory", e);
            throw new RuntimeException(e);
        }
        this.context = context;
    }

    public ITermFactory getBaseFactory() {
        return baseFactory;
    }

    @Override public IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoTerm[] kids, IStrategoList annotations) {
        IStrategoAppl term = super.makeAppl(ctr, kids, annotations);

        SortType[] sorts = checkConstruction(ctr, kids, term);
        if(sorts != null)
            TypesmartSortAttachment.put(term, sorts);
        return term;
    }

    /**
     * @return list of alternative result sorts; null indicates that the construction is illegal; the empty array
     *         indicates a special constructor.
     */
    private SortType[] checkConstruction(IStrategoConstructor ctr, IStrategoTerm[] kids, IStrategoTerm term) {
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

            Set<SortType> resultingSorts = new HashSet<>();
            for(List<SortType> sig : sigs) {
                if(sig.size() - 1 == kids.length) {
                    // matching number of arguments
                    boolean matches = true;
                    for(int i = 0; i < kids.length; i++) {
                        if(!sig.get(i).matches(kids[i], context)) {
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
                String message = "Ill-formed constructor call of " + cname + ", no signature matched.\n  Signatures "
                    + TypesmartContext.printSignatures(sigs) + "\n  Arguments " + Arrays.toString(kids);
                logger.error(message);
                // prevent error propragation by assuming this term has the right sorts
                for(List<SortType> sig : sigs) {
                    resultingSorts.add(sig.get(sig.size() - 1));
                }
            }

            return resultingSorts.toArray(new SortType[resultingSorts.size()]);

        } finally {
            long end = System.currentTimeMillis();
            totalTimeMillis += (end - start);
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
    @Override public IStrategoAppl replaceAppl(IStrategoConstructor constructor, IStrategoTerm[] kids,
        IStrategoAppl old) {
        return makeAppl(constructor, kids, old.getAnnotations());
    }
}
