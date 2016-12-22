package org.metaborg.scalaterms.spoofax

import org.metaborg.scalaterms._

/**
  * The Scala representation of the hover result message, as returned by `editor-hover`
  */
case class HoverResult(message: String) extends TermLike {
  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = STerm.String(message)
}

object HoverResult extends TermLikeCompanion[HoverResult] {
  override val fromSTerm = new FromSTerm[HoverResult] {
    /**
      * The extraction from STerm within a pattern match
      *
      * @param term the STerm
      * @return the Some(T) that's extracted if matched
      */
    override def unapply(term: STerm): Option[HoverResult] = term match {
      case STerm.String(message, _) => Some(HoverResult(message))
      case _ => None
    }
  }
}
