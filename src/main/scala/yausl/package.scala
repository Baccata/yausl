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

package object yausl {

  type +[L <: Integer, R <: Integer] = L#add[R]
  type -[L <: Integer, R <: Integer] = L#sub[R]
  type *[L <: Integer, R <: Integer] = L#prod[R]
  type /[L <: Integer, R <: NonZeroInt] = L#div[R]
  type <[L <: Integer, R <: Integer] = L#lt[R]
  type <=[L <: Integer, R <: Integer] = L#lteq[R]
  type >[L <: Integer, R <: Integer] = R#lt[L]
  type >=[L <: Integer, R <: Integer] = R#lteq[L]
  type ==[L <: Integer, R <: Integer] = L#eq[R]
  type min[L <: Integer, R <: Integer] = (L < R)#branch[Integer, L, R]
  type minp[L <: NonNegInt, R <: NonNegInt] = (L < R)#branch[NonNegInt, L, R]

  type p1 = _0#succ
  type p2 = p1#succ
  type p3 = p2#succ
  type p4 = p3#succ
  type p5 = p4#succ
  type p6 = p5#succ
  type p7 = p6#succ
  type p8 = p7#succ
  type p9 = p8#succ

  type n1 = _0#pred
  type n2 = n1#pred
  type n3 = n2#pred
  type n4 = n3#pred
  type n5 = n4#pred
  type n6 = n5#pred
  type n7 = n6#pred
  type n8 = n7#pred
  type n9 = n8#pred

  // BoolOps:
  type ||[L <: Bool, R <: Bool] = L#or[R]
  type &&[L <: Bool, R <: Bool] = L#and[R]

}
