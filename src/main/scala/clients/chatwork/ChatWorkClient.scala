package clients.chatwork

import java.util.Date

import com.softwaremill.sttp.{HttpURLConnectionBackend, Id, SttpBackend}
import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JInt, JLong, JNull}

trait ChatWorkClient {

  def room(roomid: String): RoomAPI

}

trait RoomAPI {
  def message(): MessageAPI
}

trait MessageAPI {
  def list(force: Boolean = false): Either[String, Seq[Message]]
}

case class Message(body: String, send_time: Date)

class ChatworkClientMock extends ChatWorkClient {
  override def room(roomid: String): RoomAPI = new RoomAPI {
    override def message(): MessageAPI = new MessageAPI {
      override def list(force: Boolean): Either[String, Seq[Message]] =
        Right(Seq(
          Message("こんにちは1", new Date),
          Message("こんにちは2", new Date),
          Message("こんにちは3", new Date)
        ))
    }
  }
}

class ChatWorkClientImpl(token: String)
                        (implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend())
  extends ChatWorkClient {

  import com.softwaremill.sttp._

  private val baseUrl = "https://api.chatwork.com/v2"

  override def room(roomid: String): RoomAPI = new RoomAPI {
    override def message(): MessageAPI = new MessageAPI {
      override def list(force: Boolean): Either[String, Seq[Message]] = {
        def parseMessage(a: String): Seq[Message] = {
          import org.json4s._
          import org.json4s.native.JsonMethods._
          implicit val formats: Formats = DefaultFormats + ChatWorkDateSerializer

          parse(a).extract[Seq[Message]]
        }

        val res = sttp.get(uri"$baseUrl/rooms/$roomid/messages?force=${if (force) 1 else 0}")
          .headers("X-ChatWorkToken" -> token)
          .response(asString.map(parseMessage))
          .send()

        res.body
      }
    }
  }
}


object ChatWorkDateSerializer extends CustomSerializer[Date](format => ( {
  case JInt(s) => new Date(s.toLong * 1000)
  case JLong(s) => new Date(s * 1000)
  case JNull => null
}, {
  case x: Date => JLong(x.getTime / 1000)
}))
