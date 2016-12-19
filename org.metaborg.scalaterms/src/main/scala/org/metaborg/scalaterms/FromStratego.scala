package org.metaborg.scalaterms

import org.spoofax.interpreter.terms.IStrategoTerm

/**
  * A pattern to match a Java ATerm against. Can also match lists and options of these.
  *
  * @tparam T the type to extract from the STerm
  */
trait FromStratego[T <: TermLike] { self =>
  val fromSTerm: FromSTerm[T]

  lazy val list = new FromStratego[STerm.List[T]] {
    override val fromSTerm: FromSTerm[STerm.List[T]] = self.fromSTerm.list
  }

  lazy val option = new FromStratego[sdf.Option[T]] {
    override val fromSTerm: FromSTerm[sdf.Option[T]] = self.fromSTerm.option
  }

  /**
    * The extraction from Java ATerm within a pattern match
    * @param term the ATerm
    * @return the Some(T) that's extracted if matched
    */
  def unapply(term: IStrategoTerm): Option[T] = fromSTerm.unapply(STerm.fromStratego(term))

  /**
    * The same extraction from Java ATerm outside of a pattern match
    * @param term the ATerm
    * @return the Some(T) that's extracted if matched
    */
  def apply(term: IStrategoTerm): Option[T] = unapply(term)
}
