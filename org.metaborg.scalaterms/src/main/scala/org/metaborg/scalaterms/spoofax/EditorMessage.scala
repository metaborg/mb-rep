package org.metaborg.scalaterms.spoofax

import org.metaborg.scalaterms._

/**
  * The Scala representation of an editor message as returned (as part of a larger term) by the editor-analyze strategy
  */
case class EditorMessage(message: String, override val origin: Origin) extends TermLike with HasOrigin {
  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = {
    STerm.Tuple(Seq(STerm.String("Dummy string for supplying the origin", Some(origin)), STerm.String(message)))
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
      case STerm.Tuple(Seq(STerm.String(_, _), STerm.String(message, _)), Some(origin)) =>
        Some(EditorMessage(message, origin))
      case _ => None
    }
  }

  def from(message: Any, origin: Origin): EditorMessage = {
    val msg = message.toString.replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;")
    EditorMessage(msg, origin)
  }
}
