package boids.io
import boids.simulation.Obstacle
import scala.io.Source._
import java.io._
import java.io.FileNotFoundException

/** An object to handle save/load features */
object SaveLoad {
  /**
   * Saves positions of obstacles into a file
   * 
   * @param filename the filename
   * @param data the array of obstacle coordinates as tuple 
   * 
   */
  def save(fileName: String, data: Array[(Int,Int)]): Unit = {
    
    try{
    
      val file = new File(fileName)
      val fw = new FileWriter(file)
      val bw = new BufferedWriter(fw)
      
      try{
        for(d <- data){
          bw.write(d._1 + ";" + d._2 + "\n");
        }
      } finally {
        bw.close()
        fw.close()
      }
   
    } catch {
      case e:IOException =>
        throw new UnableToSaveException("unknown error occurred.") 
    }
    
   
  }
  
  /** Opens a file */
  def load(filename: String): Array[(Int, Int)] = {
    try {
      
      val fileIn = new FileReader(filename)
      
      val linesIn = new BufferedReader(fileIn)
      
      try {         
        val obstacles = this.parser(linesIn)
        return obstacles
      } finally {      

        fileIn.close()
        linesIn.close()
      }
    } catch {
        //catch errors and rethrows
        case notFound:FileNotFoundException => 
          throw new UnableToLoadException("file not found")    
        case e:IOException => 
          throw new UnableToLoadException("cannot read file")           
    }
      
  }  
  
  /** parses the opened file */
  def parser(input: BufferedReader): Array[(Int, Int)] = {
    val resultArr = new scala.collection.mutable.ArrayBuffer[(Int, Int)]()
    var line = input.readLine()
    while(line != null){
      if(line.trim() != ""){
        val dataArr = line.split(";")
      
        if(dataArr.length != 2){
          throw new UnableToLoadException("something went wrong")
        } else {
        //let's try to parse file
          try{
            val x = dataArr(0).trim().toInt
            val y = dataArr(1).trim().toInt
            resultArr += ((x,y))
          } catch {
          case e: NumberFormatException => throw new UnableToLoadException("not a valid coordinate!")
          }
        }
      }
      line = input.readLine()
    }
    return resultArr.toArray
  }
  
  
  
  
}