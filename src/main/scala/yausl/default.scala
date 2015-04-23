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
//  object gram extends UnitM[Mass]("kilogram", "kg")
//  object kelvin extends UnitM[Temperature]("kelvin", "K")
//  object ampere extends UnitM[Current]("ampere", "A")
//  object mole extends UnitM[AmountOfSubstance]("mole", "mol")
//  object candela extends UnitM[Intensity]("candela", "cd")


//  val hertz = UnitM[Frequency]("hertz","Hz")
//val celsius = UnitM[Temperature]("celsius", "°C")
//  val metrePerSecond = UnitM[Speed]("metre per second", "m/s")
//  val metrePerSecondSquared = UnitM[Acceleration]("metre per second squared", "m/s^2")
//  val newton = UnitM[Force]("newton","N")
//  val newtonMetre = newton * metre
//  val pascal = UnitM[Pressure]("pascal","Pa")
//  val joule = UnitM[Energy]("joule","J")
//  val watt = UnitM[Power]("watt","W")
//  val coulomb = UnitM[Charge]("coulomb","C")
//  val volt = UnitM[Potential]("volt","V")
//  val farad = UnitM[Capacitance]("farad","F")
//  val ohm = UnitM[Resistance]("ohm","Ω")
//  val siemens = UnitM[Conductance]("siemens","S")
//  val weber = UnitM[Flux]("weber","Wb")
//  val tesla = UnitM[FieldStrength]("tesla","T")
//  val henry = UnitM[Inductance]("henry","H")
//  val lumen = UnitM[Intensity]("lumen","lm")
//  val lux = UnitM[Illuminance]("lux","lx")
//  val becquerel = UnitM[Decay]("becquerel","Bq")
//  val gray = UnitM[Dose]("gray","Gy")
//  val sievert = UnitM[Dose]("sievert","Sv")
//  val katal = UnitM[CatalyticActivity]("katal","kat")
//  val jouleSecond = UnitM[AngularMomentum]("Joule-second","J*s")
//  val squareMetre = UnitM[Area]("square metre", "m^2")
//  val cubicMetre = UnitM[Volume]("cubic metre", "m^3")
}