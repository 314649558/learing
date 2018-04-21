package com.bigdata.spark.graphx

import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.sql.SparkSession
import org.apache.spark.graphx.{Graph, VertexRDD}

object AggregateMessagesExample {

  def main(args: Array[String]): Unit = {


    val spark = SparkSession
      .builder
        .master("local[2]")
      .appName(s"${this.getClass.getSimpleName}")
      .getOrCreate()


    val sc=spark.sparkContext

    val graph:Graph[Double, Int]=
      GraphGenerators.logNormalGraph(sc, numVertices = 100)
                                  .mapVertices((id,_)=>id.toDouble)

    val olderFollowers: VertexRDD[(Int,Double)] = graph.aggregateMessages[(Int,Double)](
      triplt=>{
        if(triplt.srcAttr > triplt.dstAttr){
          triplt.sendToDst((1,triplt.srcAttr))
        }
      },
      (a,b)=>(a._1+b._1,a._2+b._2)
    )

    val avgAgeOfOlderFollowers: VertexRDD[Double]=
      olderFollowers.mapValues((id,value)=>{
        value match{case (count,totalAge) => totalAge/count}
      })

    avgAgeOfOlderFollowers.collect.foreach(println(_))

    spark.stop()




  }

}
