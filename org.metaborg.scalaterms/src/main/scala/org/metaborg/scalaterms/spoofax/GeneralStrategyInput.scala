package org.metaborg.scalaterms.spoofax

import org.metaborg.scalaterms._

/**
  * Strategy input that's given, for example, to `editor-hover` and `editor-resolve`
  */
case class GeneralStrategyInput(ast: STerm, path: String, projectPath: String, origin: Origin) extends TermLike {
  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = {
    STerm.Tuple(List(ast, STerm.String(path, origin), STerm.String(projectPath, origin)), origin)
  }

  /**
    * Convenience function to make an `AnalysisResult`. Automatically sets the origin to that of the input and
    * optionally sets the ast to the input ast.
    *
    * @param errors   The list of error messages to report
    * @param warnings The list of warnings to report
    * @param notes    The list of notes to report
    * @param ast      The ast to give back. Default is the input ast
    * @return The AnalysisResult that can directly be turned into a Spoofax consumable representation with `toStratego`
    */
  def makeAnalysisResult(errors: List[EditorMessage],
                         warnings: List[EditorMessage],
                         notes: List[EditorMessage],
                         ast: STerm = this.ast): AnalysisResult = {
    AnalysisResult(ast, errors, warnings, notes, this.origin)
  }
}

object GeneralStrategyInput extends TermLikeCompanion[GeneralStrategyInput] {
  override val fromSTerm = new FromSTerm[_] {
    /**
      * The extraction from STerm
      *
      * @param term the STerm
      * @return the Some(T) that's extracted if matched
      */
    override def unapply(term: STerm): Option[GeneralStrategyInput] = term match {
      case STerm.Tuple(List(ast, STerm.String(path, _), STerm.String(projectPath, _)), origin) => Some(
        GeneralStrategyInput(ast, path, projectPath, origin))
      case FocusedStrategyInput.fromSTerm(fsi) => Some(fsi)
      case _ => None
    }
  }
}
