package kuaixuescala.ch10

object Name {
    def main(args: Array[String]): Unit = {

      val f=Fraction(10,20)

      val Fraction(nn,dd)=f

      println(nn)


      val pf: PartialFunction[Char,Int] ={
        case '+' =>1
        case '-' => -1
      }


      println(pf('-'))

      println(pf.isDefinedAt('+'))



  }
}
