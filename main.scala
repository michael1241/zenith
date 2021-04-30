import scala.io.Source
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object Main extends App {
    case class Game(
        white: String,
        black: String,
        weekday: Int,
        timebin: Int,
        whiteelo: Int,
        blackelo: Int,
        whiteratingdiff: Int,
        blackratingdiff: Int,
        timecontrol: Int
    )

    case class GameList(games: List[Game])
    case class PartGame(lines: List[String])
    case class Aggregator(gamelist: GameList, partgame: PartGame)

    val test = Source.fromFile("test").getLines.toList
    //val data = Source.fromFile("2016-02_reduced").mkString.split("\n\n\n")

    test.foldLeft(Aggregator(GameList(Nil), PartGame(Nil))) {
        (a, s) => {
            s match {
                case "\n" => parseGame(a.partgame) match {
                    case Some(game) => Aggregator(a.gamelist :: game, PartGame(Nil))
                    case _ => Aggregator(a.gamelist, PartGame(Nil))
                }
                case _ => Aggregator(a.gamelist, a.partgame :: s)
            }
        }
    }

    def timeConvert(control: String): Int =
        control.split("\\+") match {
            case Array(mins, sec) => (mins.toInt * 60) + (40 * sec.toInt)
            case _ => 0
        }

    def getWeekday(date: String): Int =
        DateTime.parse(date, DateTimeFormat.forPattern("yyyy.MM.dd").withZoneUTC()).getDayOfWeek()

    def getTimeBin(time: String): Int =
        DateTime.parse(time, DateTimeFormat.forPattern("HH:mm:ss").withZoneUTC()).getHourOfDay()

    def parseGame(gamestrings: PartGame): Option[Game] = {
        val t: Seq[String] = {
            gamestrings.list map {
                case s"""[${tagname} "${tagvalue}"]""" => tagvalue
            }
        }
        t match {
            case t if t.length == 9 => 
                Some(Game(white = t(0),
                    black = t(1),
                    weekday = getWeekday(t(2)),
                    timebin = getTimeBin(t(3)),
                    whiteelo = t(4).toInt,
                    blackelo = t(5).toInt,
                    whiteratingdiff = t(6).toInt,
                    blackratingdiff = t(7).toInt,
                    timecontrol = timeConvert(t(8))
                ))
            case _ => None
            }
    }

    //val games: Array[Game] = test flatMap parseGame

    //games distribution over a day
    //println(games.groupBy(i=>i.timebin).mapValues(_.size).toMap)
}
