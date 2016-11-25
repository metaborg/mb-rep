package org.metaborg.scalaterms.spoofax

import org.metaborg.scalaterms.{FromSTerm, Origin, STerm, TermLikeCompanion}

/**
  * Strategy input that's given, for example, to `editor-analyze`
  */
case class FocusedStrategyInput(node: STerm,
                                position: STerm,
                                override val ast: STerm,
                                override val path: String,
                                override val projectPath: String,
                                override val origin: Origin)
  extends GeneralStrategyInput(ast, path, projectPath, origin) {
  
  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = {
    STerm.Tuple(List(node, position, ast, STerm.String(path, origin), STerm.String(projectPath, origin)), origin)
  }
}

object FocusedStrategyInput extends TermLikeCompanion[FocusedStrategyInput] {
  override val fromSTerm = new FromSTerm[FocusedStrategyInput] {
    /**
      * The extraction from STerm
      *
      * @param term the STerm
      * @return the Some(T) that's extracted if matched
      */
    override def unapply(term: STerm): Option[FocusedStrategyInput] = term match {
      case STerm.Tuple(List(node, position, ast, STerm.String(path, _), STerm.String(projectPath, _)), origin) => Some(
        FocusedStrategyInput(node, position, ast, path, projectPath, origin))
      case _                                                                                                   => None
    }
  }
}