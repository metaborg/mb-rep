package org.metaborg.scalaInterop.terms.spoofax

import org.metaborg.scalaInterop.terms.generic.{ Extract, ExtractList }
import org.metaborg.scalaInterop.terms.stratego.{ Origin, TermLike, Term => STerm }

/**
  * Created by jeff on 23/11/16.
  */
case class AnalysisResult(ast: STerm,
                          errors: List[EditorMessage],
                          warnings: List[EditorMessage],
                          notes: List[EditorMessage],
                          origin: Origin) extends TermLike {
  /**
    * @return equivalent scalaInterop.stratego ATerm representation
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
                          ExtractEditorMessageList(errors),
                          ExtractEditorMessageList(warnings),
                          ExtractEditorMessageList(notes)), origin) => Some(AnalysisResult(ast,
                                                                                           errors,
                                                                                           warnings,
                                                                                           notes,
                                                                                           origin))
  }
}

object ExtractAnalysisResultList extends ExtractList[AnalysisResult] {
  override val Extract: Extract[AnalysisResult] = ExtractAnalysisResult
}
