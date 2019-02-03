package boids.math

/**
 * A two dimensional vector
 * 
 * @constructor create new vector
 * @param x the x coordinate
 * @param y the y coodinate
 */
class Vector(x: Double, y: Double) extends Coordinate(x, y){
  
  /**
   * Alternative constructor using two Coordinates
   * 
   * @constructor creates a new vector using two coordinates
   * @param begin the starting point coordinate
   * @param end the ending point coordinate
   */
  def this(begin: Coordinate, end: Coordinate) = {
    this(end.x - begin.x, end.y - begin.y)
  }
  
  /**
   * Alternative Constructor using length and angle
   * 
   * @param length The length of the vector
   * @param angle the angle in radians or degrees
   * @param radians: If true angle is given in radians false if in degrees. 
   */
  def this(length: Double, angle: Double, radians: Boolean) = {
    this(length * Math.cos(angle * (if(radians) 1 else Vector.degreeToRad)), 
        length * Math.sin(angle * (if(radians) 1 else Vector.degreeToRad))) 
  }
  
  def length: Double = this.distanceTo(Origo)
  
  def lengthSquared: Double = this.distanceToSquared(Origo)
  
  /** Operator overloading for the sum two vectors. Result is a new vector. */  
  def +(other: Vector): Vector = new Vector(this.x + other.x, this.y + other.y)  
  
  /** Operator overloading for substracting two. Result is a new vector. */  
  def -(other: Vector): Vector = new Vector(this.x - other.x, this.y - other.y)
  
  /** Product between a vector and a scalar. Result is a new vector */  
  def multiply(multiplier: Double): Vector = new Vector(this.x * multiplier, this.y * multiplier)
  
  /** Product between a vector and a scalar */ 
  def *(multiplier: Double): Vector = this.multiply(multiplier)
  
  /** Dot product between two vectors */
  def *(other: Vector): Double = this.x * other.x + this.y * other.y 
  
  /** Normalizes a length of a vector to 1 */
  def normalize: Vector = this.multiply(1/this.length)
  
  /** Normalizes the length of a vector to given length
   *  @param length the length of the resulting vector
   */
  def normalizeLengthTo(length: Double): Vector = this.normalize * length
  
  /** Heading/angle of a vector in radians [0 ... 2*Pi] */
  def heading: Double = {
    val atan = math.atan(this.y / this.x)
    //second quarter
    if(this.x < 0 && this.y > 0){
      atan + math.Pi
    } else if ( this.x < 0 && this.y < 0) { //third quarter
      atan + math.Pi
    } else atan
    
  }
  
  override def equals(other: Any): Boolean = {
    if(!other.isInstanceOf[Vector]) false
    else other.asInstanceOf[Vector].x == this.x && other.asInstanceOf[Vector].y == this.y
  }
  
  override def toString = "[" + this.x + ", " + this.y + "]"
}

object ZeroVector extends Vector(0,0)

/**
 * Companion object for vector for a few vector operations
 */
object Vector{
  
  private val degreeToRad = 1/360.0 * 2 * math.Pi
  
  def sumOfVectors(vectors: Seq[Vector]): Vector = {
    
    var sumVector = new Vector(0,0)
    
    for(v <- vectors){
      sumVector += v
    }
    sumVector
  }
  
  def centerPoint(vectors: Seq[Vector]): Vector = {   
    sumOfVectors(vectors) * (1.0 / vectors.length) 
  }
  
  def angle(v1: Vector, v2: Vector): Double = {
    math.acos(v1 * v2 / (v1.length * v2.length))
  }
}