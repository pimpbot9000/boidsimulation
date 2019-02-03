package boids.math

class Coordinate(val x: Double, val y: Double) {
  def distanceTo(other: Coordinate): Double = Math.sqrt( Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2))
  def distanceToSquared(other: Coordinate): Double = Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2)
}
/** defines origo */
object Origo extends Coordinate(0,0)