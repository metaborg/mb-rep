package org.spoofax.interpreter.library.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spoofax.interpreter.terms.IStrategoConstructor;

/**
 * @author Gabriël Konat
 * 
 * Collects and prints statistics about an {@link IIndex}.
 */
public class IndexStatistics {
    private class Data {
        public Collection<IndexPartitionDescriptor> partitions;
        public Collection<IndexEntry> entries;
        public Map<IStrategoConstructor, Integer> entriesPerConstructor;
    }

    private IIndex index;

    public IndexStatistics(IIndex index) {
        this.index = index;
    }

    private Collection<IndexPartitionDescriptor> partitions() {
        return index.getAllPartitionDescriptors();
    }

    private Collection<IndexEntry> elements() {
        List<IndexEntry> entries = new ArrayList<IndexEntry>();
        for(IndexPartitionDescriptor partitionDescriptor : index.getAllPartitionDescriptors()) {
            entries.addAll(index.getInPartition(partitionDescriptor));
        }

        return entries;
    }

    public Data stats() {
        Data data = new Data();
        data.partitions = partitions();
        data.entries = elements();

        // Divide types by constructor
        data.entriesPerConstructor = new HashMap<IStrategoConstructor, Integer>();
        for(IndexEntry entry : data.entries) {
            IndexURI uri = entry.getKey();
            Integer constructorCount = data.entriesPerConstructor.get(uri.getConstructor());
            if(constructorCount == null)
                data.entriesPerConstructor.put(uri.getConstructor(), 1);
            else
                data.entriesPerConstructor.put(uri.getConstructor(), constructorCount + 1);
        }

        return data;
    }

    public String statsString() {
        Data data = stats();

        StringBuilder b = new StringBuilder();
        b.append("* Number of partitions: " + data.partitions.size() + "\n");
        b.append("* Number of elements: " + data.entries.size() + "\n");
        b.append("* Elements per constructor: \n");
        for(Entry<IStrategoConstructor, Integer> pair : data.entriesPerConstructor.entrySet())
            b.append("  - " + pair.getKey() + ": " + pair.getValue() + "\n");

        return b.toString();
    }

    public void printStats() {
        System.out.println(statsString());
    }
}
