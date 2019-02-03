package boids.ui

import scala.swing._


class DrawableObstacle(val x: Double, val y: Double, val radius: Double) extends Drawable{
  
  def draw(g: Graphics2D): Unit = {
    g.setColor(java.awt.Color.WHITE)
    g.fillOval((x - radius).toInt, (y - radius).toInt, (2 * radius).toInt, (2 * radius).toInt)
  }
}