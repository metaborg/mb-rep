package org.metaborg.scalaterms.sdf

import org.metaborg.scalaterms.{HasOrigin, Origin, STerm, TermLike}

/**
  * An option as generated by a parser on an SDF question mark operator.
  * @todo Add more of the `scala.Option` methods when an equivalent exists
  */
sealed trait Option[+A] extends TermLike with HasOrigin

final case class Some[+A <: TermLike](x: A, override val origin: Origin) extends Option[A] {
  def isEmpty = false

  def get = x

  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = STerm.Cons("Some", Seq(x.toSTerm), scala.Some(origin))
}

case class None(override val origin: Origin) extends Option[Nothing] {
  def isEmpty = true

  def get = throw new NoSuchElementException("None.get")

  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = STerm.Cons("None", Seq(), scala.Some(origin))
}

object implicits {
  implicit def sdfOptionToOption[A](so: Option[A]): scala.Option[A] = so match {
    case Some(t, _) => scala.Some(t)
    case _ => scala.None
  }
}