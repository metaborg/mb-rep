package org.metaborg.scalaInterop.terms.sdf

import org.metaborg.scalaInterop.terms.stratego.{Origin, TermLike, Term => STerm}

/**
  * Represents an SDF Constructor. The implementor can be generated from an SDF specification.
  */
trait Constructor extends TermLike {
  val origin: Origin

  /**
    * This method is generated from an SDF specification.
    *
    * @return The scalaInterop.stratego ATerm representation for this constructor
    */
  override def toSTerm: STerm
}


