package org.metaborg.regexp;


public class Normalizer<S> implements IRegExpVisitor<S,IRegExp<S>> {

    @Override public IRegExp<S> symbol(S symbol) {
        return Symbol.of(symbol);
    }

    @Override public IRegExp<S> or(IRegExp<S> left, IRegExp<S> right) {
        if (left.equals(right)) {
            return left.accept(this);
            // } else if (re1 > re2) { return or(re2, re1)
        } else if (left instanceof Or) {
            Or<S> o = (Or<S>) left;
            return or(o.getLeft(), or(o.getRight(), right));
        } else if (left instanceof Complement && ((Complement<S>) left).re() instanceof EmptySet) {
            return complement(emptySet());
        } else if (left instanceof EmptySet) {
            return right.accept(this);
        } else {
            return Or.of(left.accept(this), right.accept(this));
        }
    }

    @Override public IRegExp<S> and(IRegExp<S> left, IRegExp<S> right) {
        if (left.equals(right)) {
            return left.accept(this);
            // } else if (re1 > re2) { return and(re2, re1)
        } else if (left instanceof And) {
            And<S> a = (And<S>) left;
            return and(a.getLeft(), and(a.getRight(), right));
        } else if (left instanceof EmptySet) {
            return emptySet();
        } else if (left instanceof Complement && ((Complement<S>) left).re() instanceof EmptySet) {
            return right.accept(this);
        } else {
            return And.of(left.accept(this), right.accept(this));
        }
    }

    @Override public IRegExp<S> concat(IRegExp<S> left, IRegExp<S> right) {
        if(left instanceof Concat) {
            Concat<S> c = (Concat<S>) left;
            return concat(c.getLeft(), concat(c.getRight(), right));
        } else if (left instanceof EmptySet) {
            return emptySet();
        } else if (right instanceof EmptySet) {
            return emptySet();
        } else if (left instanceof EmptyString) {
            return right.accept(this);
        } else if (right instanceof EmptyString) {
            return left.accept(this);
        } else {
            return Concat.of(left.accept(this), right.accept(this));
        }
    }

    @Override public IRegExp<S> complement(IRegExp<S> re) {
        if (re instanceof Complement) {
            Complement<S> c = (Complement<S>) re;
            return c.re().accept(this);
        } else {
            return Complement.of(re.accept(this));
        }
    }

    @Override public IRegExp<S> closure(IRegExp<S> re) {
        if(re instanceof Closure) {
            Closure<S> c = (Closure<S>) re;
            return closure(c.re());
        } else if (re instanceof EmptyString) {
            return emptyString();
        } else if (re instanceof EmptySet) {
            return emptyString();
        } else {
            return Closure.of(re.accept(this));
        }
    }

    @Override public IRegExp<S> emptyString() {
        return new EmptyString<>();
    }

    @Override public IRegExp<S> emptySet() {
        return new EmptySet<>();
    }

}
