/**
 * Copyright (C) 2015  Phasmid Software
 * Created on: Feb 24, 2015
 */

import org.apache.spark.{ graphx, SparkContext }
import org.scalatest.{ FunSuite, BeforeAndAfter }

class HealthGridTest extends FunSuite with BeforeAndAfter {

  var context: SparkContext = null

  before {
    context = new SparkContext(HealthGrid.createConf("GraphSearch", "local").set("spark.driver.allowMultipleContexts", "true"))
  }

  after {
    context.stop
  }

  test("Load Dataset Conditions") {
    val healthGrid = new HealthGrid(context, new DatasetLibrary, new GraphLibrary)
    val conditions = new Conditions("Conditions-short.csv")
    val result = healthGrid.loadDataset("conditions", conditions)
    assert(result)
    healthGrid.close
  }

  test("Load Dataset Patients") {
    val healthGrid = new HealthGrid(context, new DatasetLibrary, new GraphLibrary)
    val patients = new Patients("Patients-short.csv")
    val result = healthGrid.loadDataset("patients", patients)
    assert(result)
    healthGrid.close
  }

  test("Load Graph Patients with Conditions") {
    val nPatients = 100
    val nConditions = 33
    val nProviders = 36
    val healthGrid = new HealthGrid(context, new DatasetLibrary, new GraphLibrary)
    assert(healthGrid.loadDataset("patients", new Patients("Patients-short.csv")))
    assert(healthGrid.loadDataset("conditions", new Conditions("Conditions-short.csv")))
    val patients = healthGrid.getGraph("patients")
    val conditions = healthGrid.getGraph("conditions")
    val pGraph = patients.get.graph.graph
    val cGraph = conditions.get.graph.graph
    val xGraph = pGraph.joinVertices(cGraph.vertices) { (x, y, z) => z }
    assert(xGraph.vertices.count == nPatients + nConditions + nProviders)
    assert(xGraph.edges.count == nPatients * (8 + 1))
    healthGrid.close
  }

  test("Join Graphs Patients with Conditions") {
    val nPatients = 100
    val nConditions = 33
    val nProviders = 36
    val healthGrid = new HealthGrid(context, new DatasetLibrary, new GraphLibrary)
    assert(healthGrid.loadDataset("patients", new Patients("Patients-short.csv")))
    assert(healthGrid.loadDataset("conditions", new Conditions("Conditions-short.csv")))
    val name = healthGrid.addJoinGraph("patients", "conditions")
    assert(name equals "patients_conditions")
    val joined = healthGrid.getGraph("patients_conditions").getOrElse(null)
    assert(joined != null)
    val xGraph = joined.getGraph.graph
    assert(xGraph.vertices.count == nPatients + nConditions + nProviders)
    assert(xGraph.edges.count == nPatients * (8 + 1))
    healthGrid.close
  }

  test("Join Graphs Patients with Conditions with Providers") {
    val nPatients = 100
    val nConditions = 33
    val nProviders = 36
    val healthGrid = new HealthGrid(context, new DatasetLibrary, new GraphLibrary)
    assert(healthGrid.loadDataset("patients", new Patients("Patients-short.csv")))
    assert(healthGrid.loadDataset("conditions", new Conditions("Conditions-short.csv")))
    assert(healthGrid.loadDataset("providers", new Providers("Providers.csv")))
    val name = healthGrid.addJoinGraphs(List("patients", "conditions", "providers"))
    assert(name equals "patients_conditions_providers")
    val joined = healthGrid.getGraph("patients_conditions").getOrElse(null)
    assert(joined != null)
    val all_joined = healthGrid.getGraph("patients_conditions_providers").getOrElse(null)
    assert(all_joined != null)
    val xGraph = all_joined.getGraph.graph
    assert(xGraph.vertices.count == nPatients + nConditions + nProviders)
    assert(xGraph.edges.count == nPatients * (8 + 1))

    all_joined.graph.graph.vertices.foreach(_ match {
      case (id, doc) => println(s"id: $id\tdoc: $doc")
      case other => println(other)
    })
    val provider = all_joined.getGraph.getVertex(1053319400)
    provider match {
      case None => fail()
      case Some(p) => assert(p.map != null)
    }
    println(provider.get.toString())
    healthGrid.close
  }

}
object HealthGridTest {
  import org.apache.spark.rdd.RDD
  import org.apache.spark.graphx._
  // Implementing a function statically allows us to pass it to Spark -- otherwise it has to be part of a closure.
  // Otherwise we get the following exception:
  // org.apache.spark.SparkException: Job aborted due to stage failure: Task not serializable: java.io.NotSerializableException: ...
  def mapper(x: VertexId, y: Document[String, String], z: Document[String, String]): Document[String, String] = {
    println("mapper: " + x + "; " + y + "; " + z)
    z
  }

}
