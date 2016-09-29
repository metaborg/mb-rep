package org.metaborg.unification.terms;

public final class TermVar implements IAny {

    private static final long serialVersionUID = 6751667565412022313L;

    private final String resource;
    private final String name;
    private final int hashCode;

    public TermVar(String name) {
        this(null, name);
    }

    public TermVar(String resource, String name) {
        this.resource = resource;
        this.name = name;
        this.hashCode = calcHashCode();
    }

    public String getResource() {
        return resource;
    }

    public String getName() {
        return name;
    }

    @Override public <T> T accept(ITermVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override public int hashCode() {
        return hashCode;
    }

    private int calcHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (resource == null ? 0 : name.hashCode());
        result = prime * result + name.hashCode();
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TermVar other = (TermVar) obj;
        if (resource == null) {
            if (other.resource != null)
                return false;
        } else if (!resource.equals(other.resource))
            return false;
        if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder("?");
        if (resource != null) {
            sb.append(resource);
            sb.append(".");
        }
        sb.append(name);
        return sb.toString();
    }

}
