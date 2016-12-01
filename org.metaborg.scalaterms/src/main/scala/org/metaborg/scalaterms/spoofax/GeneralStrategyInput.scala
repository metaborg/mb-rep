package org.metaborg.scalaterms.spoofax

import org.metaborg.scalaterms._

/**
  * Strategy input that's given, for example, to `editor-hover` and `editor-resolve`
  */
class GeneralStrategyInput(val ast: STerm,
                           val path: String,
                           val projectPath: String) extends TermLike {
  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = {
    STerm.Tuple(List(ast, STerm.String(path), STerm.String(projectPath)))
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
    AnalysisResult(ast, errors, warnings, notes)
  }
}

object GeneralStrategyInput extends TermLikeCompanion[GeneralStrategyInput] {
  override val fromSTerm = new FromSTerm[GeneralStrategyInput] {
    /**
      * The extraction from STerm
      *
      * @param term the STerm
      * @return the Some(T) that's extracted if matched
      */
    override def unapply(term: STerm): Option[GeneralStrategyInput] = term match {
      case STerm.List(List(ast, STerm.String(path, _), STerm.String(projectPath, _)), _) => Some(
        GeneralStrategyInput(ast.toSTerm, path, projectPath))
      case FocusedStrategyInput.fromSTerm(fsi) => Some(fsi)
      case _ => None
    }
  }

  def apply(ast: STerm,
            path: String,
            projectPath: String): GeneralStrategyInput = new GeneralStrategyInput(ast, path, projectPath)

  def unapply(gsi: GeneralStrategyInput): Option[(STerm, String, String)] = Some((gsi.ast, gsi.path, gsi
    .projectPath))
}
