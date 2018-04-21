package com.bigdata.spark

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

case class People(age:Int, name:String)

object DataFrameDemo {



  def main(args: Array[String]): Unit = {


    val sparksession=SparkSession.builder()
                                  .appName("SparkSqlDemo")
                                  .master("local[3]")
                                  .config("spark.sql.sources.partitionColumnTypeInference.enabled",false)
                                  .getOrCreate()




    dfJson(sparksession)


  }

  def dfJson(sparksession:SparkSession): Unit ={
    val df1=sparksession.read/*.option("inferSchema",true)*/.json("bigdata_spark/src/main/resources/people.json")
    val df2=sparksession.read/*.option("inferSchema",true)*/.json("bigdata_spark/src/main/resources/people.json")
    import sparksession.implicits._
   /* df.createOrReplaceTempView("people")
    val sqlDF=sparksession.sql("select name from people")
    sqlDF.show()
    df.show()
    df.dropDuplicates(Array("name","age")).show()



   // df.withColumn("age",df("age")).as[People]]

    val ds=df.withColumn("age",'age.cast("int") ).as[People]

    ds.filter(_.age>20)/*.select($"name")*/.selectExpr("cast (name as string) as name").show()*/
    df1.createOrReplaceTempView("people1")
    df2.createOrReplaceTempView("people2")


    sparksession.sql("select count(1),p1.name as name1 from people1 as p1 inner join people2 p2 on (p1.name=p2.name) group by p1.name").show()






  }


  def dfTxt(sparksession: SparkSession):Unit={
    val df=sparksession.sparkContext.textFile("bigdata_spark/src/main/resources/people.txt")

    val schemaString="name age"

    val fields=schemaString.split(" ")
        .map(fieldName => StructField(fieldName,StringType,nullable = true))

    val schema=StructType(fields)

    val rowRDD=df.map(_.split(",")).map(attrubutes=>org.apache.spark.sql.Row(attrubutes(0),attrubutes(1).trim.toInt))
    sparksession.createDataFrame(rowRDD,schema).createOrReplaceTempView("people")
    sparksession.sql("select * from people").show()
  }


  def runBasicDataSourceExample(spark: SparkSession): Unit ={
    //val usersDF = spark.read.load("bigdata_spark/src/main/resources/users.parquet")
    //usersDF.write.partitionBy("name").save("namesAndFavColors.parquet")


    val p= spark.read.load("bigdata_spark/src/main/resources/namesAndFavColors.parquet")

    p.select("name").show()


    /*val peopleDF= spark.read.format("json").load("bigdata_spark/src/main/resources/people.json")



    peopleDF.write.format("json")/*.bucketBy(42,"name").sortBy("age")*/.mode(SaveMode.Overwrite).save("D:\\people_bucketed")*/

  }




}
