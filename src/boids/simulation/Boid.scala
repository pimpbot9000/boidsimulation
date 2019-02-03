package boids.simulation

import boids.math.{Coordinate, Vector}
import scala.util.Random

/**
 * A class to handle the movement of an individual boid
 * 
 * @constructor create a new boid
 * @param position the initial position
 * @param velocity the initial velocity
 * @param topSpeed the top speed of a boid in px/s
 * @mass mass the mass of a boid
 */

class Boid(var position: Vector, var velocity: Vector, topSpeed: Double, val mass: Double = 1.0){
  protected var acceleration = new Vector(0,0)
  private val maxAcceleration = 5.0
  private val noise = false
  private val r = new Random()
  private val vision = 150.0
  
  /**
   * Updates the position of the boid
   * 
   * @param dt the time difference since last update
   */
  def updatePosition(dt: Double): Unit = {
    position = position + velocity * dt
  }
  
  /**
   * Updates the acceleration of the boid
   * 
   * @param dt the time difference since last update
   * @param otherboids an array consisting all the other Boid objects. Note: array can contain this instance
   * itself, it will be filtered out
   * @param obstacles an array consisting of obstacles
   * @param S the parameter for separation
   * @param C the parameter for cohesion
   * @param A the parameter for alignment
   * @param radius the radius of fish-o-vision(TM)
   */
  def updateAcceleration(dt: Double, otherBoids: Array[Boid], obstacles: Array[Obstacle],
      S: Double, C: Double, A: Double, radius: Double): Unit = {
    
    //every update round acceleration is reset
    //note: acceleration is actually a force vector
    acceleration = new Vector(0,0)
    
    
    
    //Step 1. filter nearby boids ( using distanceToSquared eliminates need to calculate square root)
    val nearBoids = otherBoids.filter(b => {
      val distance = this.position.distanceToSquared(b.position)
      distance <= radius * radius && distance > 0.0} //latter will filter fish itself
    )
   
    
    val steering = Boid.steerAwayFromObstacles(this, obstacles)
    acceleration += steering._1    
    
    val steering2 = Boid.obstacleCollisionAvoidanceVector(this, obstacles)
    acceleration += steering2._1 * 1000
    
    val avoidanceMode = steering2._2 || steering2._2
    
    //if the fish is in obstacle avoidanceMode it will ignore flocking behavior
    
    if(nearBoids.length > 0 && !avoidanceMode){      
         
      /**
      * Step 3. Separation
      */      
      acceleration += Boid.separationSteerVector(this, nearBoids) * S
      
      /**
     	* Step 4. Alignment
     	*/
      acceleration += Boid.alignmentSteerVector(this, nearBoids) * A      
    
      /**
      * Step 5. Cohesion
      */
      acceleration += Boid.cohesionSteerVector(this, nearBoids) * C     
      
    }
    
    /**
     * Step 6. Update velocity
     */
    
    velocity += acceleration * (dt * 1/mass) 
    
    //normalize velocity if it exceeds max value
    if(velocity.length > topSpeed) velocity = velocity.normalizeLengthTo(topSpeed)     
    
    
  }  
   
}

/** Companion object for calculating force vectors to steer a boid */
object Boid{   
  
    /** Calculate the a alignment cohesion force steervector */  
    def alignmentSteerVector(boid: Boid, otherBoids: Array[Boid]): Vector = {
      
      val velocitySum = Vector.sumOfVectors(otherBoids.map(b => b.velocity.normalize))      
      val averageNormalizedVelocity = velocitySum.normalize
      val normalizedVelocity = boid.velocity.normalize      
      averageNormalizedVelocity - normalizedVelocity * (averageNormalizedVelocity * normalizedVelocity)
     
    }
    
    /** Calculate the a cohesion force steervector */
    def cohesionSteerVector(boid: Boid, otherBoids: Array[Boid]): Vector = {
     val angle = 0.8 * math.Pi
     //exclude the boids behind this boid (experimentally selected offset angle)
     val nearBoidsExcluded = otherBoids
     .filter(b => Vector.angle(boid.velocity, b.position - boid.position) < angle)
     
     if(nearBoidsExcluded.length > 0){
        val center = Vector.centerPoint(nearBoidsExcluded.map(b => b.position))      
        center - boid.position        
      } else new Vector(0,0)
    }
    
    /** Calculate the separation force steervector */
    def separationSteerVector(boid: Boid, otherBoids: Array[Boid]): Vector = {       
      val distanceVectors = otherBoids.map(b => boid.position - b.position)
      //proportional to the inverse of distance      
      Vector.sumOfVectors(distanceVectors.map(v => v * (1 / v.lengthSquared)))      
    }
    
    def obstacleCollisionAvoidanceVector(boid: Boid, obstacles: Array[Obstacle]): (Vector, Boolean) = {
      var avoid = false
      
      val distanceVectors = obstacles
      .filter(o => boid.position.distanceTo(o.position) < o.radius + 0)
      .filter(o => Vector.angle(boid.velocity, o.position - boid.position) < 0.25 * math.Pi)
      .map(o => boid.position - o.position)
      if(distanceVectors.length > 0){
        avoid = true
      }
      (Vector.sumOfVectors(distanceVectors.map(v => v * (1 / ( v.length)))) * obstacles.length, avoid)  
    }
    
    /** Helper method for calculating if fishes current trajectory overlaps on obstacle */
    def trajectoryOverlaps(boid: Boid, obstacle: Obstacle): Boolean = {
      val d = obstacle.position - boid.position
      val v0 = boid.velocity.normalize
      
      val t = d * v0 / (v0.x * v0.x + v0.y * v0.y)
      
      var d1 = Double.MaxValue
      var d2 = d.length
      var d3 = (obstacle.position - (boid.position + v0 * boid.vision)).length
      if(t >= 0 && t <= boid.vision){
        d1 = (d - v0 * t).length        
      }
      
      d1 < obstacle.radius || d2 < obstacle.radius || d3 < obstacle.radius
    }
    
    /** If boids trajectory overlaps an obstacle calculates a steering force vector */
    def steerAwayFromObstacles(boid: Boid, obstacles: Array[Obstacle]): (Vector, Boolean) = {
      
      val obs = obstacles
      .filter(o => boid.position.distanceTo(o.position) < boid.vision)
      .filter(o => Boid.trajectoryOverlaps(boid, o))
      .sortWith( (o1,o2) => o1.position.distanceTo(boid.position) < o2.position.distanceTo(boid.position))
      
      if (obs.length > 0){
        val obstacle = obs(0) //closest obstacle
        val distToObstacle = obstacle.position - boid.position       
        val newacceleration = boid.velocity.normalize * (distToObstacle * boid.velocity.normalize) - distToObstacle 
        (newacceleration * ( 100000.0 * boid.mass * (1 / distToObstacle.lengthSquared) ), true)
      } else{        
        (new Vector(0,0), false)        
      }
    }
}