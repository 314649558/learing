package kuaixuescala.ch17

class Pair1[T:Ordering](val first:T,val second:T){
  def smaller(implicit ord:Ordering[T]): T ={
    if (ord.compare(first,second)<0) first else second
  }


  def makePair[R:Manifest](first:R,second:R): Array[R] ={
    val r=new Array[R](2)
    r(0)=first
    r(1)=second
    r
  }
}

object Pair1{
  def main(args: Array[String]): Unit = {

    val p=new Pair1[String]("Spark","Hadoop")

    println(p.smaller)

    val d=p.makePair(1,2)
    println(d.length)



  }
}
