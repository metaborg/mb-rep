package org.metaborg.scalaterms.spoofax

import org.metaborg.scalaterms.{ Extract, Origin, STerm, TermLike }

/**
  * Created by jeff on 23/11/16.
  */
case class AnalysisResult(ast: STerm,
                          errors: List[EditorMessage],
                          warnings: List[EditorMessage],
                          notes: List[EditorMessage],
                          origin: Origin) extends TermLike {
  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = {
    STerm.Tuple(List(ast,
                     STerm.List(errors.map(_.toSTerm), origin),
                     STerm.List(warnings.map(_.toSTerm), origin),
                     STerm.List(notes.map(_.toSTerm), origin)), origin)
  }
}

object ExtractAnalysisResult extends Extract[AnalysisResult] {
  def unapply(term: STerm): Option[AnalysisResult] = term match {
    case STerm.Tuple(List(ast,
                          ExtractEditorMessage.list(errors),
                          ExtractEditorMessage.list(warnings),
                          ExtractEditorMessage.list(notes)), origin) => Some(AnalysisResult(ast,
                                                                                            errors,
                                                                                            warnings,
                                                                                            notes,
                                                                                            origin))
  }
}
