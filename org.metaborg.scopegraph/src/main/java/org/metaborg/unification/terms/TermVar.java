package org.metaborg.unification.terms;

public final class TermVar implements ITerm {
 
    private final String name;
    private final int hashCode;
    
    public TermVar(String name) {
        this.name = name;
        this.hashCode = calcHashCode();
    }

    public String getName() {
        return name;
    }

    @Override
    public <T> T accept(ITermVisitor<T> visitor) {
        return visitor.visit(this);
    }

    private int calcHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        TermVar other = (TermVar) obj;
        if(!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "?" + name;
    }

}
