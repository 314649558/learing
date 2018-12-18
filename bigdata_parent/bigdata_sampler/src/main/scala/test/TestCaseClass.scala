package test

/**
  * Created by Administrator on 2018/12/9.
  */
case class TestCaseClass(name:String) {
  def sayHello():Unit={
    println(s"Hello ${name}")
  }
}


object TestCaseClassObj{
  def main(args: Array[String]): Unit = {
    new TestCaseClass("Hailong").sayHello()
  }


}
