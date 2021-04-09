package QTrees

import java.awt.Color
import QTrees.BitMap._

case class BitMap(img: List[List[Int]]) {
  def makeQTree(): QTree[Coords] = BitMap.makeQTree(this)

}

object BitMap {

  type Point = (Int, Int)
  type Coords = (Point, Point)
  type Section = (Coords, Color)

  def makeQTree(b:BitMap): QTree[Coords] = {
    val x = b.img(0).length - 1
    val y = b.img.length - 1
    auxMQT( ((0,0):Point, (x,y):Point):Coords, b )

  }

  def auxMQT(c:Coords, b: BitMap): QTree[Coords] = {

    def coordsInbounds(bound:Coords, coords:Coords): Boolean = {
      val (boundTopX,boundTopY) = bound._1
      val (boundBotX,boundBotY) = bound._2
      val (coordsTopX,coordsTopY) = coords._1
      val (coordsBotX,coordsBotY) = coords._2

      (boundTopX<=coordsTopX) && (boundTopX<=coordsBotX) &&
      (boundBotX>=coordsTopX) && (boundBotX>=coordsBotX) &&
      (boundTopY<=coordsTopY) && (boundTopY<=coordsBotY) &&
      (boundBotY>=coordsTopY) && (boundBotY>=coordsBotY)
    }

    if( (c._1._1 == c._2._1) && (c._1._2 == c._2._2) ) {
      val a = ImageUtil.decodeRgb(b.img(c._1._2)(c._1._1)).toList
      val color = new Color(a(0),a(1),a(2)) //new Color(b.img(c._1._2)(c._1._1))
      return new QLeaf[Coords, Section]( (c, color):Section ) // RETURN?
    }

    val sCords = splitCoords(c)


    println("sCords._1: " + sCords._1)
    println("sCords._2: " + sCords._2)
    println("sCords._3: " + sCords._3)
    println("sCords._4: " + sCords._4)

    val qtOne = if(coordsInbounds(c, sCords._1)) auxMQT(sCords._1, b) else QEmpty
    val qtTwo = if(coordsInbounds(c, sCords._2)) auxMQT(sCords._2, b) else QEmpty
    val qtThree = if(coordsInbounds(c, sCords._3)) auxMQT(sCords._3, b) else QEmpty
    val qtFour = if(coordsInbounds(c, sCords._4)) auxMQT(sCords._4, b) else QEmpty

    (qtOne, qtTwo, qtThree, qtFour) match {
      case (q1: QLeaf[Coords, Section], q2:QLeaf[Coords, Section], q3:QLeaf[Coords, Section], q4:QLeaf[Coords, Section]) => {
        if( (q1.value._2 equals  q2.value._2) && (q3.value._2 equals q4.value._2) && (q1.value._2 equals q4.value._2 ) )
          new QLeaf[Coords, Section]( (c, q1.value._2):Section )
        else
          new QNode[Coords](c, qtOne, qtTwo, qtThree, qtFour) //(value: A, one: QTree[A], two: QTree[A], three: QTree[A], four: QTree[A])
      }
      case _ => new QNode[Coords](c, qtOne, qtTwo, qtThree, qtFour) //(value: A, one: QTree[A], two: QTree[A], three: QTree[A], four: QTree[A])
    }
  }

  def splitCoords(c:Coords): (Coords, Coords, Coords, Coords) = { //temos que considerar que pode não ser quadrada e imagens com lados impares

    val width = c._2._1 - c._1._1 + 1
    val height = c._2._2 - c._1._2 + 1

    val c1 = ( c._1, (c._1._1+(width/2.0).ceil.toInt-1, c._1._2+(height/2.0).ceil.toInt-1) )
    val c2 = ( (c._1._1+(width/2.0).ceil.toInt, c._1._2),(c._2._1, c._1._2+(height/2.0).ceil.toInt-1) )
    val c3 = ( (c._1._1, c._1._2+(height/2.0).ceil.toInt),(c._2._1-(width/2.0).floor.toInt, c._2._2) )
    val c4 = ( (c._1._1+(width/2.0).ceil.toInt, c._1._2+(height/2.0).ceil.toInt), c._2 )
    (c1,c2,c3,c4)
    //val c1 = ( c._1,( c._2._1-(width/2.0).floor.toInt,c._2._2-(height/2.0).floor.toInt ) )
    //val c2 = ( ( c._1._1+(width/2.0).ceil.toInt,c._1._2),(c._2._1,c._2._2-(height/2.0).floor.toInt ) ) //x da primeira esta errado
    //val c3 = ( (c._1._1,c._1._2+(height/2.0).ceil.toInt),(c._2._1-(width/2.0).floor.toInt,c._2._2) ) //y da primeira coordenada está errado
    //val c4 = ( (c._1._1+(width/2.0).ceil.toInt, c._1._2+(height/2.0).ceil.toInt),(c._2) ) //x,y da primeira está errado

  }

  /*
  def splitCoords(c:Coords): (Coords, Coords, Coords, Coords) = { //Está mal aqui probably
    val width = c._2._1 - c._1._1 + 1
    val height = c._2._2 - c._1._2 + 1
    println("Width: " + width + " Height: " + height)
    if(width == 2 && height == 1){
      print("IN1")
      val ac1 = ( (c._1),( c._2._1-width/2,c._2._2 ) )
      val ac2 = ( ( c._1._1+width/2,c._1._2),(c._2) )
      val ac3 = ( (-1,-1), (-1,-1) ) //enumerado
      val ac4 = ( (-1,-1), (-1,-1) )
      return (ac1,ac2,ac3,ac4)
    }
    if(height == 2 && width == 1){
      print("IN2")
      val ac1 = ( (c._1),( c._2._1,c._2._2-height/2 ) )
      val ac2 = ( (-1,-1), (-1,-1) )
      val ac3 = ( ( c._1._1,c._1._2+height/2),(c._2) )
      val ac4 = ( (-1,-1), (-1,-1) )
      return (ac1,ac2,ac3,ac4)
    }

    val c1 = ( (c._1),( c._2._1-(width/2).ceil.toInt,c._2._2-(height/2).ceil.toInt ) )
    val c2 = ( ( c._1._1+(width/2).ceil.toInt,c._1._2),(c._2._1,c._2._2-(height/2).floor.toInt ) )
    val c3 = ( (c._1._1,c._1._2+(height/2).ceil.toInt),(c._2._1-(width/2).floor.toInt,c._2._2) )
    val c4 = ( (c._1._1+(width/2).ceil.toInt, c._1._2+(height/2).ceil.toInt),(c._2) )
    (c1,c2,c3,c4)
  }*/

}
