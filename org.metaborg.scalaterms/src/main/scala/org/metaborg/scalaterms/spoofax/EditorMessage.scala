package org.metaborg.scalaterms.spoofax

import org.metaborg.scalaterms._

/**
  * The Scala representation of an editor message as returned (as part of a larger term) by the editor-analyze strategy
  */
case class EditorMessage(message: String, origin: Origin) extends TermLike {
  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = {
    STerm.Tuple(List(STerm.String("origin dummy", origin), STerm.String(message, origin)), origin)
  }
}

object EditorMessage extends TermLikeCompanion[EditorMessage] {
  override val fromSTerm = new FromSTerm[EditorMessage] {
    /**
      * The extraction from STerm
      *
      * @param term the STerm
      * @return the Some(T) that's extracted if matched
      */
    override def unapply(term: STerm): Option[EditorMessage] = term match {
      case STerm.Tuple(List(STerm.String(_, _), STerm.String(message, _)), origin) =>
        Some(EditorMessage(message, origin))
      case _ => None
    }
  }
}
