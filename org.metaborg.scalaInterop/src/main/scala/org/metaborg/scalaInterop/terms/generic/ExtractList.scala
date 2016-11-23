package org.metaborg.scalaInterop.terms.generic

import org.metaborg.scalaInterop.terms.stratego.{Term => STerm}

trait ExtractList[T] {
  val Extract: Extract[T]
  
  def unapply(term: STerm): Option[List[T]] = term match {
    case STerm.List(l, o) =>
      val l2 = l.map(Extract.unapply)
      if (l2.contains(None)) None
      else Some(l2.map(_.get))
    case _ => None
  }
}

