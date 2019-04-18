package main

import clients.chatwork.ChatWorkClient
import clients.slack.SlackClient

class Logic(chatwork: ChatWorkClient, slack: SlackClient) {

  def execute(roomid: String, cannelid: String): Either[String, Seq[String]] = for {
    chatworkMessages <- chatwork.room(roomid).message().list()
    slackMessages <- Right(chatworkMessages.map(m => m.body))
    posted <- Right(slackMessages.map(slack.message(cannelid).post))
  } yield {
    // TODO: 失敗したらリトライしたい
    // TODO: Leftを失いたくない → Eitherじゃなくなるはず。その上位としてEitherでくるむことはあると思う。
    posted.filter(_.isRight).map(_.right.get)
  }
}
