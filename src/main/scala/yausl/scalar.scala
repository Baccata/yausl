package yausl

import shapeless._

import scala.annotation.implicitNotFound

class Scalar[U <: HList, T <: HList] /* protected[yausl] */  (val value: Double) extends AnyVal {

  def +(s: Scalar[U, T]): Scalar[U, T] = new Scalar(value + s.value)

  def -(s: Scalar[U, T]): Scalar[U, T] = new Scalar(value + s.value)

  def *[T2 <: HList](s: Scalar[U, T2])(implicit product: Product[T, T2]): Scalar[U, product.result] =
    new Scalar[U, product.result](value * s.value)

  def /[T2 <: HList](s: Scalar[U, T2])(implicit division: Division[T, T2]): Scalar[U, division.result] =
    new Scalar[U, division.result](value / s.value)
}

@implicitNotFound("Could not derive a multiplication from the provided values. " +
  "Are you trying to multiply values from different unit systems ?")
trait Product[T1 <: HList, T2 <: HList] {
  type result <: HList
}

object Product {
  type Aux[HL1 <: HList, HL2 <: HList, Out <: HList] = Product[HL1, HL2]{type result = Out}
  implicit val product1: Aux[HNil, HNil, HNil] = new Product[HNil, HNil] {
    type result = HNil
  }

  implicit def product2[H1 <: Integer, R1 <: HList, H2 <: Integer, R2 <: HList](implicit prodAux: Product[R1, R2])
  : Aux[H1 :: R1, H2 :: R2, ((H1 + H2) :: prodAux.result)] =
    new Product[H1 :: R1, H2 :: R2] {
      type result = (H1 + H2) :: prodAux.result
    }
}

@implicitNotFound("Could not derive a division from the provided values. " +
  "Are you trying to divide values from different unit systems ?")
trait Division[T1 <: HList, T2 <: HList] {
  type result <: HList
}

object Division {
  type Aux[HL1 <: HList, HL2 <: HList, Out <: HList] = Division[HL1, HL2]{type result = Out}
  implicit val div1: Aux[HNil, HNil, HNil] = new Division[HNil, HNil] {
    type result = HNil
  }

  implicit def div2[H1 <: Integer, R1 <: HList, H2 <: Integer, R2 <: HList](implicit divAux: Division[R1, R2])
  : Aux[H1 :: R1, H2 :: R2, (H1 - H2) :: divAux.result] = new Division[H1 :: R1, H2 :: R2] {
      type result = (H1 - H2) :: divAux.result
    }
}

trait BaseQuantity
trait UnitM[M <: BaseQuantity]


/**
 * Typeclass used by a system to give dimensions to a measure.
 */
trait DimensionsOf[Units <: HList, U <: UnitM[_]] {
  type result <: HList
}

object DimensionsOf {
  type Aux[Units <: HList, U <: UnitM[_], Out <: HList] = DimensionsOf[Units, U]{type result = Out}

  implicit def dimOf1[U <: UnitM[_]]: Aux[HNil, U, HNil] = new DimensionsOf[HNil, U] {
    type result = HNil
  }

  implicit def dimOf2[R <: HList, U <: UnitM[_]](implicit r: DimensionsOf[R, U]): Aux[U :: R, U, p1 :: r.result] =
    new DimensionsOf[U :: R, U] {
      type result = p1 :: r.result
    }

  implicit def dimOf3[R <: HList, U <: UnitM[_], U2  <: UnitM[_]](implicit r: DimensionsOf[R, U], ev: U =:!= U2)
  : Aux[U2 :: R, U, _0 :: r.result] = new DimensionsOf[U2 :: R, U] {
    type result = _0 :: r.result
  }
}


trait Show[T] {
  def apply() : String
}

object Show {
  implicit def show0[I <: Integer](implicit ev : ToInt[I]) = new Show[I]{def apply() = ev().toString}
  def show[A](a : A)(implicit ev : Show[A]) = ev()
}


trait ToInt[I <: Integer]{
  def apply() : Int
}

object ToInt {
  implicit val toInt0 = new ToInt[_0]{ def apply() = 0}
  implicit def toIntPos[N <: NonNegInt](implicit toInt: ToInt[N]) : ToInt[++[N]]
    = new ToInt[++[N]]{ def apply() = toInt() + 1}
  implicit def toIntNeg[N <: NonPosInt](implicit toInt: ToInt[N]) : ToInt[--[N]]
    = new ToInt[--[N]]{ def apply() = toInt() - 1}
}

