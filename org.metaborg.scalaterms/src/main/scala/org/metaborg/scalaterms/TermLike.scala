package org.metaborg.scalaterms

import org.strategoxt.lang.Context
import org.spoofax.interpreter.terms.IStrategoTerm

/**
  * Something that has an STerm representation
  */
trait TermLike {
  val origin: Origin

  /**
    * @return equivalent Scala ATerm representation
    */
  def toSTerm: STerm

  /**
    * @return equivalent Java ATerm representation
    */
  def toStratego(implicit context: Context): IStrategoTerm = {
    this.toSTerm.toStratego(context)
  }
}
