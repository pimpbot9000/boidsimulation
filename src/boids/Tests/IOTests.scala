package boids.Tests

import boids.io._
import org.scalatest._
import java.io._
import boids.io.UnableToLoadException
//@RunWith(classOf[JUnitRunner])

import scala.util.Random
class IOTests extends FlatSpec with Matchers {
  //val random = new Random()  
  //boids.io.SaveLoad.load()
  "SaveLoad.parser" should "be able to load a correctly formatted file" in {
    val testData = "10;20\n" + "100;20\n" + "200;30"
    val testInput = new BufferedReader(new StringReader(testData))
    
    val result = SaveLoad.parser(testInput)
    
    result shouldBe Array((10,20), (100,20), (200,30))
    testInput.close()
   }
  
  "SaveLoad.parser" should "be able to load a file with extra whitespaces" in {
    val testData = "10;20\n" + "100;   20\n" + "200;   30   "
    val testInput = new BufferedReader(new StringReader(testData))
    
    val result = SaveLoad.parser(testInput)
    
    result shouldBe Array((10,20), (100,20), (200,30))
    testInput.close()
   }
  
  "SaveLoad.parser" should "be able to load a file with empty lines" in {
    val testData = "10;20\n" + "100;   20\n\n" + "200;   30   "
    val testInput = new BufferedReader(new StringReader(testData))
    
    val result = SaveLoad.parser(testInput)
    
    result shouldBe Array((10,20), (100,20), (200,30))
    testInput.close()
   }
  
  "SaveLoad.parser" should "throw UnableToLoadException when file is corrupted" in {
    val testData = "10;20\n" + "100aa;   20\n" + "200;   30   "
    val testInput = new BufferedReader(new StringReader(testData))    
    
    intercept[boids.io.UnableToLoadException] {
      SaveLoad.parser(testInput)
    }        
    testInput.close()
  }
  
  "SaveLoad.load" should "throw UnableToLoadException when file is missing" in {
    intercept[boids.io.UnableToLoadException] {
      SaveLoad.load("iBetAFileNamedLikeThisDoesNotExist")
    }
  }
  
  "The App" should "be awesome" in {
    "awesomeApp" shouldBe "awesomeApp"
  }
  
}