package org.metaborg.regexp;

public final class RegExpBuilder<S> {

    private final IAlphabet<S> alphabet;

    public RegExpBuilder(IAlphabet<S> alphabet) {
        this.alphabet = alphabet;
    }

    public IAlphabet<S> getAlphabet() {
        return alphabet;
    }
    
    public IRegExp<S> emptySet() {
        return EmptySet.of(alphabet);
    }

    public IRegExp<S> emptyString() {
        return EmptyString.of(alphabet);
    }

    public IRegExp<S> symbol(S s) {
        return Symbol.of(s, alphabet);
    }

    public IRegExp<S> concat(final IRegExp<S> left, final IRegExp<S> right) {
        return left.accept(new ARegExpFunction<S,IRegExp<S>>() {

            @Override public IRegExp<S> emptySet() {
                return RegExpBuilder.this.emptySet();
            }

            @Override public IRegExp<S> emptyString() {
                return right;
            }

            @Override public IRegExp<S> concat(IRegExp<S> innerLeft, IRegExp<S> innerRight) {
                return RegExpBuilder.this.concat(innerLeft, RegExpBuilder.this.concat(innerRight, right));
            }

            @Override public IRegExp<S> defaultValue() {
                return right.accept(new ARegExpFunction<S,IRegExp<S>>() {

                    @Override public IRegExp<S> emptySet() {
                        return RegExpBuilder.this.emptySet();
                    }

                    @Override public IRegExp<S> emptyString() {
                        return left;
                    }


                    @Override public IRegExp<S> defaultValue() {
                        return Concat.of(left, right, alphabet);
                    }

                });
            }

        });
    }

    public IRegExp<S> closure(final IRegExp<S> re) {
        return re.accept(new ARegExpFunction<S,IRegExp<S>>() {

            @Override public IRegExp<S> emptySet() {
                return RegExpBuilder.this.emptyString();
            }

            @Override public IRegExp<S> emptyString() {
                return RegExpBuilder.this.emptyString();
            }

            @Override public IRegExp<S> closure(IRegExp<S> innerRe) {
                return Closure.of(innerRe, alphabet);
            }

            @Override public IRegExp<S> defaultValue() {
                return Closure.of(re, alphabet);
            }

        });
    }

    public IRegExp<S> or(final IRegExp<S> left, final IRegExp<S> right) {
        if (left.equals(right)) {
            return left;
        }
        if (compare(left, right) > 0) {
            return RegExpBuilder.this.or(right, left);
        }
        return left.accept(new ARegExpFunction<S,IRegExp<S>>() {

            @Override public IRegExp<S> or(IRegExp<S> innerLeft, IRegExp<S> innerRight) {
                return Or.of(innerLeft, RegExpBuilder.this.or(innerRight, right), alphabet);
            }

            @Override public IRegExp<S> defaultValue() {
                return left.accept(new ARegExpFunction<S,IRegExp<S>>() {

                    @Override public IRegExp<S> emptySet() {
                        return right;
                    }

                    @Override public IRegExp<S> complement(IRegExp<S> re) {
                        final ARegExpFunction<S,IRegExp<S>> outer = this;
                        return re.accept(new ARegExpFunction<S,IRegExp<S>>() {

                            @Override public IRegExp<S> emptySet() {
                                return left;
                            }

                            @Override public IRegExp<S> defaultValue() {
                                return outer.defaultValue();
                            }

                        });
                    }

                    @Override public IRegExp<S> defaultValue() {
                        return Or.of(left, right, alphabet);
                    }
                });

            }

        });
    }

    public IRegExp<S> and(final IRegExp<S> left, final IRegExp<S> right) {
        System.out.println(left + " & " + right);
        if (left.equals(right)) {
            return left;
        }
        if (compare(left, right) > 0) {
            System.out.println("swap");
            return RegExpBuilder.this.and(right, left);
        }
        return left.accept(new ARegExpFunction<S,IRegExp<S>>() {

            @Override public IRegExp<S> and(IRegExp<S> innerLeft, IRegExp<S> innerRight) {
                return And.of(innerLeft, RegExpBuilder.this.and(innerRight, right), alphabet);
            }

            @Override public IRegExp<S> defaultValue() {
                return left.accept(new ARegExpFunction<S,IRegExp<S>>() {

                    @Override public IRegExp<S> emptySet() {
                        return RegExpBuilder.this.emptySet();
                    }

                    @Override public IRegExp<S> complement(IRegExp<S> re) {
                        final ARegExpFunction<S,IRegExp<S>> outer = this;
                        return re.accept(new ARegExpFunction<S,IRegExp<S>>() {

                            @Override public IRegExp<S> emptySet() {
                                return right;
                            }

                            @Override public IRegExp<S> defaultValue() {
                                return outer.defaultValue();
                            }

                        });
                    }

                    @Override public IRegExp<S> defaultValue() {
                        return And.of(left, right, alphabet);
                    }

                });


            }

        });
    }

    public IRegExp<S> complement(final IRegExp<S> re) {
        return re.accept(new ARegExpFunction<S,IRegExp<S>>() {

            @Override public IRegExp<S> complement(IRegExp<S> innerRe) {
                return innerRe;
            }

            @Override public IRegExp<S> defaultValue() {
                return Complement.of(re, alphabet);
            }

        });
    }

    private int compare(final IRegExp<S> re1, final IRegExp<S> re2) {
        assert re1.getAlphabet().equals(alphabet);
        assert re2.getAlphabet().equals(alphabet);
        return re1.accept(new IRegExpFunction<S,Integer>() {

            @Override public Integer emptySet() {
                return re1.accept(order) - re2.accept(order);
            }

            @Override public Integer emptyString() {
                return re1.accept(order) - re2.accept(order);
            }

            @Override public Integer symbol(final S s1) {
                return re2.accept(new ARegExpFunction<S,Integer>() {

                    @Override public Integer symbol(final S s2) {
                        return alphabet.indexOf(s1) - alphabet.indexOf(s2);
                    }

                    @Override public Integer defaultValue() {
                        return re1.accept(order) - re2.accept(order);
                    }

                });
            }

            @Override public Integer concat(final IRegExp<S> left1, final IRegExp<S> right1) {
                return re2.accept(new ARegExpFunction<S,Integer>() {

                    @Override public Integer concat(final IRegExp<S> left2, final IRegExp<S> right2) {
                        int c = compare(left1, left2);
                        if (c == 0) {
                            c = compare(right1, right2);
                        }
                        return c;
                    }

                    @Override public Integer defaultValue() {
                        return re1.accept(order) - re2.accept(order);
                    }

                });
            }

            @Override public Integer closure(final IRegExp<S> innerRe1) {
                return re2.accept(new ARegExpFunction<S,Integer>() {

                    @Override public Integer closure(final IRegExp<S> innerRe2) {
                        return compare(innerRe1, innerRe2);
                    }

                    @Override public Integer defaultValue() {
                        return re1.accept(order) - re2.accept(order);
                    }

                });
            }

            @Override public Integer or(final IRegExp<S> left1, final IRegExp<S> right1) {
                return re2.accept(new ARegExpFunction<S,Integer>() {

                    @Override public Integer or(final IRegExp<S> left2, final IRegExp<S> right2) {
                        int c = compare(left1, left2);
                        if (c == 0) {
                            c = compare(right1, right2);
                        }
                        return c;
                    }

                    @Override public Integer defaultValue() {
                        return re1.accept(order) - re2.accept(order);
                    }

                });
            }

            @Override public Integer and(final IRegExp<S> left1, final IRegExp<S> right1) {
                return re2.accept(new ARegExpFunction<S,Integer>() {

                    @Override public Integer and(final IRegExp<S> left2, final IRegExp<S> right2) {
                        int c = compare(left1, left2);
                        if (c == 0) {
                            c = compare(right1, right2);
                        }
                        return c;
                    }

                    @Override public Integer defaultValue() {
                        return re1.accept(order) - re2.accept(order);
                    }

                });
            }

            @Override public Integer complement(final IRegExp<S> innerRe1) {
                return re2.accept(new ARegExpFunction<S,Integer>() {

                    @Override public Integer complement(final IRegExp<S> innerRe2) {
                        return compare(innerRe1, innerRe2);
                    }

                    @Override public Integer defaultValue() {
                        return re1.accept(order) - re2.accept(order);
                    }

                });
            }

        });
    }

    private final IRegExpFunction<S,Integer> order = new IRegExpFunction<S,Integer>() {

        @Override public Integer emptySet() {
            return 1;
        }

        @Override public Integer emptyString() {
            return 2;
        }

        @Override public Integer complement(IRegExp<S> re) {
            return 3;
        }

        @Override public Integer closure(IRegExp<S> re) {
            return 4;
        }

        @Override public Integer concat(IRegExp<S> left, IRegExp<S> right) {
            return 5;
        }

        @Override public Integer symbol(S s) {
            return 7 + alphabet.indexOf(s);
        }

        @Override public Integer or(IRegExp<S> left, IRegExp<S> right) {
            return Math.min(left.accept(this), right.accept(this));
        }

        @Override public Integer and(IRegExp<S> left, IRegExp<S> right) {
            return Math.min(left.accept(this), right.accept(this));
        }

    };

}