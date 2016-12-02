package org.metaborg.scalaterms.spoofax
import org.spoofax.interpreter.terms.IStrategoTerm
import org.strategoxt.lang.Context

/**
  * Created by jeff on 01/12/16.
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
    GeneralStrategyInput.fromStratego(term).map(editorAnalyze).map(_.toStratego).orNull
  }

  def editorAnalyze(generalStrategyInput: GeneralStrategyInput)(implicit context: Context): AnalysisResult
}
