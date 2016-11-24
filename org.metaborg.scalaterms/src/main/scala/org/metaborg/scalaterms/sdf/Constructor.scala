package org.metaborg.scalaterms.sdf

import org.metaborg.scalaterms.{ Origin, STerm, TermLike }

/**
  * Represents an SDF Constructor. The implementor can be generated from an SDF specification.
  */
trait Constructor extends TermLike {
  val origin: Origin
  
  /**
    * This method is generated from an SDF specification.
    *
    * @return The Scala ATerm representation for this constructor
    */
  override def toSTerm: STerm
}


