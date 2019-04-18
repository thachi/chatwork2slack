package clients.chatwork

import org.scalatest.FunSuite

class ChatWorkClientImplTest extends FunSuite {

  for {
    token <- Option(System.getProperty("chatwork.token"))
      .toRight {
        //TODO:とりあえずプロパティがない場合はignoreにしている。IntegrationTestとすべき。
        ignore("システムプロパティに'chatwork.token'が含まれていません。") {}
      }

    roomid <- Option(System.getProperty("chatwork.roomid"))
      .toRight {
        //TODO:とりあえずプロパティがない場合はignoreにしている。IntegrationTestとすべき。
        ignore("システムプロパティに'chatwork.roomid'が含まれていません。") {}
      }
  } {
    test("MessageAPIで一覧を取得し空でないこと") {
      val client = new ChatWorkClientImpl(token)
      val Right(messages) = client.room(roomid).message().list(force = true)
      assert(messages.nonEmpty)
    }
  }
}
