package boids.ui
import boids.simulation.Simulation
import scala.util.Random
import boids.math._


class FishWorld(width: Int, height: Int) extends CanvasContent(width, height){
 
  private val r = new Random()
  private val simulation = new Simulation(width, height)
  private var drawCircles = false
  private var maxNofFish = 500
  
  def switchCircles(): Unit = {
    drawCircles = !drawCircles 
  }
  
  def setObstacles(coords: Array[(Int, Int)]): Unit = {
    this.simulation.clearObstacles()
    coords.foreach(c => this.simulation.addObstacle(c._1, c._2))
  }
  
  def getDrawables: Array[Drawable] = {     
      val fishArr = simulation.getBoids.map(b => new DrawableFish(b._1, b._2, b._3, simulation.radius, drawCircles, b._4)).toArray
      val obstacleArr = simulation.getObstacles.map(o => new DrawableObstacle(o._1, o._2, o._3-10)).toArray
      fishArr ++ obstacleArr
  }
  
  def getObstacleCoordinates(): Array[(Int, Int)] = {
    simulation.getObstacles.map( o => (o._1.toInt, o._2.toInt))
  }
  
  def update(dt: Double): Unit = {
     
     simulation.update(dt)
    
  }
 
  def randomize(): Unit = {
    var counter = 0
    while( counter < 20 && simulation.getBoids.length < maxNofFish) {
     
      val x = 100 + r.nextInt(width-200)
      val y = 100 + r.nextInt(height-200)
      val newPosition = new Vector(x,y)
      //make sure new fish does not overlap obstacle
      if(simulation.getObstacles.forall( o => (new Vector(o._1, o._2)).distanceToSquared(newPosition) > o._3 * o._3 )){
         
        simulation.addBoid( x, y, r.nextDouble()*2*math.Pi, 1.0)     
        counter += 1
        
      }
    }
  }
  
  def addFish(x: Double, y: Double): Unit = {
    if (simulation.getBoids.length < 500)
      simulation.addBoid(x, y, r.nextDouble()*2*math.Pi, 0.5 + r.nextDouble()*1)
  }
  
  def addObstacle(x: Double, y: Double): Unit = {
    simulation.addObstacle(x, y)
  }
  
  def clearFish(): Unit = simulation.clearBoids()
  
  def clearObstacles(): Unit = simulation.clearObstacles()
  
  def setSeparationCoefficient(S: Double): Unit ={
    this.simulation.S = S
  }
  
  def setCohesionCoefficient(C: Double): Unit ={
    this.simulation.C = C
  }
  
  def setAlignmentCoefficient(A: Double): Unit = {
    this.simulation.A = A
  }
  
  def setRadius(radius: Double): Unit = {
    this.simulation.radius = radius
  }
  
}