package org.spoofax.scalaTerms.sdf

import org.spoofax.scalaTerms.{ Origin, STerm, TermLike }

/**
  * Created by jeff on 24/11/16.
  */
sealed trait Option[+A] extends TermLike

final case class Some[+A <: TermLike](x: A, origin: Origin) extends Option[A] {
  def isEmpty = false
  
  def get = x
  
  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = STerm.Cons("Some", List(x.toSTerm), origin)
}

case class None(origin: Origin) extends Option[Nothing] {
  def isEmpty = true
  
  def get = throw new NoSuchElementException("None.get")
  
  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = STerm.Cons("None", List(), origin)
}

object implicits {
  implicit def sOptionToOption[A](so: Option[A]): scala.Option[A] = so match {
    case Some(t, _) => scala.Some(t)
    case _          => scala.None
  }
}