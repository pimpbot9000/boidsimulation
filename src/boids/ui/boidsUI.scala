package boids.ui
import scala.swing._
import scala.swing.event._
import java.awt.{Color, Dimension, Graphics, Graphics2D, Point, geom, Polygon}
import java.awt.event._
import scala.util.Random
//import javax.swing._
//import java.awt.event._
import javax.swing.UIManager;
import boids.io._
import javax.swing.JFrame
object runMe extends SimpleSwingApplication {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
  val WIDTH = 1200
  val HEIGHT = 800
  //create world to access game logic
  val fishworld = new FishWorld(this.WIDTH, this.HEIGHT)
  
  val savefileName = "data.txt"
  
  
  val verticalPanel = new BoxPanel(Orientation.Vertical)
  val horizontalPanel = new GridPanel(2,3)
  
  //buttons
  val buttonRandomize = new Button("Randomize 20 Fish")
  val buttonClearFish = new Button("Clear Fish")
  val buttonClearObstacles = new Button("Clear Walls")
  val buttonCircles = new Button("Circles ON/OFF")
  
  //sliders
  
  val separationSlider = new Slider{
    min = 0
    max = 80000   
  }
  separationSlider.value = 10000
  separationSlider.name = "separationSlider"
  
  val cohesionSlider = new Slider{
    min = 0
    max = 50
    value = 30
    name = "cohesionSlider"
  } 
  
  val alignmentSlider = new Slider{
    min = 0
    max = 3000
    value = 1000
    name = "alignmentSlider"
  }
 
  
  val radiusSlider = new Slider{
    min = 50
    max = 200
    name = "radiusSlider"
    value = 100
  }
  
  
  
  horizontalPanel.contents += new BoxPanel(Orientation.Horizontal) {
             
     contents += buttonRandomize
      
  }
  
  horizontalPanel.contents += new BoxPanel(Orientation.Horizontal){
    contents += new BoxPanel(Orientation.Horizontal){
      
      contents += new Label("Separation")
      contents += separationSlider
    }
    
    contents += new BoxPanel(Orientation.Horizontal){
      
      contents += new Label("Alignment")
      contents += alignmentSlider
    }
    
    contents += new BoxPanel(Orientation.Horizontal){
      
      contents += new Label("Cohesion")
      contents += cohesionSlider
    }
    
    
    
  }
  
  horizontalPanel.contents += new BoxPanel(Orientation.Horizontal){
      contents += buttonClearFish
      contents += buttonClearObstacles
      contents += buttonCircles
      
    }
  horizontalPanel.contents += new BoxPanel(Orientation.Horizontal){
    contents += (new Label("Adjust Fish-o-Vision (TM) Radius: "), radiusSlider)
  }
  
  
  val canvas = new Canvas(WIDTH, HEIGHT, fishworld)
  verticalPanel.contents += horizontalPanel
  verticalPanel.contents += canvas
  
  //register listeners
  this.listenTo(canvas.mouse.clicks)
  this.listenTo(buttonRandomize)
  this.listenTo(buttonClearFish)
  this.listenTo(buttonClearObstacles)
  this.listenTo(buttonCircles)
  this.listenTo(separationSlider)
  this.listenTo(cohesionSlider)
  this.listenTo(alignmentSlider)
  this.listenTo(radiusSlider)
  //define listener actions
  this.reactions += {
    
    case ValueChanged(slider) => {
      slider.name match {
        case "separationSlider" =>
          fishworld.setSeparationCoefficient(slider.asInstanceOf[Slider].value)
        case "cohesionSlider" =>
          fishworld.setCohesionCoefficient(slider.asInstanceOf[Slider].value)
        case "alignmentSlider" =>
          fishworld.setAlignmentCoefficient(slider.asInstanceOf[Slider].value)
        case "radiusSlider" =>
          fishworld.setRadius(slider.asInstanceOf[Slider].value)
      }
    }
    
    //left click on canvas
    case MouseClicked(component, point, modifiers, clicks, triggersPopUp) if modifiers == 0 =>            
      fishworld.addFish(point.x, point.y)
      
      
    //right click on canvas  
    case MouseClicked(component, point, modifiers, clicks, triggersPopUp) if modifiers == 256 =>      
      fishworld.addObstacle(point.x, point.y)
    
    case ButtonClicked(button) => {
      button.text match {
        case "Randomize 20 Fish" => fishworld.randomize()
        case "Clear Fish" => fishworld.clearFish()
        case "Circles ON/OFF" => fishworld.switchCircles()
        case "Clear Walls" => fishworld.clearObstacles()
      }
    }
   
      
      
  }
  
  
  //setup the window
  val mainView = new MainFrame{
    preferredSize = new Dimension(WIDTH, HEIGHT + 100)
    menuBar = new MenuBar{
      contents += new Menu("File"){
        contents += new MenuItem(new Action("Load"){
          def apply{
            
            try{
              val obstacles = boids.io.SaveLoad.load(savefileName)
              fishworld.clearFish()
              fishworld.setObstacles(obstacles)
              SimulationThread.running = true 
            } catch {
              case e: UnableToLoadException =>                
                Dialog.showMessage(verticalPanel.contents(1), "File could not be loaded. Cause:\n" + e.getLocalizedMessage, title="Error")
            }
          }
        })
        contents += new MenuItem(new Action("Save"){
          
          def apply{
            try{
              boids.io.SaveLoad.save(savefileName, fishworld.getObstacleCoordinates())
            } catch {
                case e: UnableToSaveException =>
                  Dialog.showMessage(verticalPanel.contents(1), message = "Error occurred", title="Error")                
            }
          }
          
        })
      }
    }
  }
  mainView.title = "There's plenty of picky fish in the sea"
  mainView.resizable = false
  mainView.contents = verticalPanel
  
  def top = mainView
  
  
  
  
  //set window visible  
  
  canvas.repaint()
  
  //repaint canvas every 10 ms
  new javax.swing.Timer(10, new ActionListener {
    def actionPerformed(e: java.awt.event.ActionEvent) {
      canvas.repaint()
    }
  }).start
  
  object SimulationThread extends Runnable {
    var running = true
    private var prevTime: Long = 0 // random initial value
    private var currentTime: Long = 0 //random initial value
    
    
    def run() = {
      
      currentTime = 0
      this.prevTime = System.currentTimeMillis()
      
      //start the infinite simulation loop! To teh infinity and beyond!
      while(running){
        this.currentTime = System.currentTimeMillis()
        val dt = this.currentTime - this.prevTime
        this.prevTime = this.currentTime
        fishworld.update(dt * 0.001)
      }
    }
  }
  
  new Thread(SimulationThread).start() 
}