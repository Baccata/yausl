
# Yausl : Yet Another Unit System Library

The goal of this library is to provide a way to generically create **Systems of Units** such as the 
[International System of Units](http://en.wikipedia.org/wiki/International_System_of_Units), with as little
boilerplate as possible. The types originating from these systems are checked at compile time when
performing operations such as additions and multiplications, and the values of these types should have 
performances similar to unboxed doubles, thanks to [value classes](http://docs.scala-lang.org/overviews/core/value-classes.html). 

**THIS LIBRARY IS EXPERIMENTAL, USE IT AT YOUR OWN RISK**

This library was partly inspired from Grant Beaty's [Scunits](https://github.com/gbeaty/scunits), from which I 
got some class names and the implementation for typelevel integers based on type-projections (though I enriched it 
for my own knowledge). What differs from it is mainly the fact that I wanted to write things such as 
*1.metre / 1.second* without having to manually write these extension methods, 
and the fact that I didn't care so much about (not) using implicits for type-level computations. 

## Usage 

Yausl is built using SBT and published on the oss sonatype repositories. To use it, 
The following dependencies should be added to your build (see the double %% that differentiates 
maven-built artifacts from SBT-built artifacts).

    resolvers += Resolver.sonatypeRepo("snapshots")
    
    libraryDependencies += "com.github.baccata" %% "yausl" % "0.1.0-SNAPSHOT"


## Example code 


```scala 
import yausl._ 
import shapeless._

trait Length extends Quantity //defining a quantity
trait Time extends Quantity
trait metre extends UnitM[Length] //defining a unit for this quantity
trait second extends UnitM[Time]

val system = SystemGenerator.fromHList[metre :: second :: HNil] //generating a system of units
import system._ // importing extension methods generated during the system creation. 

// The pX type is the "type-level positive integer of value X" and 
// nX is the "type-level" negative integer of value -X"

val a = 5 metre // compiles, creates an instance of Scalar[metre :: second :: HNil, p1 :: _0 :: HNil]
val b = 3 second // compiles, creates an instance of Scalar[metre :: second :: HNil, _0 :: p1 :: HNil]
val c = (5 metre) / (3 second) // compiles, creates an instance of 
                               //Scalar[metre :: second :: HNil, p1 :: n1 :: HNil]  
val d = (8 metre) + (2 metre) // compiles
// val e = c + d // does not compile as you cannot sum m et m.s^-1

val f = measure[metre](3) // scalars can also be initialized like this.  

val g = 1.scalar / 4.second // compiles, creates an instance of s^-1 Scalar

println(a.show) //prints "8.0 metre.second^-1" 
```


## Requirements 

When this library was imagined, a certain number of requirements were conceptualized. As the code evolves, 
I will keep a formal list of requirements and provide a test for each of them. However, some tests 
are hard to be written, as performing them means checking whether a piece of code compiles. 


The usability requirements are the following : 


Requirements ID   | Description
------------------| ----------------------------------------------------------------
REQ_YAUSL_USE_001 | systems of units should be creatable from a list of types (HList)                        
REQ_YAUSL_USE_002 | "natural constructors" must be generated to allow a very natural writing style 
REQ_YAUSL_USE_003 | summing/substracting values of different dimensions must not compile                
REQ_YAUSL_USE_004 | summing/substracting values of equal dimensions must compile and work               
REQ_YAUSL_USE_005 | multiplication should be commutative(type-wise and value-wise)
REQ_YAUSL_USE_006 | values should be printable with their types (for debugging purposes)
REQ_YAUSL_USE_007 | Scalar multiplicative operators should work with doubles                 
REQ_YAUSL_USE_008 | doubles should be usable in prefix position of scalar operators, through conversion to Scalar          


The performance requirements are the following : 


Requirements ID    | Description
-------------------|-----------------------------------------------------------------
REQ_YAUSL_PERF_001 | performances of yausl Scalar instances should be similar to the ones of unboxed doubles  
              
## Implementation

The implementation relies on implicit-expansion based type-level programming 
Basically, what happens is that a value (yausl.Scalar) is accompanied at compile 
time with a type-level list (shapeless.HList) of Units (yausl.UnitM) and a typelevel list of integers that represents, 
for each unit, a associated dimension. Therefore, a Scalar[metre::second::HNil, 1 :: -1 :: HNil] is a speed value. 

I really wanted the system to be as generic as possible and did not make assumption on how this should be used, 
so you can have values that are m^-3, which does not physically represent anything ... 

The automatic creation of "natural constructors" (when writing "1 meter" for instance) is performed using a 
type generator macro that uses vampire methods. Calling the macro 

    SystemGenerator.fromHList[metre::second::HNil]
    
creates a System instance with an embedded implicit extension that adds the methods "metre" and "second" to the 
Double primitive type, each of them instantiating a Scalar of their respective type representation within the
system. 

## Limitations / TODOs 

There is a number of things you would probably like to do with yausl which have not yet been implemented : 

- Since we use fancy things such as type-level programming and whitebox, macros, your ide will probably show 
false positives when looking for type errors.
- Initializing values with composite types : you cannot currently write "1 metre / second", you have 
to write "(1 metre) / (1 second)" or "1.metre / 1.second". 
- Values (yausl.Scalar) from different systems cannot be summed, even if they represent the same quantities.
- Speaking of quantities : right now my quantities are absolutely useless. 
- Values are instances of the yausl.Scalar value class, and as such cannot be used inside a usual Collection, 
I will provide a custom implementation of Array at some point that will deal with this problem.  
- Multiplying/dividing/showing values requires some typelevel computation that will increase your 
compile time. I personally don' mind, the compiler works for me and the end user should see any performance problem.
- If you want your units to have symbols, you should create your own instance of the yausl.Show typeclass. 

## Thank you note 

I'd like to thank Miles Sabin and the guys behind the [Shapeless](https://github.com/milessabin/shapeless)
library (although yausl doesn't use a lot of Shapeless, their CaseClassMacros trait was very useful) 
and the team that works to make macros writable/readable by humans, 
especially Eugene Burmako and Travis Brown for their [type providers examples](https://github.com/travisbrown/type-provider-examples)

## Finally 

On a personal note, I'm currently looking for a job and am ready to move anywhere. 
If you are in need of a young, eager to learn, Scala developer who's ready to fight boilerplate through 
type-level programming and macros, please ask me for a CV. 

