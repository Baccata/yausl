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

object default {
  trait Time extends BaseQuantity
  trait Length extends BaseQuantity
  trait Mass extends BaseQuantity
  trait Temperature extends BaseQuantity
  trait AmountOfSubstance extends BaseQuantity
  trait Current extends BaseQuantity
  trait Intensity extends BaseQuantity

  trait metre extends UnitM[Length]
  trait second extends UnitM[Time]
  trait kilogram extends  UnitM[Mass]
  trait kelvin extends  UnitM[Temperature]
  trait mole extends  UnitM[AmountOfSubstance]
  trait ampere extends  UnitM[Current]
  trait candela extends  UnitM[Intensity]
}
