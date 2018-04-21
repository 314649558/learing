package kuaixuescala.ch21

import java.io.File

object FileDemo  {

  def main(args: Array[String]): Unit = {

    val file=new File("")

    implicit def file2RichFile(from:File) = new RichFile(from)


  }

}
