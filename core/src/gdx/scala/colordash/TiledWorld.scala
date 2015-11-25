package gdx.scala.colordash

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer, TmxMapLoader}
import gdx.scala.colordash.effects.{Effects, EffectState}
import gdx.scala.colordash.entities.Renderizable
import gdx.scala.colordash.tiles.{Brick, Spike, Activator, Tile}

object TiledWorld {
  var levelMap: TiledMap = _
  var mapRenderer: OrthogonalTiledMapRenderer = _

  def initialize(): Unit = {
    levelMap = new TmxMapLoader().load("boxes.tmx")
    mapRenderer = new OrthogonalTiledMapRenderer(levelMap, Constants.unitScale)
  }

  def render(renderizables: Iterable[Renderizable], camera: OrthographicCamera): Unit = {
    mapRenderer.setView(camera)
    mapRenderer.render()
    val batch = mapRenderer.getBatch
    batch.begin()
    renderizables.foreach(_.render(batch))
    batch.end()
  }

  def findTiles(startX: Int, startY: Int, endX: Int, endY: Int)(implicit tiles: com.badlogic.gdx.utils.Array[Tile]) {
    val layer: TiledMapTileLayer = levelMap.getLayers.get("level").asInstanceOf[TiledMapTileLayer]
    tiles.clear()
    for (x <- startX to endX; y <- startY to endY) {
      val cell: TiledMapTileLayer.Cell = layer.getCell(x, y)
      if (cell != null) {
        tiles.add(cell.asTile(x, y))
      }
    }
  }

  def getTile(x: Int, y: Int): Option[Tile] = {
    val layer: TiledMapTileLayer = levelMap.getLayers.get("level").asInstanceOf[TiledMapTileLayer]
    val cell = layer.getCell(x, y)
    cell match {
      case null => None
      case _ => Some(cell.asTile(x, y))
    }
  }

  implicit class CellToTile(cell: Cell) {

    def asTile(x: Int, y: Int): Tile = {
      val tile = Tile(x, y)
      val cellProperties = cell.getTile.getProperties
      cellProperties.get("type") match {
        case "activator" => tile.content = Activator(cellProperties.get("effect", Effects.None, classOf[EffectState]))
        case "spike" => tile.content = Spike
        case _ => tile.content = Brick
      }
      tile
    }

  }

}
