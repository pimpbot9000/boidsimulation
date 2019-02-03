package boids.ui


abstract class CanvasContent(val width: Int, val height: Int){
  def getDrawables: Array[Drawable]  
  def update(dt: Double): Unit
}