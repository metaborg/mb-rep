package org.metaborg.scalaInterop.terms.spoofax

import org.metaborg.scalaInterop.terms.generic.{ Extract, ExtractList }
import org.metaborg.scalaInterop.terms.stratego.{ Origin, TermLike, Term => STerm }

/**
  * Editor message expected to be returned by editor-* strategy implementations
  * Well.. the scalaInterop.stratego ATerm representation of it anyway
  */
case class EditorMessage(message: String, origin: Origin) extends TermLike {
  /**
    * @return equivalent scalaInterop.stratego ATerm representation
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

object ExtractEditorMessageList extends ExtractList[EditorMessage] {
  override val Extract: Extract[EditorMessage] = ExtractEditorMessage
}
