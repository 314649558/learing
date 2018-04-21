package kuaixuescala.ch18

import scala.collection.mutable.ArrayBuffer

class Network {
  class Member(val name: String){
    val contacts = new ArrayBuffer[Network#Member]()
  }

  private val members = new ArrayBuffer[Member]()


  def join(name:String)={
    val m=new Member(name)
    members+=m
    m
  }
}


object Network extends App {
  val a=new Network
  val b=new Network

  val m1=a.join("m1")
  val m2=b.join("m2")

  m1.contacts += m2

}
