package org.metaborg.scalaterms.spoofax
import org.spoofax.interpreter.terms.IStrategoTerm
import org.strategoxt.lang.Context

/**
  * `editor-hover` strategies that can be called from the Java `InteropRegistrer` in the Spoofax project
  * Easiest to call from Java when the implementer is an object.
  */
trait EditorHover {

  /**
    * Directly callable method from Java to register an `editor-hover` strategy externally.
    *
    * @param context the strategy context
    * @param term    the strategy input tuple
    * @return hover message compliant with the expected Java ATerm
    */
  def editorHover(context: Context, term: IStrategoTerm): IStrategoTerm = {
    implicit val ctx = context
    FocusedStrategyInput.fromStratego(term).flatMap(editorHover).map(_.toStratego).orNull
  }

  /**
    * Override this.
    *
    * @param focusedStrategyInput
    * @param context
    * @return Some(HoverResult) if applicable, None otherwise
    */
  def editorHover(focusedStrategyInput: FocusedStrategyInput)(implicit context: Context): Option[HoverResult] = None
}
