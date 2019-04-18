package clients.slack

import org.scalatest.FunSuite

class SlackClientImplTest extends FunSuite {

  for {
    token <- Option(System.getProperty("slack.token"))
      .toRight {
        //TODO:とりあえずプロパティがない場合はignoreにしている。IntegrationTestとすべき。
        ignore("システムプロパティに'slack.token'が含まれていません。") {}
      }

    channelid <- Option(System.getProperty("slack.channelid"))
      .toRight {
        //TODO:とりあえずプロパティがない場合はignoreにしている。IntegrationTestとすべき。
        ignore("システムプロパティに'slack.channelid'が含まれていません。") {}
      }
  } {
    val client = new SlackClientImpl(token)

    test("testMessage") {
      val Right(result) = client.message(channelid).post("こんにちは。")

      //TODO: 応答を解釈するようになれば修正する
      assert(result == "true")

    }
  }
}
