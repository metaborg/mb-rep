package org.metaborg.scalaTerms

import org.metaborg.scalaTerms.sdf._

/**
  * Extracts an option of T from an STerm version of Option
  *
  * @tparam T the type to extract from the STerm
  */
trait ExtractOption[T] extends Extract[Option[T]] {
  val extract: Extract[T]
  
  override def unapply(term: STerm): scala.Option[Option[T]] = term match {
    case STerm.Cons("Some", List(t), o) => extract.unapply(t).map(Some(_, o))
    case STerm.Cons("None", List(), o)  => scala.Some(None(o))
    case _                              => scala.None
  }
}
