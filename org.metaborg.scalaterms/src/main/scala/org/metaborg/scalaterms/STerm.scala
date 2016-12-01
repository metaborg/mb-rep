package org.metaborg.scalaterms

import org.strategoxt.lang.Context
import org.spoofax.interpreter.terms._

import scala.collection.JavaConverters._

/**
  * The Scala representation of ATerms
  */
sealed trait STerm extends TermLike {
  val origin: Option[Origin] = None
  /**
    * @return equivalent Scala ATerm representation
    */
  override def toSTerm: STerm = this

  /**
    * @return equivalent Java ATerm representation
    */
  override def toStratego(implicit context: Context): IStrategoTerm
}

object STerm {
  /**
    * @param iStrategoTerm the Java ATerm representation
    * @return equivalent Scala representation
    */
  def fromStratego(iStrategoTerm: IStrategoTerm): STerm = {
    val origin = Origin.fromStratego(iStrategoTerm)
    iStrategoTerm.getTermType match {
      case IStrategoTerm.INT => Int(iStrategoTerm.asInstanceOf[IStrategoInt].intValue, origin)
      case IStrategoTerm.REAL => Real(iStrategoTerm.asInstanceOf[IStrategoReal].realValue, origin)
      case IStrategoTerm.STRING => String(iStrategoTerm.asInstanceOf[IStrategoString].stringValue, origin)
      case IStrategoTerm.LIST => List(iStrategoTerm.asInstanceOf[IStrategoList].asScala.toList.map(fromStratego),
                                      origin)
      case IStrategoTerm.TUPLE => Tuple(iStrategoTerm.asInstanceOf[IStrategoTuple].asScala.toList.map(fromStratego),
                                        origin)
      case IStrategoTerm.APPL =>
        val applTerm = iStrategoTerm.asInstanceOf[IStrategoAppl]
        val children = applTerm.asScala.toList.map(fromStratego)
        Cons(applTerm.getName, children, origin)
    }
  }

  case class Int(value: scala.Int, override val origin: Option[Origin] = None) extends STerm {
    /**
      * @return equivalent Java ATerm representation
      */
    override def toStratego(implicit context: Context): IStrategoTerm = {
      val term = context.getFactory.makeInt(value)
      origin match {
        case Some(o) => term.putAttachment(o.toStratego)
        case None => // Do nothing
      }
      term
    }
  }

  case class Real(value: Double, override val origin: Option[Origin] = None) extends STerm {
    /**
      * @return equivalent Java ATerm representation
      */
    override def toStratego(implicit context: Context): IStrategoTerm = {
      val term = context.getFactory.makeReal(value)
      origin match {
        case Some(o) => term.putAttachment(o.toStratego)
        case None => // Do nothing
      }
      term
    }
  }

  case class String(value: java.lang.String, override val origin: Option[Origin] = None) extends STerm {
    /**
      * @return equivalent Java ATerm representation
      */
    override def toStratego(implicit context: Context): IStrategoTerm = {
      val term = context.getFactory.makeString(value)
      origin match {
        case Some(o) => term.putAttachment(o.toStratego)
        case None => // Do nothing
      }
      term
    }
  }

  case class List[T <: TermLike](value: scala.List[T], override val origin: Option[Origin] = None) extends STerm {
    /**
      * @return equivalent Java ATerm representation
      */
    override def toStratego(implicit context: Context): IStrategoTerm = {
      val term = context.getFactory.makeList(value.map(_.toStratego).asJava)
      origin match {
        case Some(o) => term.putAttachment(o.toStratego)
        case None => // Do nothing
      }
      term
    }
  }

  case class Tuple(value: scala.List[STerm], override val origin: Option[Origin] = None) extends STerm {
    /**
      * @return equivalent Java ATerm representation
      */
    override def toStratego(implicit context: Context): IStrategoTerm = {
      val factory = context.getFactory
      val term = factory.makeTuple(value.map(_.toStratego).toArray, factory.makeList())
      origin match {
        case Some(o) => term.putAttachment(o.toStratego)
        case None => // Do nothing
      }
      term
    }

  }

  case class Cons(value: java.lang.String, children: scala.List[STerm], override val origin: Option[Origin] = None) extends STerm {
    /**
      * @return equivalent Java ATerm representation
      */
    override def toStratego(implicit context: Context): IStrategoTerm = {
      val factory = context.getFactory
      val constructor = factory.makeConstructor(value, children.length)
      val term = factory.makeAppl(constructor, children.map(_.toStratego).toArray, factory.makeList)
      origin match {
        case Some(o) => term.putAttachment(o.toStratego)
        case None => // Do nothing
      }
      term
    }
  }

}

object implicits {
  implicit def termLikeToTerm[T <: TermLike](t: T): STerm = t.toSTerm

  implicit def sListToList[T <: TermLike](l: STerm.List[T]): List[T] = l.value
}