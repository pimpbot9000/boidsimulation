package boids.Tests
import boids.math._
import org.scalatest._
//@RunWith(classOf[JUnitRunner])
class VectorTest extends FlatSpec with Matchers {
  
  "Vector.equals method" should "be return true when equal" in {
    assert( (new Vector(20,5)).equals(new Vector(20,5)) )
  }
  
  "Vector.length" should "return correct length" in {
    val v = new Vector(1, 4)
    assert(v.length === math.sqrt(17) +- 0.001)
  }
  
  "Vector.normalize" should "return correctly normalized vector" in {
    val v = new Vector(10,10)
    val normalized = v.normalize
    assert(normalized.x === Math.sqrt(2)/2.0 +- 0.01 && normalized.y === Math.sqrt(2)/2.0 +- 0.01)
  }
  
  "Overloaded vector sum operator" should "return correct result" in {
    val v1 = new Vector(10,10)
    val v2 = new Vector(5, 15)
    assert( (v1 + v2).equals(new Vector(15.0, 25.0)) === true )
  }
  
  "Overloaded vector subtraction operator" should "return correct result" in {
    val v1 = new Vector(10,10)
    val v2 = new Vector(5, 15)
    assert( (v1 - v2).equals(new Vector(5.0, -5.0)) === true )
  }
  
  "Overloaded vector dot product" should "return correct result" in {
    val v1 = new Vector(5, 6)
    val v2 = new Vector(5, 10)
    assert( (v1 * v2) === 85 )
  }
  
  "Overloaded vector product with scalar" should "return correct result" in {
    val v1 = new Vector(5, 6)
    
    assert( (v1 * 2.0).equals(new Vector(10.0, 12.0)) === true) 
    
  }
  
  
  "Vector.centerPoint" should "return correct result" in {
    val arr = Array(new Vector(1,2), new Vector(-1,2), new Vector(0,1), new Vector(0,3))
    val center: Vector = Vector.centerPoint(arr)
    assert(center.equals(new Vector(0,2.0)) === true)   
    
  }
  
  "Vector.normalizeLengthTo" should "reuturn correct result" in {
    val v1  = new Vector(120, 360)
    assert( v1.normalizeLengthTo(250).length === 250.00 +- 0.001)
  }
  
  "Vector.angle" should "return correct result" in {
    val v1 = new Vector(0,10)
    val v2 = new Vector(-4, 0)
    
    assert(Vector.angle(v1, v2) === 0.5 * math.Pi +- 0.0001)
  }
}