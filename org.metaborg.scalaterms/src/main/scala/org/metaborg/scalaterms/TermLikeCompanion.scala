package org.metaborg.scalaterms

/**
  * The companion object of a TermLike.
  * It can turn an STerm into the class that it's a companion (`T`).
  * That automatically also allows it to go from a Java ATerm directly to `T`.
  * Both of these transformations are object with apply and unapply methods so they can be used like methods
  * or patterns in a pattern match.
  * @tparam T The class this object is a companion of
  */
trait TermLikeCompanion[T] { self =>
  val fromSTerm: FromSTerm[T]
  val fromStratego: FromStratego[T] = new FromStratego[T] {
    override val fromSTerm: Extract[T] = self.fromSTerm
  }
}
