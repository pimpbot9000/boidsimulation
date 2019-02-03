package boids.ui
import scala.swing._
import java.awt.{Color, Graphics2D}

/**
 * A panel to draw graphics in 
 */

class Canvas(val width: Int, val height: Int, val content: CanvasContent) extends Panel{
  background = Color.BLACK  
  
  preferredSize = new Dimension(width, height)  
  
  override def paintComponent(g: Graphics2D) {
     super.paintComponent(g)
     g.setColor(background)
     content.getDrawables.foreach(_.draw(g))   
  }

}
