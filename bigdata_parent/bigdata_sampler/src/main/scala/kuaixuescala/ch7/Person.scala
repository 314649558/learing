package kuaixuescala.ch7

abstract class Person {
  val id:Int
  val name:String

  def add:Int

  def say:Unit={
    println("Person")
  }

}
