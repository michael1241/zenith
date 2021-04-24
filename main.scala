import scala.io.Source
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object Main extends App {
    case class Game(
        white: String,
        black: String,
        weektime: Long,
        whiteelo: Int,
        blackelo: Int,
        whiteratingdiff: Int,
        blackratingdiff: Int,
        timecontrol: Int
    )

    val test = Source.fromFile("test").mkString.split("\n\n\n")

    def timeConvert(control: String): Int =
        control.split("\\+") match {
            case Array(mins, sec) => (mins.toInt * 60) + (40 * sec.toInt)
            case _ => 0
        }

    def dayConvert(date: String, time: String): Long =
        ((DateTime.parse(time, DateTimeFormat.forPattern("HH:mm:ss").withZoneUTC()).getMillis + DateTime.parse(date, DateTimeFormat.forPattern("yyyy.MM.dd").withZoneUTC()).getMillis) / 1000) % 604800 //60*60*24*7 seconds in a week

    def parseGame(gamestring: String): Option[Game] = {
        val lines: Array[String] = gamestring.split("\n")
        val t: Seq[String] = {
            lines map {
                case s"""[${tagname} "${tagvalue}"]""" => tagvalue
            }
        }
        t match {
            case t if t.length == 9 => 
                Some(Game(white = t(0),
                    black = t(1),
                    weektime = dayConvert(t(2), t(3)),
                    whiteelo = t(4).toInt,
                    blackelo = t(5).toInt,
                    whiteratingdiff = t(6).toInt,
                    blackratingdiff = t(7).toInt,
                    timecontrol = timeConvert(t(8))
                ))
            case _ => None
            }
    }

    val games: Array[Game] = test flatMap parseGame
    games.take(1000) map println
}
