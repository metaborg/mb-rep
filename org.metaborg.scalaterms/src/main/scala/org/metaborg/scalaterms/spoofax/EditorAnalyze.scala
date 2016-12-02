package org.metaborg.scalaterms.spoofax

import org.metaborg.scalaterms.STerm
import org.spoofax.interpreter.terms.IStrategoTerm
import org.strategoxt.lang.Context

/**
  * `editor-analyze` strategy that can be called from the Java `InteropRegistrer` in the Spoofax project
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
trait EditorAnalyze {

  /**
    * Directly callable method from Java to register an `editor-analyze` strategy externally.
    *
    * @param context the strategy context
    * @param term    the strategy input tuple
    * @return analysis result compliant with the expected Java ATerm
    */
  def editorAnalyze(context: Context, term: IStrategoTerm): IStrategoTerm = {
    implicit val ctx = context
    GeneralStrategyInput.fromStratego(term) match {
      case Some(gsi) => editorAnalyze(gsi).toStratego
      case None => throw new RuntimeException("editor-analyze Scala implementation expects the 3-tuple or 5-tuple input")
    }
  }

  def editorAnalyze(generalStrategyInput: GeneralStrategyInput)(implicit context: Context): AnalysisResult =
    AnalysisResult(STerm.Tuple(Seq()), Seq(), Seq(), Seq())
}
