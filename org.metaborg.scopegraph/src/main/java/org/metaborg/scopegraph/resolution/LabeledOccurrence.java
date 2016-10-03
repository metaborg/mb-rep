package org.metaborg.scopegraph.resolution;

import org.metaborg.scopegraph.ILabel;
import org.metaborg.scopegraph.ILabeledOccurrence;
import org.metaborg.scopegraph.IOccurrence;


public class LabeledOccurrence implements ILabeledOccurrence {

    private static final long serialVersionUID = -3415353320298889093L;

    private final ILabel label;
    private final IOccurrence occurrence;

    public LabeledOccurrence(ILabel label, IOccurrence occurrence) {
        this.label = label;
        this.occurrence = occurrence;
    }

    @Override public ILabel label() {
        return label;
    }

    @Override public IOccurrence occurrence() {
        return occurrence;
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + label.hashCode();
        result = prime * result + occurrence.hashCode();
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LabeledOccurrence other = (LabeledOccurrence) obj;
        if (!label.equals(other.label))
            return false;
        if (!occurrence.equals(other.occurrence))
            return false;
        return true;
    }

    @Override public String toString() {
        return label + "/" + occurrence;
    }

}
