package org.metaborg.unit;

public interface IUnit {
    <T> IUnitContrib<T> contrib(String id);

    Iterable<IUnitContrib<?>> contribs();


    <T> IUnit modify(IUnitContrib<T> contrib);

    IUnit modify(IUnitContrib<?>... contribs);
}
