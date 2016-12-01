package org.metaborg.scalaterms

import org.spoofax.interpreter.terms.IStrategoTerm
import org.spoofax.jsglr.client.imploder.ImploderAttachment

/**
  * Scala representation of the Imploder Attachment on ATerms for the origin information (ImploderAttachment)
  */
class Origin(val filename: String, val line: Int, val column: Int, val startOffset: Int, val endOffset: Int) {
  def toStratego: ImploderAttachment = {
    ImploderAttachment.createCompactPositionAttachment(filename, line, column, startOffset, endOffset)
  }
}

object Origin {
  def fromStratego(term: IStrategoTerm): Origin = {
    val origin = ImploderAttachment.getCompactPositionAttachment(term, false)
    if (origin == null) {
      null
    } else {
      new Origin(filename = origin.getLeftToken.getTokenizer.getFilename,
                 line = origin.getLeftToken.getLine,
                 column = origin.getLeftToken.getColumn,
                 startOffset = origin.getLeftToken.getStartOffset,
                 endOffset = origin.getRightToken.getEndOffset)
    }
  }

  def zero(o: Origin): Origin = {
    if (o == null) {
      null
    } else {
      new Origin(o.filename, 0, 0, 0, 0)
    }
  }
}
