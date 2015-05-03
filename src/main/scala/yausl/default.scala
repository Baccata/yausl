package yausl

import shapeless.{HNil, ::}

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
}