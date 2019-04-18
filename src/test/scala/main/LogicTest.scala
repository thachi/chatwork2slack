package main

import clients.chatwork.ChatworkClientMock
import clients.slack.SlackClientMock
import org.scalatest.FunSuite

class LogicTest extends FunSuite {

  val logic = new Logic(new ChatworkClientMock, new SlackClientMock)

  test("正常に応答するMockを使った場合に正常終了すること") {
    val actual = logic.execute("room", "cannel")
    assert(actual.isRight)
  }
}
