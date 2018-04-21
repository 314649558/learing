package com.bigdata.spark

import org.apache.spark.sql.SparkSession

object ParquetMerge {

  def main(args: Array[String]): Unit = {

    val spark=SparkSession.builder()
      .appName("SparkSqlDemo")
      .master("local[1]")
      .config("spark.sql.parquet.mergeSchema",true)//默认为False  Parquet合并操作是昂贵的，谨慎使用。
      .getOrCreate()

    import spark.implicits._

    // Create a simple DataFrame, store into a partition directory
    val squaresDF = spark.sparkContext.makeRDD(1 to 5).map(i => (i, i * i)).toDF("value", "square")
    squaresDF.write.parquet("data/test_table/key=1")

    squaresDF.show()

    // Create another DataFrame in a new partition directory,
    // adding a new column and dropping an existing column
    val cubesDF = spark.sparkContext.makeRDD(6 to 10).map(i => (i, i * i * i)).toDF("value", "cube")
    cubesDF.write.parquet("data/test_table/key=2")

    squaresDF.show()

    // Read the partitioned table
    val mergedDF = spark.read.option("mergeSchema", "true").parquet("data/test_table")
    mergedDF.show()
    mergedDF.printSchema()

  }

}
