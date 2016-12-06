package org.metaborg.scalaterms.spoofax

import org.metaborg.scalaterms._

/**
  * The Scala representation of the resolution result, as returned by `editor-resolve`
  */
case class ResolutionResult(override val origin: Origin) extends TermLike with HasOrigin {
  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = STerm.String("Dummy string for supplying the origin", Some(origin))
}

object ResolutionResult extends TermLikeCompanion[ResolutionResult] {
  override val fromSTerm = new FromSTerm[ResolutionResult] {
    /**
      * The extraction from STerm
      *
      * @param term the STerm
      * @return the Some(T) that's extracted if matched
      */
    override def unapply(term: STerm): Option[ResolutionResult] = term match {
      case STerm.String(_, Some(origin)) => Some(ResolutionResult(origin))
      case _ => None
    }
  }
}
