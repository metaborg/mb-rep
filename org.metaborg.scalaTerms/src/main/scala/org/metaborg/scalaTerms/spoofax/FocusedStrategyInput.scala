package org.metaborg.scalaTerms.spoofax

import org.metaborg.scalaTerms.{ Extract, Origin, STerm }

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

object ExtractFocusedStrategyInput extends Extract[FocusedStrategyInput] {
  override def unapply(term: STerm): Option[FocusedStrategyInput] = term match {
    case STerm.Tuple(List(node, position, ast, STerm.String(path, _), STerm.String(projectPath, _)), origin) => Some(
      FocusedStrategyInput(node, position, ast, path, projectPath, origin))
    case _                                                                                                   => None
  }
}
