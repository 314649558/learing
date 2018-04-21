package kuaixuescala.ch6

object TrafficLightColor extends Enumeration {
  //val Red,Yellow,Green = Value

  val Red = Value(0,"stop")
  val Yellow = Value(5)
  val Green = Value("Go")
}

object TrafficLightColorDemo extends App{

  import TrafficLightColor._

  println(Red.id)
  println(TrafficLightColor.Yellow.id)
  println(TrafficLightColor.Green.id)

  println(TrafficLightColor(0))
  println(TrafficLightColor.withName("stop"))
}