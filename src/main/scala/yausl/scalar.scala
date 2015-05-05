/*
 * Copyright 2015 Olivier Mélois
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yausl

import shapeless._

import scala.annotation.implicitNotFound

/**
 * Yausl uses this value-class to avoid boxing at runtime. It models the fact that a measure
 * is a value associated with a unit-system (U) and a list of dimensions in this system.
 * So T is a type-level List of Integers, of the same size as the list of units in the system,
 *
 * If U =:= meter :: second :: HNil, a speed value (meter.second^^-1) is a
 * Scalar[meter::second::HNil, 1 :: -1 :: HNil]
 */
class Scalar[U <: HList, T <: HList] protected[yausl](val value: Double) extends AnyVal {

  /**
   * Multiplies the scalar with a double
   */
  def *(d : Double) : Scalar[U, T] = new Scalar(value * d)

  /**
   * Divides the scalar with a double
   */
  def /(d : Double) : Scalar[U, T] = new Scalar(value / d)

  /**
   * Adds two scalars with the same dimensions.
   */
  def +(s: Scalar[U, T]): Scalar[U, T] = new Scalar(value + s.value)

  /**
   * Substracts two scalars with the same dimensions.
   */
  def -(s: Scalar[U, T]): Scalar[U, T] = new Scalar(value + s.value)

  /**
   * Multiplies two scalars with the same dimensions.
   */
  def *[T2 <: HList](s: Scalar[U, T2])(implicit product: Product[T, T2]): Scalar[U, product.result] =
    new Scalar[U, product.result](value * s.value)

  /**
   * Divides two scalars with the same dimensions.
   */
  def /[T2 <: HList](s: Scalar[U, T2])(implicit division: Division[T, T2]): Scalar[U, division.result] =
    new Scalar[U, division.result](value / s.value)

  /**
   * A "toString" method that relies on an instance generated at compile time.
   */
  def show(implicit s : Show[Scalar[U, T]]) = value + " " + s()
}

/**
 * Typeclass that allows to find the dimensions resulting from the multiplication of two values.
 */
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

/**
 * Typeclass that allows to find the dimensions resulting from the division of two values.
 */
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

/**
 * A quantity can represent something like quality, substance, change. It could be Length, Time, Mass ...
 */
trait BaseQuantity

/**
 * A unit of measurement is a definite magnitude of a physical quantity. In our case, of base quantity.
 */
trait UnitM[M <: BaseQuantity]


/**
 * Typeclass used by a system to give dimensions to a measure in a system of units.
 * For instance, if a System is meter::second::HNil, the dimensions of a time value in this system would be :
 * 0::1::HNil
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

/**
 * Typeclass that computes a list of Zeros from a unit list.
 */
trait Zeros[D <: HList]{
  type result <: HList
}

object Zeros {
  type Aux[D <: HList, Out <:HList] = Zeros[D]{type result = Out}

  implicit val inv0 : Aux[HNil, HNil] = new Zeros[HNil] {type result = HNil}
  implicit def inv1[H <: UnitM[_], T <: HList, R <: HList](implicit zeros : Zeros.Aux[T, R]) : Aux[H :: T, _0 :: R]
    = new Zeros[H :: T]{type result = _0 :: R}
}

