package org.spoofax.scalaTerms.spoofax

import org.spoofax.scalaTerms.{ Extract, Origin, STerm, TermLike }

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
}

object ExtractGeneralStrategyInput extends Extract[GeneralStrategyInput] {
  override def unapply(term: STerm): Option[GeneralStrategyInput] = term match {
    case STerm.Tuple(List(ast, STerm.String(path, _), STerm.String(projectPath, _)), origin) => Some(
      GeneralStrategyInput(ast, path, projectPath, origin))
    case ExtractFocusedStrategyInput(fsi)                                                    => Some(fsi)
    case _                                                                                   => None
  }
}
