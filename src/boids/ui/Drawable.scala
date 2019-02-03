package boids.ui

import scala.swing._
import java.awt.Graphics2D

trait Drawable {
  def draw(g: Graphics2D): Unit  
}