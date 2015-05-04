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

/**
 * Typelevel representation of booleans.
 *
 * Copied from Grant Beaty's Scunits
 */
trait Bool {
  type branch[B,T <: B, E <: B] <: B // typelevel if-then-else
  type not <: Bool
  type or[R <: Bool] <: Bool
  type and[R <: Bool] <: Bool
  type Xor[R <: Bool] <: Bool
}
/**
 * Implementation of the typelevel true.
 */
trait True extends Bool {
  type not = False
  type branch[B,T <: B, E <: B] = T
  type or[R <: Bool] = True
  type and[R <: Bool] = R
  type Xor[R <: Bool] = R#not
}
/**
 * Implementation of the typelevel false.
 */
trait False extends Bool {
  type not = True
  type branch[B,T <: B, E <: B] = E
  type or[R <: Bool] = R
  type and[R <: Bool] = False
  type Xor[R <: Bool] = R
}

/**
 * Typelevel representation of integers, with several arithmetic operators. These allows to write
 * things like "p1 + p3" in type parameters, which is imho more readable than an implementation through
 * implicits
 *
 * Inspired from Grant Beaty's Scunits
 */
sealed trait Integer {
  type N <: Integer
  type isZero <: Bool
  type isPos <: Bool
  type isNeg <: Bool
  type abs <: NonNegInt
  type sameSign[R <: Integer] = (isPos && R#isPos) || (isNeg && R#isNeg)

  type succ <: Integer
  type pred <: Integer

  type add[N <: Integer] <: Integer
  type sub[N <: Integer] <: Integer
  type prod[N <: Integer] <: Integer
  type div[N <: NonZeroInt] <: Integer

  type neg <: Integer

  type loop[B,F[_ <: B] <: B, Res <: B] <: B

  type lt[N <: Integer] = sub[N]#isNeg

  type lteq[N <: Integer] = ({
    type minus = sub[N]
    type res = minus#isNeg || minus#isZero
  })#res

  type eq[N <: Integer] = sub[N]#isZero
}

/**
 * Refinement of integers that matches the [0, +infinity[ subset.
 */
sealed trait NonNegInt extends Integer {
  type N <: NonNegInt
  type abs = N
  type isNeg = False
  type succ <: PosInt
  type neg <: NonPosInt
  type predNat <: NonNegInt
  type addNat[R <: NonNegInt] <: NonNegInt
  type subNat[R <: NonNegInt] <: NonNegInt
  type divNat[D <: PosInt] <: NonNegInt
}
/**
 * Refinement of integers that matches the ]-infinity, 0] subset.
 */
sealed trait NonPosInt extends Integer {
  type N <: NonPosInt
  type isPos = False
  type pred <: NegInt
  type neg <: NonNegInt
  type loop[B,F[_ <: B] <: B, Res <: B] = Res
}

/**
 * Refinement of integers that matches the ]-infinity, 0[ U ]0, + infinity[ subset.
 */
sealed trait NonZeroInt extends Integer {
  type N <: NonZeroInt
  type isZero = False
  type abs <: PosInt
  type div[R <: NonZeroInt] = ({
    type aRes = abs#divNat[R#abs]
    type result = sameSign[R]#branch[Integer, aRes, aRes#neg]
  })#result
}

/**
 * Refinement of integers that matches the ]-infinity, 0[
 */
sealed trait NegInt extends NonPosInt with NonZeroInt {
  type N <: NegInt
  type isNeg = True
  type neg <: PosInt
  type abs = neg
  type succ <: NonPosInt
  type pred <: NegInt
}
/**
 * Refinement of integers that matches the ]0, +infinity[
 */
sealed trait PosInt extends NonNegInt with NonZeroInt {
  type N <: PosInt
  type isPos = True
  type neg <: NegInt
  type pred <: NonNegInt
  type succ <: PosInt
  type loop[B,F[_ <: B] <: B, Res <: B] = pred#loop[B,F,F[Res]]
}

/**
 * Typelevel implementation of positive integers : each of them is either a successor of Zero or
 * a successor of another positive integer.
 */
case class ++[P <: NonNegInt]() extends PosInt {
  type N = ++[P]
  type succ = ++[++[P]]
  type add[R <: Integer] = P#add[R#succ]
  type pred = P
  type sub[R <: Integer] = P#sub[R#pred]
  type prod[R <: Integer] = P#prod[R]#add[R]
  type neg = --[P#neg]
  type predNat = P
  type addNat[R <: NonNegInt] = P#addNat[R#succ]
  type subNat[R <: NonNegInt] = (R#isZero)#branch[NonNegInt, N, P#subNat[R#predNat]]
  type divNat[D <: PosInt] = lt[D]#branch[NonNegInt, _0, p1#addNat[subNat[D]#divNat[D]]]
}

/**
 * Typelevel implementation of negative integers : each of them is either a predecessor of Zero or
 * a predecessor of another negative integer.
 */
case class --[S <: NonPosInt]() extends NegInt {
  type N = --[S]
  type succ = S
  type add[N <: Integer] = S#add[N#pred]
  type pred = --[--[S]]
  type sub[N <: Integer] = S#sub[N#succ]
  type prod[N <: Integer] = (neg#prod[N])#neg
  type neg = ++[S#neg]
}

/**
 * Typelevel implementation of Zero.
 */
class _0 extends NonNegInt with NonPosInt {
  type N = _0
  type isZero = True
  type succ = ++[_0]
  type add[R <: Integer] = R
  type addNat[R <: NonNegInt] = R
  type subNat[R <: NonNegInt] = _0
  type divNat[R <: PosInt] = _0
  type pred = --[_0]
  type predNat = _0
  type sub[R <: Integer] = R#neg
  type prod[R <: Integer] = _0
  type div[R <: NonZeroInt] = _0
  type neg = _0
}
