package org.metaborg.scalaInterop.terms.sdf

import org.metaborg.scalaInterop.terms.stratego.{TermLike, Term => STerm}

/**
  * Represents an SDF Lexical Sort. The implementor can be generated from an SDF specification.
  */
trait Lexical extends TermLike {
  /**
    * This method is generated from an SDF specification.
    *
    * @return The scalaInterop.stratego ATerm representation for this constructor
    */
  override def toSTerm: STerm.String
}
