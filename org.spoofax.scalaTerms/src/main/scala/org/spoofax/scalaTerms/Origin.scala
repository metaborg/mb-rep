package org.spoofax.scalaTerms

import org.spoofax.interpreter.terms.IStrategoTerm
import org.spoofax.jsglr.client.imploder.ImploderAttachment

/**
  * Scala representation of the Imploder Attachment on ATerms for the origin information (ImploderAttachment)
  */
class Origin(filename: String, line: Int, column: Int, startOffset: Int, endOffset: Int) {
  def toStratego: ImploderAttachment = {
    ImploderAttachment.createCompactPositionAttachment(filename, line, column, startOffset, endOffset)
  }
}

object Origin {
  def fromStratego(term: IStrategoTerm): Origin = {
    val origin = ImploderAttachment.getCompactPositionAttachment(term, false)
    new Origin(filename = origin.getLeftToken.getTokenizer.getFilename,
      line = origin.getLeftToken.getLine,
      column = origin.getLeftToken.getColumn,
      startOffset = origin.getLeftToken.getStartOffset,
      endOffset = origin.getRightToken.getEndOffset)
  }
}
