package org.metaborg.scalaterms

/**
  * A pattern to match an STerm against. Can also match lists and options of these.
  *
  * @tparam T the type to extract from the STerm
  */
trait FromSTerm[T <: TermLike] {self =>
  /**
    * The list matcher for the same type.
    */
  lazy val list = new FromSTerm[STerm.List[T]] {
    override def unapply(term: STerm): Option[STerm.List[T]] = term match {
      case STerm.List(l, o) =>
        val l2: List[Option[T]] = l.map(t => self.unapply(t.toSTerm))
        if (l2.contains(None.asInstanceOf[Option[T]])) {
          None
        }
        else {
          Some(STerm.List(l2.map(_.get), o))
        }
      case _ => None
    }
  }

  /**
    * The option matcher for the same type.
    */
  lazy val option = new FromSTerm[sdf.Option[T]] {
    override def unapply(term: STerm): Option[sdf.Option[T]] = term match {
      case STerm.Cons("Some", List(t), Some(o)) => self.unapply(t).map(sdf.Some(_, o))
      case STerm.Cons("None", List(), Some(o)) => Some(sdf.None(o))
      case _ => None
    }
  }

  /**
    * The extraction from STerm within a pattern match
    *
    * @param term the STerm
    * @return the Some(T) that's extracted if matched
    */
  def unapply(term: STerm): Option[T]

  /**
    * The same extraction from STerm outside of a pattern match
    *
    * @param term the STerm
    * @return the Some(T) that's extracted if matched
    */
  def apply(term: STerm): Option[T] = unapply(term)
}
