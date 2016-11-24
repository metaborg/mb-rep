package org.metaborg.scalaTerms

/**
  * Extracts a list of T from an STerm version of List
  *
  * @tparam T the type to extract from the STerm
  */
trait ExtractList[T] extends Extract[STerm.List[T]] {
  val extract: Extract[T]
  
  override def unapply(term: STerm): Option[STerm.List[T]] = term match {
    case STerm.List(l, o) =>
      val l2 = l.map(t => extract.unapply(t.toSTerm))
      if (l2.contains(None)) {
        None
      }
      else {
        Some(STerm.List(l2.map(_.get), o))
      }
    case _                => None
  }
}

