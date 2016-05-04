package org.spoofax.terms.typesmart;

import java.io.IOException;
import java.io.ObjectInputStream;
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

import org.apache.commons.vfs2.FileNotFoundException;
import org.apache.commons.vfs2.FileObject;
import org.metaborg.util.log.ILogger;
import org.spoofax.terms.typesmart.types.SortType;

public class TypesmartContext implements Serializable {
    private static final long serialVersionUID = -2343547085277594696L;

    private final Map<String, Set<List<SortType>>> constructorSignatures;
    private final Set<SortType> lexicals;
    private final Set<Entry<SortType, SortType>> injections;
    private transient Map<SortType, Set<SortType>> injectionsClosure;
    private transient Map<SortType, Set<SortType>> reverseInjectionsClosure;

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

    /**
     * @return An empty context.
     */
    public static TypesmartContext empty() {
        return new TypesmartContext(Collections.<String, Set<List<SortType>>>emptyMap(),
            Collections.<SortType>emptySet(), Collections.<Entry<SortType, SortType>>emptySet());
    }

    public static TypesmartContext load(FileObject file, ILogger logger) {
        try(ObjectInputStream ois = new ObjectInputStream(file.getContent().getInputStream())) {
            return (TypesmartContext) ois.readObject();
        } catch(FileNotFoundException e) {
            logger.warn("Typesmart context file " + file + " not found");
            return TypesmartContext.empty();
        } catch(IOException | ClassNotFoundException e) {
            logger.error("Error while loading typesmart term factory", e);
            throw new RuntimeException(e);
        }
    }

    public boolean isEmpty() {
        return constructorSignatures.isEmpty() && lexicals.isEmpty() && injections.isEmpty();
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
            computeClosure();
        return injectionsClosure;
    }

    public Map<SortType, Set<SortType>> getReverseInjectionsClosure() {
        if(reverseInjectionsClosure == null)
            computeClosure();
        return reverseInjectionsClosure;
    }

    private void computeClosure() {
        injectionsClosure = new HashMap<>();
        reverseInjectionsClosure = new HashMap<>();
        for(Entry<SortType, SortType> e : injections) {
            addToMap(e.getKey(), e.getValue(), injectionsClosure);
            addToMap(e.getValue(), e.getKey(), reverseInjectionsClosure);
        }

        LinkedList<Entry<SortType, SortType>> todo = new LinkedList<>(injections);

        while(!todo.isEmpty()) {
            Entry<SortType, SortType> e = todo.pop();
            SortType from = e.getKey();
            SortType to = e.getValue();
            Set<SortType> transTos = injectionsClosure.get(to);
            if(transTos != null) {
                Set<SortType> fromTos = injectionsClosure.get(from);
                for(SortType transTo : transTos) {
                    if(fromTos.add(transTo)) {
                        reverseInjectionsClosure.get(transTo).add(from);
                        todo.add(new SimpleEntry<>(from, transTo));
                    }
                }
            }
        }

        injectionsClosure = Collections.unmodifiableMap(injectionsClosure);
        reverseInjectionsClosure = Collections.unmodifiableMap(reverseInjectionsClosure);
    }

    private static <K, V> void addToMap(K key, V value, Map<K, Set<V>> map) {
        Set<V> vals = map.get(key);
        if(vals == null) {
            vals = new HashSet<>();
            map.put(key, vals);
        }
        vals.add(value);
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

    public TypesmartContext merge(TypesmartContext c) {
        Set<SortType> lexicals = new HashSet<>(this.lexicals);
        lexicals.addAll(c.lexicals);
        lexicals = Collections.unmodifiableSet(lexicals);

        Set<Entry<SortType, SortType>> injections = new HashSet<>(this.injections);
        injections.addAll(c.injections);
        injections = Collections.unmodifiableSet(injections);

        Map<String, Set<List<SortType>>> constructorSignatures = new HashMap<>(this.constructorSignatures);
        for(Entry<String, Set<List<SortType>>> e : c.constructorSignatures.entrySet()) {
            String cname = e.getKey();
            Set<List<SortType>> set = this.constructorSignatures.get(cname);
            if(set == null) {
                constructorSignatures.put(cname, e.getValue());
            } else {
                Set<List<SortType>> newSet = new HashSet<>(set);
                newSet.addAll(e.getValue());
                constructorSignatures.put(cname, Collections.unmodifiableSet(newSet));
            }
        }
        constructorSignatures = Collections.unmodifiableMap(constructorSignatures);

        return new TypesmartContext(constructorSignatures, lexicals, injections);
    }

    public boolean isInjection(SortType t1, SortType t2) {
        Set<SortType> injectedInto = getInjectionsClosure().get(t1);
        return injectedInto != null && injectedInto.contains(t2);
    }
}
