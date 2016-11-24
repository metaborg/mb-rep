package org.spoofax.scalaTerms.sdf

import org.spoofax.scalaTerms.{ STerm, TermLike }

/**
  * Represents an SDF Lexical Sort. The implementor can be generated from an SDF specification.
  */
trait Lexical extends TermLike {
  /**
    * This method is generated from an SDF specification.
    *
    * @return The Scala ATerm representation for this constructor
    */
  override def toSTerm: STerm.String
}
