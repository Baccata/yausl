import shapeless.{HNil, ::}
import yausl._


object Main {

  def main (args: Array[String]) {
    import yausl.default._

    val system = SystemGenerator.fromHList[metre :: second :: HNil]
    import system._

    val a = 3.metre
    val b = 5.metre
    val c = a + b
    val d = 5.second
    //val e = a + c // fails to compile, you can't add metres and seconds
    val f = 5.metre / 5.second // computes a speed (length / time) at compile time
    val h = 5.metre * 5.metre // computes a surface (metre * metre) at compile time

    val i = (32 metre) / (16 second)
    println(i.show)




  }

}
