package org.spoofax.scalaTerms

/**
  * A pattern to match an STerm against. Can also match lists and options of these.
 *
  * @tparam T the type to extract from the STerm
  */
trait Extract[T] {self =>
  /**
    * The list matcher for the same type.
    */
  val list = new ExtractList[T] {
    override val extract = self
  }
  
  /**
    * The option matcher for the same type.
    */
  val option = new ExtractOption[T] {
    override val extract = self
  }
  
  /**
    * The extraction from STerm
    * @param term the STerm
    * @return the Some(T) that's extracted if matched
    */
  def unapply(term: STerm): Option[T]
}
