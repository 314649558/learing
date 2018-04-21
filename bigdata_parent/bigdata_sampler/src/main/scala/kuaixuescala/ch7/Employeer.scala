package kuaixuescala.ch7

class Employeer extends Person {
   val id: Int = 1
   val name: String = "dong"

   def add: Int = {
      1
   }

  //如果不是抽象方法和字段的话 子类要重写方法必须使用override 关键字
  override def say: Unit = {

  }






}
