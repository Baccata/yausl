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

import shapeless.{::, HNil, HList}

/**
 * Simple typeclass giving a name/description/symbol to a type.
 */
trait Show[T] {
  def apply() : String
}

object Show {
  implicit def show0[I <: Integer](implicit ev : ToInt[I]) : Show[I] = new Show[I]{def apply() = ev().toString}
  implicit def show1[Units <: HList, Dims <: HList](implicit unitL : ToList[Units, String], dimL : ToList[Dims, Int])
  : Show[Scalar[Units, Dims]] = new Show[Scalar[Units, Dims]] {
    def apply(): String = (unitL() zip dimL())
      .filter(_._2 != 0)
      .map(x => x._1 + (if (x._2 == 1) "" else "^" + x._2)).mkString(".")
  }
  def apply[A](a : A)(implicit ev : Show[A]) = ev()
}

/**
 * Typeclass that unlifts a typelevel list to a list of something...
 */
trait ToList[L <: HList, Out]{
  def apply() : List[Out]
}

object ToList {
  import scala.reflect.runtime.universe._
  implicit def toList0[Out] : ToList[HNil, Out] = new ToList[HNil, Out] {def apply() = Nil}
  implicit def toList1[H <: Integer, T <: HList](implicit toInt : ToInt[H], toList : ToList[T, Int])
  : ToList[H :: T, Int] = new ToList[H :: T, Int]{def apply() = toInt() :: toList()}
  implicit def toList2[H <: UnitM[_], T <: HList](implicit tag : TypeTag[H], toList :ToList[T, String])
  : ToList[H :: T, String] = new ToList[H :: T, String]{def apply() = tag.tpe.typeSymbol.name.toString :: toList()}
}

/**
 * Unlifts a typelevel Integer to a value-level Int.
 */
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
