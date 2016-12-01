package org.metaborg.scalaterms.spoofax

import org.metaborg.scalaterms._
import org.metaborg.scalaterms.implicits._

/**
  * The Scala representation of the analysis result, as returned by `editor-analyze`
  */
case class AnalysisResult(ast: STerm,
                          errors: List[EditorMessage],
                          warnings: List[EditorMessage],
                          notes: List[EditorMessage]) extends TermLike {
  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = {
    STerm.Tuple(List(ast,
                     STerm.List(errors.map(_.toSTerm)),
                     STerm.List(warnings.map(_.toSTerm)),
                     STerm.List(notes.map(_.toSTerm))))
  }
}

object AnalysisResult extends TermLikeCompanion[AnalysisResult] {
  override val fromSTerm = new FromSTerm[AnalysisResult] {
    /**
      * The extraction from STerm
      *
      * @param term the STerm
      * @return the Some(T) that's extracted if matched
      */
    override def unapply(term: STerm): Option[AnalysisResult] = term match {
      case STerm.Tuple(List(ast,
                            EditorMessage.fromSTerm.list(errors),
                            EditorMessage.fromSTerm.list(warnings),
                            EditorMessage.fromSTerm.list(notes)), _) => Some(AnalysisResult(ast,
                                                                                            errors,
                                                                                            warnings,
                                                                                            notes))
    }
  }
}
