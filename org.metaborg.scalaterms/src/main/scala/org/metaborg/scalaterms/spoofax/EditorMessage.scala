package org.metaborg.scalaterms.spoofax

import org.metaborg.scalaterms.{ Extract, Origin, STerm, TermLike }

/**
  * Editor message expected to be returned by editor-* strategy implementations
  * Well.. the Scala ATerm representation of it anyway
  */
case class EditorMessage(message: String, origin: Origin) extends TermLike {
  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = {
    STerm.Tuple(List(STerm.String("origin dummy", origin), STerm.String(message, origin)), origin)
  }
}

object ExtractEditorMessage extends Extract[EditorMessage] {
  def unapply(term: STerm): Option[EditorMessage] = term match {
    case STerm.Tuple(List(STerm.String(_, _), STerm.String(message, _)), origin) =>
      Some(EditorMessage(message, origin))
    case _                                                                       => None
  }
}
