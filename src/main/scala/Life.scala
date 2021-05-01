import indigo._
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object Life extends IndigoSandbox[Unit, Model] :
  override val config: GameConfig = GameConfig.default
  override val animations: Set[Animation] = Set()

  def assetPath(pathWithoutPrefix: String) = AssetPath("assets/" + pathWithoutPrefix)

  val cellsAsset = AssetName("cells")
  override val assets: Set[AssetType] =
    Set(AssetType.Image(cellsAsset, assetPath("cells.png")))

  override val fonts: Set[FontInfo] = Set()
  override val shaders: Set[Shader] = Set()

  override def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] = Outcome(
    Startup.Success(())
  )

  override def initialModel(startupData: Unit): Outcome[Model] = Outcome(
    Model.initial
  )

  override def updateModel(context: FrameContext[Unit], model: Model): GlobalEvent => Outcome[Model] = {
    case MouseEvent.Click(x, y) => Outcome(
      model.addCell(Cell(Point(x, y), cellSize))
    )
    case KeyboardEvent.KeyDown(Key.SPACE) => Outcome(model.update)
    case _ => Outcome(model)
  }

  val cellSize = 10
  val cellGraphic =
    Graphic(Rectangle(0, 0, 10, 10), 1, Material.Bitmap(cellsAsset))
      .withCrop(1, 1, cellSize, cellSize)

  override def present(context: FrameContext[Unit], model: Model): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        model.aliveCells
          .map(_.toPoint(cellSize))
          .map(cellGraphic.moveTo(_))
          .toList
      )
    )
end Life

case class Model(aliveCells: Set[Cell]):
  def addCell(cell: Cell): Model =
    this.copy(aliveCells = aliveCells + cell)

  def update: Model =
    def allNeighbours(c: Cell) =
      (-1 to 1 flatMap { dx =>
        -1 to 1 map { dy =>
          Cell(c.x + dx, c.y + dy)
        }
      })
        .filterNot(_ == c)

    val allNeighboursOfAllCells =
      aliveCells
        .toList
        .flatMap(allNeighbours(_))
    val numberOfNeighbours =
      allNeighboursOfAllCells
        .groupBy(identity(_))
        .view
        .mapValues(_.size)
        .toMap
        .withDefault(_ => 0)

    val bornAndSurviversWithThreeNeighbours =
      numberOfNeighbours
        .filter { case _ -> n => n == 3 }
        .map(_._1)
    val surviversWithTwoNeighbours = aliveCells.filter(numberOfNeighbours(_) == 2)
    Model(surviversWithTwoNeighbours ++ bornAndSurviversWithThreeNeighbours)
end Model

object Model:
  def initial: Model = Model(Set())

case class Cell(x: Int, y: Int):
  def toPoint(cellSize: Int): Point = Point(x, y) * cellSize

object Cell:
  def apply(p: Point, cellSize: Int): Cell = Cell(p.x / cellSize, p.y / cellSize)