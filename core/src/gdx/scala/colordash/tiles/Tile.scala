package gdx.scala.colordash.tiles

import com.badlogic.gdx.math.Rectangle
import gdx.scala.colordash.{Constants, Pool, Poolable, TiledWorld}
import scala.reflect.ClassTag

class Tile(var x: Int = 0, var y: Int = 0) extends Poolable {
  val width: Float = Constants.tileWidth
  val height: Float = Constants.tileHeigth

  var content: TileContent = Brick

  def reset = {
    x = 0
    y = 0
    content = Brick
  }

  def tileUp = TiledWorld.getTile(x, y + height.toInt)

  def tileDown = TiledWorld.getTile(x, y - height.toInt)

  def tileRight = TiledWorld.getTile(x + width.toInt, y)

  def tileLeft = TiledWorld.getTile(x - width.toInt, y)

  def overlaps(r: Rectangle) =
    x < r.x + r.width &&
      x + width > r.x &&
      y < r.y + r.height &&
      y + height > r.y

  def has[T <: TileContent : ClassTag]: Boolean = {
    val klass = implicitly[ClassTag[T]].runtimeClass
    klass.isInstance(content)
  }
}

object Tile extends Pool[Tile] {
  def newObject: Tile = new Tile()

  def apply(x: Int, y: Int): Tile = {
    val tile = obtain
    tile.x = x
    tile.y = y
    tile
  }

  def apply(r: Rectangle): Tile = apply(r.x.toInt, r.y.toInt)

  override def free(obj: Tile): Unit = {
    obj.content match {
      case c: Activator => Activator.free(c)
      case _ =>
    }
    super.free(obj)
  }
}




