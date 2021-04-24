import scala.io.Source

object Main extends App {
    case class Game(white: String, black: String, winner: String, weektime: String, whiteelo: String, blackelo: String, whiteratingdiff: String, blackratingdiff: String, timecontrol: String)

    val test = Source.fromFile("test").mkString.split("\n\n\n").take(3)

    def parseGame(gamestring: String): Any = { //change from Any to Option[Game]
        val lines: Array[String] = gamestring.split("\n")
        lines map {
            case s"""[${tagname} "${tagvalue}"]""" => (tagname, tagvalue) match {
                case ("White", _) => println(tagvalue)
                case ("Black", _) => println(tagvalue)
                case _ => None
            }
            case _ => None
        }
        
    }

    test map parseGame

}
