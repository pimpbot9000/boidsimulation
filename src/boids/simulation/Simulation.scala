package boids.simulation
import scala.collection.mutable.ArrayBuffer
import boids.math.Vector

class Simulation(val width: Int, val height: Int) {
  private val boids = new ArrayBuffer[Boid]()
  private val obstacles = new ArrayBuffer[Obstacle]()
  private val initialSpeed = 100
  private val topSpeed = 150
  private val offset = 20
  //default values for parameters
  var S = 10000.0 // 0 to 100000 (optimum 50000)
  var C = 30.0 // 0 to 50 (30 optimum)
  var A = 1000.0 // 0 to 2000
  var radius = 100.0 //50 to 100 (default 100)
  /**
   * Updates simulation
   * @param dt the time since last update 
   */
  def update(dt: Double): Unit = {
    //clone the array before update or clearing fish will crash the whole damn thing
    //that is to move to pointers to fish to safety     
    
    val tempBoids = boids.clone()
    tempBoids.foreach(b => b.updateAcceleration(dt, tempBoids.toArray, obstacles.toArray, S, C, A, radius))
      
    tempBoids.foreach(b => {      
      b.updatePosition(dt)
      
      //respawn fish if fish exits the simulation area
      if(b.position.x < -offset) b.position = new Vector(width + offset/2, b.position.y)
      else if(b.position.x > width + offset) b.position = new Vector(-offset/2, b.position.y)
      else if(b.position.y < -offset) b.position = new Vector(b.position.x, height + offset/2)
      else if(b.position.y > height + offset) b.position = new Vector(b.position.x, -offset/2)
    })
  }  
  /** Adds new boid */
  def addBoid(x: Double, y: Double, heading: Double, mass: Double): Unit = {
    boids += new Boid( new Vector(x,y), new Vector(initialSpeed, heading, true), topSpeed, mass)
  }
  
  /** 
   *  Gets an array of all boids in simulation. Returns array which element
   *  is a tuple (x-coordinate, y-coordinate, angle, mass)
   */
  def getBoids: Array[(Double, Double, Double, Double)] = 
    boids.map(b => (b.position.x, b.position.y, b.velocity.heading, b.mass)).toArray
  
  def clearBoids(): Unit = boids.clear()
  
  def clearObstacles(): Unit = obstacles.clear()
  
  
  def addObstacle(x: Double, y: Double): Unit = {
    this.obstacles += new Obstacle(new Vector(x,y))
  }
  /**
   * Gets an array of all obstacles. Element of the array is a tuple
   * (x-coordinate, y-coordinate, radius)
   */
  def getObstacles: Array[(Double, Double, Double)] =
    obstacles.map(o => (o.position.x, o.position.y, o.radius)).toArray
  
}