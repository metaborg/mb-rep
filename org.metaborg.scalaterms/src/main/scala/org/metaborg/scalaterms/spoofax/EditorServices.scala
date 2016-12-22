package org.metaborg.scalaterms.spoofax

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
trait EditorServices extends EditorAnalyze with EditorResolve with EditorHover
