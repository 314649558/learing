package com.bigdata.spark.graphx

import org.apache.spark._
import org.apache.spark.graphx.{Edge, VertexId,Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession


object GraphDemo {
  def main(args: Array[String]): Unit = {
    val sparksession=SparkSession.builder()
      .appName("SparkSqlDemo")
      .master("local[2]")
      .config("spark.sql.sources.partitionColumnTypeInference.enabled",false)
      .getOrCreate()


    val sc=sparksession.sparkContext

    val users:RDD[(VertexId,(String,String))]=
      sc.parallelize(Array((3L,("rxin","student")),(7L,("hailong","worker")),(5L,("franklin","prof")),(2L,("istoica","prof"))))

    val relationships:RDD[Edge[String]]=
      sc.parallelize(Array(Edge(3L,7L,"collab"),Edge(5L,3L,"advisor"),Edge(2L,5L,"colleague"),Edge(5L,7L,"pi")))


    val defaultUser=("John Doe", "Missing")

    val graph=Graph(users,relationships,defaultUser)

    val v1=graph.vertices.filter{case (id,(name,pos))=>pos=="worker"}.count()
    val v2=graph.vertices.filter(v=>{
      v._2._2=="worker"
    }).count()




    val v3=graph.edges.filter(e=>e.srcId<e.dstId).count()

    println(v3)

    val facts:RDD[String]=
      graph.triplets.map(triplet=>triplet.srcAttr._1 + " is the " +triplet.attr +" of "+triplet.dstAttr._1)

    facts.collect.foreach(println(_))


    val validGraph = graph.subgraph(vpred=(id,attr)=>attr._2!="Missing")

    validGraph.vertices.collect.foreach(println(_))

    validGraph.triplets.map(triplet=>triplet.srcAttr._1 + " is the " + triplet.attr + " of " + triplet.dstAttr._1)
      .collect.foreach(println(_))




  }

}
