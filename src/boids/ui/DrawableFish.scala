package boids.ui
import scala.swing._

import java.awt.{Color, Graphics2D}

class DrawableFish(val x: Double, val y: Double, val heading: Double, val radius: Double, val circleOn: Boolean, val mass: Double ) extends Drawable{
  
  def draw(g: Graphics2D): Unit = {
    val xValues = Array[Double](x + 15*mass * Math.cos(heading), x + 8*mass * Math.cos(heading - 2.1), x, x + 8*mass * Math.cos(heading + 2.1))
    val yValues = Array[Double](y + 15*mass * Math.sin(heading), y + 8*mass * Math.sin(heading - 2.1), y, y + 8*mass * Math.sin(heading + 2.1))
    g.setColor(Color.BLUE)
    g.fillPolygon(xValues.map(_.toInt), yValues.map(_.toInt), 4)
    
    if(circleOn){
      g.drawOval((x - radius).toInt, (y - radius).toInt, (2 * radius).toInt, (2 * radius).toInt) //100 = radius
    }
  }
  
}