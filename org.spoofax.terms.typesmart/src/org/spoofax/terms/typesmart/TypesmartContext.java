package org.spoofax.terms.typesmart;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.spoofax.terms.typesmart.types.SortType;

public class TypesmartContext {
    private final Map<String, SortType[][]> constructorSignatures;
    private final Set<String> lexicals;
    private final Set<Entry<String, String>> injections;
    private transient Map<String, Set<String>> injectionsClosure;

    /**
     * @param constructorSignatures
     *            Sequence of signatures for each constructor. The last element of the inner array marks the return type
     *            of the constructor.
     * @param lexicals
     *            Set of lexical nonterminals (considered subtype of Lexical then).
     * @param injections
     *            Nonterminal injection pairs from -> to.
     */
    public TypesmartContext(Map<String, SortType[][]> constructorSignatures, Set<String> lexicals,
        Set<Entry<String, String>> injections) {
        this.constructorSignatures = constructorSignatures;
        this.lexicals = lexicals;
        this.injections = injections;
    }

    public Map<String, SortType[][]> getConstructorSignatures() {
        return constructorSignatures;
    }

    public Set<String> getLexicals() {
        return lexicals;
    }

    public Set<Entry<String, String>> getInjections() {
        return injections;
    }

    public Map<String, Set<String>> getInjectionsClosure() {
        if(injectionsClosure == null)
            injectionsClosure = computeClosure();
        return injectionsClosure;
    }

    private Map<String, Set<String>> computeClosure() {
        Map<String, Set<String>> closure = new HashMap<>();
        for(Entry<String, String> e : injections) {
            Set<String> s = closure.get(e.getKey());
            if(s == null) {
                s = new HashSet<>();
                closure.put(e.getKey(), s);
            }
            s.add(e.getValue());
        }

        LinkedList<Entry<String, String>> todo = new LinkedList<>(injections);

        while(!todo.isEmpty()) {
            Entry<String, String> e = todo.pop();
            String from = e.getKey();
            String to = e.getValue();
            Set<String> transTos = closure.get(to);
            if(transTos != null) {
                Set<String> fromTos = closure.get(from);
                for(String transTo : transTos) {
                    if(fromTos.add(transTo))
                        todo.add(new SimpleEntry<>(from, transTo));
                }
            }
        }

        return Collections.unmodifiableMap(closure);
    }

}
