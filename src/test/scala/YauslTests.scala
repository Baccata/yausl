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

import java.util.Calendar

import org.specs2._
import org.specs2.matcher.{Expectable, Matcher}
import shapeless._
import shapeless.test.illTyped
import yausl.default.{second, metre}

/**
 * Typeclass that checks the type equality of two types and that unlifts the result
 * to a runtime value.
 */
trait TypeEqualityCheck[A, B]{
  def check : Boolean
}
object TypeEqualityCheck {
  def apply[A, B](a : A, b : B)(implicit test : TypeEqualityCheck[A, B]) = test.check

  implicit def tec0[A, B](implicit ev : A =:= B)
  : TypeEqualityCheck[A, B] = new TypeEqualityCheck[A, B]{def check = true}

  implicit def tec1[A, B](implicit ev : A =:!= B)
  : TypeEqualityCheck[A, B] = new TypeEqualityCheck[A, B]{def check = false}
}

class YauslUsabilitySpec extends Specification {

  import yausl.default._
  import scala.language.reflectiveCalls

  def is = s2"""

    The usability requirements for yausl are the following :
      REQ_YAUSL_USE_001 : systems of units should be creatable from a HList                        $e1
      REQ_YAUSL_USE_002 : "natural constructors" should be derived from the HList                  $e2
      REQ_YAUSL_USE_003 : summing values of different dimensions should not compile                $e3
      REQ_YAUSL_USE_004 : summing values of equal dimensions should compile and work               $e4
      REQ_YAUSL_USE_005 : multiplication should be commutative(type-wise and value-wise)           $e5
      REQ_YAUSL_USE_006 : values should be printable with their types                              $e6
      REQ_YAUSL_USE_007 : Scalar multiplicative operators should work with doubles                 $e7
      REQ_YAUSL_USE_008 : doubles should be usable in prefix position of scalar operators          $e8
  """

  val system = SystemGenerator.fromHList[metre :: second :: HNil]
  import system._

  def e1 = system must not be_==null

  def e2 = {
    TypeEqualityCheck(1 metre, 1 second) must beFalse
  }

  def e3 = {
    illTyped("(1 metre) + (1 second)")
    ok
  }

  val a = (5 metre) * (3 second)
  val b = (3 second) * (5 metre)

  def e4 = {
    (a + b).value must be equalTo(30)
  }

  def e5 = {
    TypeEqualityCheck(a, b) must beTrue
  } and {
    a.value must be equalTo(15)
  } and {
    b.value must be equalTo(15)
  }

  def e6 = {
    ((1 metre) / (1 second)).show must be equalTo "1.0 metre.second^-1"
  }

  def e7 = {
    ((1 metre) * 2) must be equalTo (2.scalar * (1 metre))
  } and {
    ((3 metre) / 2) must be equalTo (1.5 metre)
  }

  def e8 = {
    ((1 second) / (1 metre)) must be equalTo (1.scalar / ((1 metre) / (1 second)))
  }
}

class YauslPerformanceSpec extends Specification {
  import scala.language.reflectiveCalls

  def is = s2"""

    The runtime performance requirements for yausl are the following :
      REQ_YAUSL_PERF_001 : performances of yausl Scalar instances should be similar
      to the ones of unboxed doubles  $e1
  """

  val system = SystemGenerator.fromHList[metre :: second :: HNil]
  import system._

  val a = 1.metre
  var varA = a
  val b : Double = 1
  var varB = b

  val scalarThen = Calendar.getInstance().getTime().getTime
  for (x <- 1 to 1000000000){varA = varA + a}
  var scalarNow = Calendar.getInstance().getTime().getTime

  val doubleThen = Calendar.getInstance().getTime().getTime
  for (x <- 1 to 1000000000){varB = varB + b}
  val doubleNow = Calendar.getInstance().getTime().getTime

  val ratio : Double = ((scalarNow - scalarThen) / (doubleNow - doubleThen))
  val finalValue = varA.value

  def e1 = {ratio should beCloseTo(1, 0.1)} and {finalValue should beEqualTo(varB)}
}