package QTrees

import GUI.Container
import TUI.CommandLineOption

import scala.collection.SortedMap
import scala.io.Source
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}
import java.io._
import scala.annotation.tailrec

object IO_Utils {

  def getUserInputInt(msg : String): Try[Int] = {
    print(msg + ": ")
    Try(readLine.trim.toUpperCase.toInt)
  }

  def getUserInputDouble(msg : String): Try[Double] = {
    print(msg + ": ")
    Try(readLine.trim.toUpperCase.toDouble)
  }

  def prompt(msg : String):String = {
    print(msg + ": ")
    scala.io.StdIn.readLine()
  }

  def printMessage(msg : String): Unit = println(msg)


  @tailrec
  def optionPrompt(options : SortedMap[Int, CommandLineOption]): Option[CommandLineOption] = {
    println("-- Options --")
    options.toList map ((option:(Int, CommandLineOption)) => println(option._1 + ") " + option._2.name))

    getUserInputInt("Select an option") match {
      case Success(i) => options.get(i)
      case Failure(_) => println("Invalid number!");optionPrompt(options)
    }
  }

  def readFromFile(file: String): Container = {
    var container: Container = new Container(file, List())
    val bufferedSource = Source.fromFile(file)
    for (line <- bufferedSource.getLines) {
      val tokens = line.split(">")
      println(tokens(0))
      if(tokens.size == 1)
        container = Container.add(tokens(0),"")(container)
      else
        container = Container.add(tokens(0), tokens(1))(container)
    }
    bufferedSource.close
    container
  }

  def writeToFile(file: String, container: Container): Unit = {
    val pw = new PrintWriter(new File(file))
    container.data map (tokens => pw.write(tokens._1 + ">" + tokens._2 + "\n"))
    pw.close()
  }

}
