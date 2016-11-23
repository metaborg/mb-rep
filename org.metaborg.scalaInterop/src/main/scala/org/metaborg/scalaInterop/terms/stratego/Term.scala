package org.metaborg.scalaInterop.terms.stratego

import org.spoofax.interpreter.terms._
import org.strategoxt.lang.Context

import scala.collection.JavaConverters._

trait TermLike {
  val origin: Origin
  
  /**
    * @return equivalent scalaInterop.stratego ATerm representation
    */
  def toSTerm: Term
  
  /**
    * @return equivalent Java ATerm representation
    */
  def toStratego(implicit context: Context): IStrategoTerm = {
    this.toSTerm.toStratego(context)
  }
}

/**
  * The scalaInterop.stratego representation of ATerms
  */
sealed trait Term extends TermLike {
  /**
    * @return equivalent scalaInterop.stratego ATerm representation
    */
  override def toSTerm: Term = this
  
  /**
    * @return equivalent Java ATerm representation
    */
  override def toStratego(implicit context: Context): IStrategoTerm
}

object Term {
  
  /**
    * @param iStrategoTerm the Java ATerm representation
    *
    * @return equivalent scalaInterop.stratego representation
    */
  def fromStratego(iStrategoTerm: IStrategoTerm): Term = {
    val origin = Origin.fromStratego(iStrategoTerm)
    iStrategoTerm.getTermType match {
      case IStrategoTerm.INT    => Int(iStrategoTerm.asInstanceOf[IStrategoInt].intValue, origin)
      case IStrategoTerm.REAL   => Real(iStrategoTerm.asInstanceOf[IStrategoReal].realValue, origin)
      case IStrategoTerm.STRING => String(iStrategoTerm.asInstanceOf[IStrategoString].stringValue, origin)
      case IStrategoTerm.LIST   => List(iStrategoTerm.asInstanceOf[IStrategoList].asScala.toList.map(fromStratego),
                                        origin)
      case IStrategoTerm.TUPLE  => Tuple(iStrategoTerm.asInstanceOf[IStrategoTuple].asScala.toList.map(fromStratego),
                                         origin)
      case IStrategoTerm.APPL   =>
        val applTerm = iStrategoTerm.asInstanceOf[IStrategoAppl]
        val children = applTerm.asScala.toList.map(fromStratego)
        Cons(applTerm.getName, children, origin)
    }
  }
  
  case class Int(value: scala.Int, origin: Origin) extends Term {
    /**
      * @return equivalent Java ATerm representation
      */
    override def toStratego(implicit context: Context): IStrategoTerm = {
      val term = context.getFactory.makeInt(value)
      term.putAttachment(origin.toStratego)
      term
    }
  }
  
  case class Real(value: Double, origin: Origin) extends Term {
    /**
      * @return equivalent Java ATerm representation
      */
    override def toStratego(implicit context: Context): IStrategoTerm = {
      val term = context.getFactory.makeReal(value)
      term.putAttachment(origin.toStratego)
      term
    }
  }
  
  case class String(value: java.lang.String, origin: Origin) extends Term {
    /**
      * @return equivalent Java ATerm representation
      */
    override def toStratego(implicit context: Context): IStrategoTerm = {
      val term = context.getFactory.makeString(value)
      term.putAttachment(origin.toStratego)
      term
    }
  }
  
  case class List(value: scala.List[Term], origin: Origin) extends Term {
    /**
      * @return equivalent Java ATerm representation
      */
    override def toStratego(implicit context: Context): IStrategoTerm = {
      val term = context.getFactory.makeList(value.map(_.toStratego).asJava)
      term.putAttachment(origin.toStratego)
      term
    }
  }
  
  case class Tuple(value: scala.List[Term], origin: Origin) extends Term {
    /**
      * @return equivalent Java ATerm representation
      */
    override def toStratego(implicit context: Context): IStrategoTerm = {
      val factory = context.getFactory
      val term = factory.makeTuple(value.map(_.toStratego).toArray, factory.makeList())
      term.putAttachment(origin.toStratego)
      term
    }
    
  }
  
  case class Cons(value: java.lang.String, children: scala.List[Term], origin: Origin) extends Term {
    /**
      * @return equivalent Java ATerm representation
      */
    override def toStratego(implicit context: Context): IStrategoTerm = {
      val factory = context.getFactory
      val constructor = factory.makeConstructor(value, children.length)
      val term = factory.makeAppl(constructor, children.map(_.toStratego).toArray, factory.makeList)
      term.putAttachment(origin.toStratego)
      term
    }
  }
  
}

object conversions {
  implicit def termLikeToTerm[T <: TermLike](t: T): Term = t.toSTerm
}