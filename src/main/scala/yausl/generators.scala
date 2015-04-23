package yausl

import shapeless.HList.ListCompat._
import shapeless.{CaseClassMacros, LUBConstraint, HList}
import shapeless.ops.hlist.Selector

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

object SystemGenerator {

  /** A helper method that allows a macro method to read an annotation on
    * itself. We'll use this below in the macro methods on the anonymous class
    * we're defining.
    */
  def method_impl[Units <: HList, U <: UnitM[_], Dims <: HList](c: Context)
                                                   (dimOf : c.Expr[DimensionsOf.Aux[Units, U, Dims]])
  : c.Expr[yausl.Scalar[Units, Dims]] = {
    import c.universe._
    import org.scalamacros.resetallattrs._

    val prefixVal = TermName(c.freshName())
    val prefixVal2 = TermName(c.freshName())

    object replaceThises extends Transformer {
      override def transform(tree: Tree) = tree match {
        case This(qual) if qual.decodedName.toString.startsWith("System") => Ident(prefixVal)
        case This(qual) if qual.decodedName.toString.startsWith("unitExtension") => Ident(prefixVal2)
        case other => super.transform(other)
      }
    }

    val (arg, body) = c.macroApplication.symbol.annotations.find(
      _.tree.tpe <:< typeOf[body]
    ).flatMap { x =>
      x.tree.children.tail.collectFirst {
        case Function(ValDef(_, arg, _, _) :: Nil, body) => arg -> c.resetAllAttrs(body)
      }
    }.getOrElse(c.abort(c.enclosingPosition, "Annotation body not provided!"))

    c.Expr(
      q"""
        val $prefixVal = ${c.prefix}
        val $prefixVal2 = ${c.prefix}
        val $arg = $dimOf
        ${replaceThises.transform(body)}
        """
    )
  }

  def fromHList[Units <: HList](implicit ev: LUBConstraint[Units, UnitM[_]]): System[Units] =
  macro SystemMacros.fromHListImpl[Units]

  class SystemMacros(val c: Context) extends CaseClassMacros {

    def fromHListImpl[Units <: HList : c.WeakTypeTag](ev: c.Expr[LUBConstraint[Units, UnitM[_]]]) = {
      import c.universe._
      val tpe = c.weakTypeOf[Units]

      val className = TypeName(c.freshName("System"))
      val extensionName = TypeName(c.freshName("unitExtension"))

      c.Expr[System[Units]](

        q"""
          class $className extends yausl.System[$tpe] {

            implicit class $extensionName(val value : Double){
              ..${ hlistElements(tpe).map { t =>
                    val name = t.typeSymbol.name.toString
                    q"""
                      @yausl.body((dimOf : yausl.DimensionsOf[$tpe,$t]) => new yausl.Scalar[$tpe, dimOf.result](value))
                      def ${TermName(name)}[T <: shapeless.HList](implicit dimOf : yausl.DimensionsOf.Aux[$tpe, $t, T]) :
                      yausl.Scalar[$tpe, T] = macro SystemGenerator.method_impl[$tpe, $t, T]
                    """
                  }
              }
            }

          }
          new $className {}
        """
      )
    }
  }

}

class body(tree: Any) extends StaticAnnotation

/**
 * A system is a list of accepted units.
 */
trait System[Units <: HList] {

  def measure[T <: UnitM[_]](value: Double)(implicit dimOf: DimensionsOf[Units, T]): Scalar[Units, dimOf.result] =
    new Scalar[Units, dimOf.result](value)

  implicit class zogzog(value: Double) {
    def m[T <: UnitM[_]](implicit ev: Selector[Units, T], dimOf: DimensionsOf[Units, T]) = measure[T](value)
  }

}


class Pipo[T](val v: Int) extends AnyVal {
  def pipo = "pipo"
}

trait PipoTypeClass[X] {
  def e: Int
}

object PipoTypeClass {
  implicit val instance : PipoTypeClass[Double] = new PipoTypeClass[Double] {
    def e = 3
  }
}