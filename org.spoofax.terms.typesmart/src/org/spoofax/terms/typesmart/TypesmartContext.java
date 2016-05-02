package org.spoofax.terms.typesmart;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.spoofax.terms.typesmart.types.SortType;

public class TypesmartContext implements Serializable {
    private static final long serialVersionUID = -2343547085277594696L;

    private final Map<String, Set<List<SortType>>> constructorSignatures;
    private final Set<SortType> lexicals;
    private final Set<Entry<SortType, SortType>> injections;
    private transient Map<SortType, Set<SortType>> injectionsClosure;

    /**
     * @param constructorSignatures
     *            Sequence of signatures for each constructor. The last element of the inner array marks the return type
     *            of the constructor.
     * @param lexicals
     *            Set of lexical nonterminals (considered subtype of Lexical then).
     * @param injections
     *            Nonterminal injection pairs from -> to.
     */
    public TypesmartContext(Map<String, Set<List<SortType>>> constructorSignatures, Set<SortType> lexicals,
        Set<Entry<SortType, SortType>> injections) {
        this.constructorSignatures = constructorSignatures;
        this.lexicals = lexicals;
        this.injections = injections;
    }

    public Map<String, Set<List<SortType>>> getConstructorSignatures() {
        return constructorSignatures;
    }

    public Set<SortType> getLexicals() {
        return lexicals;
    }

    public Set<Entry<SortType, SortType>> getInjections() {
        return injections;
    }

    public Map<SortType, Set<SortType>> getInjectionsClosure() {
        if(injectionsClosure == null)
            injectionsClosure = computeClosure();
        return injectionsClosure;
    }

    private Map<SortType, Set<SortType>> computeClosure() {
        Map<SortType, Set<SortType>> closure = new HashMap<>();
        for(Entry<SortType, SortType> e : injections) {
            Set<SortType> s = closure.get(e.getKey());
            if(s == null) {
                s = new HashSet<>();
                closure.put(e.getKey(), s);
            }
            s.add(e.getValue());
        }

        LinkedList<Entry<SortType, SortType>> todo = new LinkedList<>(injections);

        while(!todo.isEmpty()) {
            Entry<SortType, SortType> e = todo.pop();
            SortType from = e.getKey();
            SortType to = e.getValue();
            Set<SortType> transTos = closure.get(to);
            if(transTos != null) {
                Set<SortType> fromTos = closure.get(from);
                for(SortType transTo : transTos) {
                    if(fromTos.add(transTo))
                        todo.add(new SimpleEntry<>(from, transTo));
                }
            }
        }

        return Collections.unmodifiableMap(closure);
    }

    public static String printSignature(List<SortType> sig) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0, max = sig.size() - 1; i < max; i++) {
            builder.append(sig.get(i)).append(" ");
        }
        if(sig.size() > 1) {
            builder.append("-> ");
        }
        builder.append(sig.get(sig.size() - 1));
        return builder.toString();
    }

    public static String printSignatures(Iterable<List<SortType>> sigs) {
        StringBuilder builder = new StringBuilder("[");
        Iterator<List<SortType>> it = sigs.iterator();
        while(it.hasNext()) {
            builder.append(printSignature(it.next()));
            if(it.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.append("]").toString();
    }
}
