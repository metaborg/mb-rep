package org.metaborg.scalaterms.spoofax

import org.metaborg.scalaterms._

/**
  * The Scala representation of the resolution result, as returned by `editor-resolve`
  */
case class ResolutionResult(origin: Origin) extends TermLike {
  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = STerm.String("ResolutionResult dummy string", origin)
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
      case STerm.String(_, origin) => Some(ResolutionResult(origin))
    }
  }
}
