package kuaixuescala.ch10

class Fraction(val n:Int,val d:Int) {



}


object Fraction{
  def apply(n:Int,d:Int) = new Fraction(n,d)

  def unapply(fraction: Fraction): Option[(Int, Int)] =
    if (fraction.d == 0) None else Some((fraction.n+10,fraction.d+10))
}


