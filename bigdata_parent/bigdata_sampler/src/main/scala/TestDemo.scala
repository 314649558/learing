object TestDemo {

  def main(args: Array[String]): Unit = {


    val arr:Array[Int]=Array(1,2,3,4,5)


    val r=arr.reduce(_ + _)

    println(r)

  }

}
