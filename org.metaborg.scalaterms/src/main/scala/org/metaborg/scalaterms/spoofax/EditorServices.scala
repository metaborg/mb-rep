package org.metaborg.scalaterms.spoofax

import org.metaborg.scalaterms.STerm
import org.spoofax.interpreter.core.Context
import org.spoofax.interpreter.terms.IStrategoTerm

/**
  * `editor-*` strategies that can be called from the Java `InteropRegistrer` in the Spoofax project
  * Easiest to call from Java when the implementer is an object. Then the call is for example:
  *
  *     public class editor_analyze_0_0 extends Strategy {
	*         public static editor_analyze_0_0 instance = new editor_analyze_0_0();
  *
  *         @Override public IStrategoTerm invoke(Context context, IStrategoTerm current) {
  *             return EditorServicesImpl$.MODULE$.editorAnalyze(context, current);
  *         }
  *     }
  *
  */
trait EditorServices {
  /**
    * Directly callable method from Java to register an `editor-analyze` strategy externally.
    * @param context the strategy context
    * @param term the strategy input tuple
    * @return analysis result compliant with the expected Java ATerm
    */
  def editorAnalyze(context: Context, term: IStrategoTerm): IStrategoTerm = {
    implicit val ctx = context
    GeneralStrategyInput.fromStratego(term) match {
      case Some(gsi) => editorAnalyze(gsi).toStratego
      case None => null
    }
  }

  /**
    * Directly callable method from Java to register an `editor-resolve` strategy externally.
    * @param context the strategy context
    * @param term the strategy input tuple
    * @return resolution result compliant with the expected Java ATerm
    */
  def editorResolve(context: Context, term: IStrategoTerm): IStrategoTerm = {
    implicit val ctx = context
    FocusedStrategyInput.fromStratego(term) match {
      case Some(fsi) => editorResolve(fsi).toStratego
      case None => null
    }
  }

  /**
    * Directly callable method from Java to register an `editor-hover` strategy externally.
    * @param context the strategy context
    * @param term the strategy input tuple
    * @return hover message compliant with the expected Java ATerm
    */
  def editorHover(context: Context, term: IStrategoTerm): IStrategoTerm = {
    implicit val ctx = context
    FocusedStrategyInput.fromStratego(term) match {
      case Some(fsi) => editorHover(fsi).toStratego
      case None => null
    }
  }

  def editorAnalyze(generalStrategyInput: GeneralStrategyInput)(implicit context: Context): AnalysisResult

  def editorResolve(focusedStrategyInput: FocusedStrategyInput)(implicit context: Context): ResolutionResult

  def editorHover(focusedStrategyInput: FocusedStrategyInput)(implicit context: Context): HoverResult
}
