package org.spoofax;

public class DummyStrategoTermWithHashCode extends DummyStrategoTerm {
    private final int hashCode;

    public DummyStrategoTermWithHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
