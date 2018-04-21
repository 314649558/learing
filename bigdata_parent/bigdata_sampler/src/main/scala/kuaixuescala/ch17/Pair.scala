package kuaixuescala.ch17


//<: 上界  表示   T <：R  表示T 必须是R的子类
//<% 视图界定： T<%R T 可以被隐式转换为R  比如 Int 转换为RichInt
class Pair[T <% Ordered[T]](val first:T , val second:T) {
  def smaller=if (first < second) first else second
}


object Pair{
  def main(args: Array[String]): Unit = {
    val pair=new Pair[String]("Books","Hadoop")
    println(pair.smaller)

    val pair1=new Pair[Int](1,2)

    println(pair1.smaller)
  }
}
