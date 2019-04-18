package clients.slack

import java.util.Date

import com.softwaremill.sttp.{HttpURLConnectionBackend, Id, SttpBackend}

trait SlackClient {

  def message(cannelid: String): MessageAPI

}

trait MessageAPI {

  def post(message: String): Either[String, String]
}

case class Message(body: String, send_time: Date)

case class PostMessageResponse(ok: Boolean)

class SlackClientMock extends SlackClient {
  override def message(str: String): MessageAPI = (message: String) => {
    Right(message)
  }
}

class SlackClientImpl(token: String)
                     (implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend())
  extends SlackClient {

  import com.softwaremill.sttp._

  private val baseUrl = "https://slack.com/api/"


  // TODO: SlackAPIを調べて処理を書く
  override def message(cannelid: String): MessageAPI = new MessageAPI {
    override def post(message: String): Either[String, String] = {

      def parseMessage(a: String): String = {
        import org.json4s._
        import org.json4s.native.JsonMethods._
        implicit val formats: Formats = DefaultFormats


        println(parse(a))

        val extracted = parse(a).extract[PostMessageResponse]

        extracted.ok.toString

      }

      val res = sttp
        .body(Map(
          "token" -> token,
          "channel" -> cannelid,
          "text" -> message
        )
        )
        .post(uri"$baseUrl/chat.postMessage")
        .response(asString.map(parseMessage))
        .send()

      res.body

    }
  }
}