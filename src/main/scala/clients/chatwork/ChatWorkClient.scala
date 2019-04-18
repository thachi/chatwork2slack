package clients.chatwork

import java.util.Date

import com.softwaremill.sttp.{HttpURLConnectionBackend, Id, SttpBackend}
import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JInt, JLong, JNull}

trait ChatWorkClient {

  def room(roomid: String): RoomAPI

  def contact(): ContactAPI
}

trait RoomAPI {
  def message(): MessageAPI
}

trait ContactAPI {
  def list(): Either[String, Seq[Contact]]
}

case class Contact(account_id: Int,
                   room_id: Int,
                   name: String,
                   chatwork_id: String)

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

  override def contact(): ContactAPI = new ContactAPI {
    override def list(): Either[String, Seq[Contact]] = Right(Seq(
      Contact(1, 1, "taro", "taro_id"),
      Contact(2, 2, "jiro", "jiro_id"),
      Contact(3, 3, "saburo", "saburo_id")
    ))
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

  override def contact(): ContactAPI = new ContactAPI {
    override def list(): Either[String, Seq[Contact]] = {
      def parseMessage(a: String): Seq[Contact] = {
        import org.json4s._
        import org.json4s.native.JsonMethods._
        implicit val formats: Formats = DefaultFormats

        parse(a).extract[Seq[Contact]]
      }

      val res = sttp.get(uri"$baseUrl/contacts")
        .headers("X-ChatWorkToken" -> token)
        .response(asString.map(parseMessage))
        .send()

      res.body
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
