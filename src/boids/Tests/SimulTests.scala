package boids.Tests
import scala.util.Random
import org.scalatest._
import boids.simulation._
import boids.math._


import scala.util.Random
class SimulTests extends FlatSpec with Matchers {
  print("Tests beginz!")
  val r = new Random()
  val topSpeed = 150
  "alignmentSteerVector" should "be perpendicular to velocity" in {
    
    val n = 100
    val boids = Array.ofDim[Boid](n)
    
    for(i <- 0 until n){
    
      val position = new Vector(r.nextDouble() * 100, r.nextDouble()* 100)
      val velocity = new Vector(r.nextDouble() * 30 - 10, r.nextDouble() * 30 - 10)
     
      boids(i) = new Boid(position, velocity, topSpeed, 1.0)
    }
    val alignmentSteer = Boid.alignmentSteerVector(boids(0), boids)
    
    //dot product should be 0
    assert( alignmentSteer * boids(0).velocity === 0.0 +- 0.0001)
    
  }
  
  "steerAwayFromObstacle" should "be perpendicular to velocity" in {
    val obstacles = Array( new Obstacle(new Vector(0, 100)))
    
    val boid = new Boid( new Vector(0,0), new Vector(1,50), topSpeed, 1.0)
    val avoidanceSteerVector = Boid.steerAwayFromObstacles(boid, obstacles)
    
    assert( avoidanceSteerVector._1 * boid.velocity === 0.0 +- 0.0001 )
    
  }
  
  
}