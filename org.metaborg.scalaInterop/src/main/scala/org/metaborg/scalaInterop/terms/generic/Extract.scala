package org.metaborg.scalaInterop.terms.generic

import org.metaborg.scalaInterop.terms.stratego.{Term => STerm}

trait Extract[T] {
  def unapply(arg: STerm): Option[T]
}
