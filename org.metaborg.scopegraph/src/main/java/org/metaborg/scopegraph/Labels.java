package org.metaborg.scopegraph;

import java.util.Comparator;
import java.util.Iterator;

import org.metaborg.scopegraph.impl.DefaultLabel;

import com.google.common.collect.Multimap;

public class Labels {

    public static final Label D = new Label() {
        public String toString() { return "D"; };
    };
 
    public static final Label R = new Label() {
        public String toString() { return "R"; };
    };
 
    public static Label label(String value) {
        return new DefaultLabel(value);
    }
    
    public static Comparator<Label> order(final Multimap<Label,Label> lt) {
        return new Comparator<Label>() {
            @Override
            public int compare(Label o1, Label o2) {
                if ( lt.containsEntry(o1, o2) ) {
                    return -1;
                } else if ( lt.containsEntry(o2, o1) ) {
                    return 1;
                }
                return 0;
            }
        };
    }
    
    public static int compare(Iterable<Label> l1, Iterable<Label> l2, Comparator<Label> order) {
        Iterator<Label> i1 = l1.iterator();
        Iterator<Label> i2 = l2.iterator();
        while ( i1.hasNext() && i2.hasNext() ) {
            int c = order.compare(i1.next(), i2.next());
            if ( c != 0 ) {
                return c;
            }
        }
        return 0;
    }
    
}