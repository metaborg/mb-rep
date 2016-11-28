package org.metaborg

/**
  * Backward compatible types
  */
package object scalaterms {
  type Extract[T <: TermLike] = FromSTerm[T]
}
