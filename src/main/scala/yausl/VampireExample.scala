import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

class body(tree: Any) extends StaticAnnotation

object VampireExample {
  def demo(name: String) = macro demo_impl

  def demo_impl(c: Context)(name: c.Expr[String]): c.Expr[Any] = {
    import c.universe._

    val methodName = name.tree match {
      case Literal(Constant(s: String)) => TermName(s)
      case _ => c.abort(c.enclosingPosition, "Must provide a literal name!")
    }

    val className = TypeName(c.freshName("COVEN"))

    c.Expr[Any](
      q"""
        class $className {
          val baz = 32
          @body((x: Int) => x + baz)
          def $methodName(x: Int): Int = macro VampireExample.method_impl
        }
        new $className {}
      """
    )
  }

  def method_impl(c: Context)(x: c.Expr[Int]): c.Expr[Int] = {
    import c.universe._
    import org.scalamacros.resetallattrs._

    val prefixVal = TermName(c.freshName())

    object replaceThises extends Transformer {
      override def transform(tree: Tree) = tree match {
        case This(qual) if qual.decodedName.toString.startsWith("COVEN") => Ident(prefixVal)
        case other => super.transform(other)
      }
    }

    val (arg, body) = c.macroApplication.symbol.annotations.find(
      _.tree.tpe <:< typeOf[body]
    ).flatMap(
        _.tree.children.tail.collectFirst {
          case Function(ValDef(_, arg, _, _) :: Nil, body) =>
            arg -> c.resetAllAttrs(body)
        }
      ).getOrElse(
        c.abort(c.enclosingPosition, "Annotation body not provided!")
      )

    c.Expr(
      q"""
        val $prefixVal = ${c.prefix}
        val $arg = $x
        ${replaceThises.transform(body)}
      """
    )
  }
}