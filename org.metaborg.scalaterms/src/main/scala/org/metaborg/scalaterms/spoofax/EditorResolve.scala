package org.metaborg.scalaterms.spoofax

import org.spoofax.interpreter.terms.IStrategoTerm
import org.strategoxt.lang.Context

/**
  * `editor-resolve` strategies that can be called from the Java `InteropRegistrer` in the Spoofax project
  * Easiest to call from Java when the implementer is an object.
  */
trait EditorResolve {

  /**
    * Directly callable method from Java to register an `editor-resolve` strategy externally.
    *
    * @param context the strategy context
    * @param term    the strategy input tuple
    * @return resolution result compliant with the expected Java ATerm
    */
  def editorResolve(context: Context, term: IStrategoTerm): IStrategoTerm = {
    implicit val ctx = context
    FocusedStrategyInput.fromStratego(term).flatMap(editorResolve).map(_.toStratego).orNull
  }

  /**
    * Override this.
    *
    * @param focusedStrategyInput
    * @param context
    * @return Some(ResolutionResult) if applicable, None otherwise
    */
  def editorResolve(focusedStrategyInput: FocusedStrategyInput)
                   (implicit context: Context): Option[ResolutionResult] = None
}
