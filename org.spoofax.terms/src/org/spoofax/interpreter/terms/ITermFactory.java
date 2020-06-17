/*
 * Created on 30. aug.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.interpreter.terms;

import org.spoofax.terms.ParseError;
import org.spoofax.terms.io.TAFTermReader;

import javax.annotation.Nullable;

/**
 * Term factory.
 */
public interface ITermFactory extends IStrategoTermBuilder {

    /**
     * Builds a constructor application term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an array of subterms.
     *
     * @param constructorName the constructor name
     * @param subterms the subterms of the application
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the term; or {@code null} or an empty list to use no annotations
     * @return the built term
     */
    IStrategoAppl buildAppl(String constructorName, IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);

    /**
     * Builds a constructor application term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an iterable of subterms.
     *
     * @param constructorName the constructor name
     * @param subterms the subterms of the application
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the term; or {@code null} or an empty list to use no annotations
     * @return the built term
     */
    IStrategoAppl buildAppl(String constructorName, Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);

    /**
     * Builds a constructor application term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an array of subterms.
     *
     * @param constructor the constructor
     * @param subterms the subterms of the application
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the term; or {@code null} or an empty list to use no annotations
     * @return the built term
     */
    IStrategoAppl buildAppl(IStrategoConstructor constructor, IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);

    /**
     * Builds a constructor application term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an iterable of subterms.
     *
     * @param constructor the constructor
     * @param subterms the subterms of the application
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the term; or {@code null} or an empty list to use no annotations
     * @return the built term
     */
    IStrategoAppl buildAppl(IStrategoConstructor constructor, Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);


    /**
     * Builds an empty list term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes no subterms. This allows implementations to optimize empty lists.
     * The actual implementation of the returned list
     * may differ from those returned by the other {@link #buildList} overloads.
     *
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the term; or {@code null} or an empty list to use no annotations
     * @return the built term
     */
    IStrategoList buildEmptyList(@Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);

    /**
     * Builds a list term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an array of subterms.
     *
     * @param subterms the subterms of the list
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the term; or {@code null} or an empty list to use no annotations
     * @return the built term
     */
    IStrategoList buildList(IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);

    /**
     * Builds a list term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an iterable of subterms.
     *
     * @param subterms the subterms of the list
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the term; or {@code null} or an empty list to use no annotations
     * @return the built term
     */
    IStrategoList buildList(Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);

    /**
     * Builds a list term from a head and tail.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes a head element and a tail list. The actual implementation of the returned list
     * may differ from those returned by the other {@link #buildList} overloads.
     *
     * @param head the head of the list
     * @param tail the tail of the list
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the outer Cons term; or {@code null} or an empty list to use no annotations
     * @return the built term
     */
    IStrategoList buildListConsNil(IStrategoTerm head, IStrategoList tail, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);


    /**
     * Builds a tuple term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an array of subterms.
     *
     * @param subterms the subterms of the tuple
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the term; or {@code null} or an empty list to use no annotations
     * @return the built term
     */
    IStrategoTuple buildTuple(IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);

    /**
     * Builds a tuple term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an iterable of subterms.
     *
     * @param subterms the subterms of the tuple
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the term; or {@code null} or an empty list to use no annotations
     * @return the built term
     */
    IStrategoTuple buildTuple(Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);


    /**
     * Builds a placeholder term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * @param template the placeholder template
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the term; or {@code null} or an empty list to use no annotations
     * @return the built term
     */
    IStrategoPlaceholder buildPlaceholder(IStrategoTerm template, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);


    /**
     * Builds an integer term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * @param value the integer value
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the term; or {@code null} or an empty list to use no annotations
     * @return the built term
     */
    IStrategoInt buildInt(int value, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);


    /**
     * Builds a real term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * @param value the real value
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the term; or {@code null} or an empty list to use no annotations
     * @return the built term
     */
    IStrategoReal buildReal(double value, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);


    /**
     * Builds a string term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * @param value the string value
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the term; or {@code null} or an empty list to use no annotations
     * @return the built term
     */
    IStrategoString buildString(String value, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);



    // Convenience overloads:
    /**
     * Builds a constructor application term with no annotations.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an array of subterms.
     *
     * @param constructorName the constructor name
     * @param subterms the subterms of the application
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the built term
     */
    default IStrategoAppl buildAppl(String constructorName, IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee) {
        return buildAppl(constructorName, subterms, replacee, null);
    }

    /**
     * Builds a constructor application term with no annotations.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an iterable of subterms.
     *
     * @param constructorName the constructor name
     * @param subterms the subterms of the application
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the built term
     */
    default IStrategoAppl buildAppl(String constructorName, Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee) {
        return buildAppl(constructorName, subterms, replacee, null);
    }

    /**
     * Builds a constructor application term with no annotations.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an array of subterms.
     *
     * @param constructor the constructor
     * @param subterms the subterms of the application
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the built term
     */
    default IStrategoAppl buildAppl(IStrategoConstructor constructor, IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee) {
        return buildAppl(constructor, subterms, replacee, null);
    }

    /**
     * Builds a constructor application term with no annotations.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an iterable of subterms.
     *
     * @param constructor the constructor
     * @param subterms the subterms of the application
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the built term
     */
    default IStrategoAppl buildAppl(IStrategoConstructor constructor, Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee) {
        return buildAppl(constructor, subterms, replacee, null);
    }


    /**
     * Builds an empty list term with no annotations.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes no subterms. This allows implementations to optimize empty lists.
     * The actual implementation of the returned list
     * may differ from those returned by the other {@link #buildList} overloads.
     *
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the built term
     */
    default IStrategoList buildEmptyList(@Nullable IStrategoTerm replacee) {
        return buildEmptyList(replacee, null);
    }

    /**
     * Builds a list term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an array of subterms.
     *
     * @param subterms the subterms of the list
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the built term
     */
    default IStrategoList buildList(IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee) {
        return buildList(subterms, replacee, null);
    }

    /**
     * Builds a list term with no annotations.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an iterable of subterms.
     *
     * @param subterms the subterms of the list
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the built term
     */
    default IStrategoList buildList(Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee) {
        return buildList(subterms, replacee, null);
    }

    /**
     * Builds a list term from a head and tail with no annotations.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes a head element and a tail list. The actual implementation of the returned list
     * may differ from those returned by the other {@link #buildList} overloads.
     *
     * @param head the head of the list
     * @param tail the tail of the list
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the built term
     */
    default IStrategoList buildListConsNil(IStrategoTerm head, IStrategoList tail, @Nullable IStrategoTerm replacee) {
        return buildListConsNil(head, tail, replacee, null);
    }


    /**
     * Builds a tuple term with no annotations.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an array of subterms.
     *
     * @param subterms the subterms of the tuple
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the built term
     */
    default IStrategoTuple buildTuple(IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee) {
        return buildTuple(subterms, replacee, null);
    }

    /**
     * Builds a tuple term with no annotations.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * This overload takes an iterable of subterms.
     *
     * @param subterms the subterms of the tuple
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the built term
     */
    default IStrategoTuple buildTuple(Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee) {
        return buildTuple(subterms, replacee, null);
    }


    /**
     * Builds a placeholder term with no annotations.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * @param template the placeholder template
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the built term
     */
    default IStrategoPlaceholder buildPlaceholder(IStrategoTerm template, @Nullable IStrategoTerm replacee) {
        return buildPlaceholder(template, replacee, null);
    }


    /**
     * Builds an integer term with no annotations.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * @param value the integer value
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the built term
     */
    default IStrategoInt buildInt(int value, @Nullable IStrategoTerm replacee) {
        return buildInt(value, replacee, null);
    }


    /**
     * Builds a real term with no annotations.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * @param value the real value
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the built term
     */
    default IStrategoReal buildReal(double value, @Nullable IStrategoTerm replacee) {
        return buildReal(value, replacee, null);
    }


    /**
     * Builds a string term with no annotations.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * @param value the string value
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the built term
     */
    default IStrategoString buildString(String value, @Nullable IStrategoTerm replacee) {
        return buildString(value, replacee, null);
    }



    /**
     * Creates a list builder.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * Finalize the build by calling {@link IStrategoList.Builder#build()}.
     *
     * @param initialCapacity the initial capacity of the builder
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @param annotations the annotations of the term; or {@code null} or an empty list to use no annotations
     * @return the list builder
     */
    IStrategoList.Builder createListBuilder(int initialCapacity, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations);

    /**
     * Builds a list using a list builder with no annotations.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * Finalize the build by calling {@link IStrategoList.Builder#build()}.
     *
     * @param initialCapacity the initial capacity of the builder
     * @param replacee the term being replaced; or {@code null} when no term is replaced
     * @return the list builder
     */
    default IStrategoList.Builder createListBuilder(int initialCapacity, @Nullable IStrategoTerm replacee) {
        return createListBuilder(initialCapacity, replacee, null);
    }



    /**
     * Parses a term from a string.
     * 
     * @see TAFTermReader#parseFromString(String)
     * @see TAFTermReader#parseFromStream(java.io.InputStream)
     * @see TAFTermReader#parseFromFile(String)
     *
     * @deprecated Use {@link TAFTermReader#parseFromString(String)} instead.
     */
    @Deprecated
    IStrategoTerm parseFromString(String text) throws ParseError;

    /** @deprecated Use {@link #buildAppl} with a replacee instead. */
    @Deprecated
    IStrategoAppl replaceAppl(IStrategoConstructor constructor, IStrategoTerm[] kids, IStrategoAppl old);

    /** @deprecated Use {@link #buildList} with a replacee instead. */
    @Deprecated
    IStrategoList replaceList(IStrategoTerm[] kids, IStrategoList old);

    /** @deprecated Use {@link #buildListConsNil} with a replacee instead. */
    @Deprecated
    IStrategoList replaceListCons(IStrategoTerm head, IStrategoList tail, IStrategoTerm oldHead, IStrategoList oldTail);

    /**
     * Replaces an existing term with another term.
     *
     * Implementations can use the {@code replacee} for origin tracking and copying attachments.
     *
     * @param term the new term
     * @param replacee the term being replaced
     * @return the new term
     */
    @Deprecated
    IStrategoTerm replaceTerm(IStrategoTerm term, IStrategoTerm replacee);

    /** @deprecated Use {@link #buildTuple} with a replacee instead. */
    @Deprecated
    IStrategoTuple replaceTuple(IStrategoTerm[] kids, IStrategoTuple old);

    /**
     * Returns a new term with the specified annotations.
     *
     * Any existing annotations are not propagated.
     *
     * @param term the term to annotate
     * @param annotations the new annotations to use; or {@code null} or an empty list to use no annotations
     * @return the new annotated term
     */
    IStrategoTerm withAnnotations(IStrategoTerm term, @Nullable IStrategoList annotations);
}
