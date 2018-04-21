package kuaixuescala.ch6
import java.util.{HashMap => JavaHashMap}
import java.util.{List => _,_}

class Account {




  val id=Account.newUniqueNumber()
  private var balance = 0.0

  def deposit(amount:Double): Unit = {
    balance += amount
  }
}


object Account{
  private var lastNumber = 0
  def newUniqueNumber() = { lastNumber +=1 ; lastNumber}


  def main(args: Array[String]): Unit = {


    val a=Array(100)
    val b=new Array(100)
    print(s"""${a.length},${b.length}""")


  }
}
