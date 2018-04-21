package com.bigdata.spark

import org.apache.spark.Aggregator
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

class MyAverage extends UserDefinedAggregateFunction{

  override def inputSchema: StructType = StructType(StructField("inputColumn", LongType) :: Nil)

  override def bufferSchema: StructType =  StructType(StructField("sum", LongType) :: StructField("count", LongType) :: Nil)

  override def dataType: DataType = DoubleType

  override def deterministic: Boolean = true

  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0L
    buffer(1) = 0L
  }


  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {

    if(!input.isNullAt(0)){
      buffer(0)=buffer.getLong(0)+input.getLong(0)
      buffer(1)=buffer.getLong(1)+1
    }

  }

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)
  }

  override def evaluate(buffer: Row): Any = buffer.getLong(0).toDouble / buffer.getLong(1)

}




object TestMyAverage{
  def main(args: Array[String]): Unit = {


    val spark=SparkSession.builder()
      .appName("udf")
      .master("local[3]")
      .getOrCreate()
    spark.udf.register("myAverage",new MyAverage())
    val df=spark.read.json("D:\\workspace\\idea\\bigdata_parent\\bigdata_spark\\src\\main\\resources\\employees.json")
    df.createOrReplaceTempView("employees")
    spark.sql("select myAverage(salary) from employees").show()



  }
}
