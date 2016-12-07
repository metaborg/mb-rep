package org.metaborg.scalaterms.sdf

import org.metaborg.scalaterms
import org.metaborg.scalaterms.HasOrigin

/**
  * Represents an SDF Lexical Sort. The implementor can be generated from an SDF specification.
  */
trait Lexical extends scalaterms.TermLike with HasOrigin
