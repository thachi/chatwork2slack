package main

import clients.chatwork.ChatWorkClientImpl
import clients.slack.SlackClientImpl

object Main {

  def main(args: Array[String]): Unit = {
    for {
      room <- findArg(args, 0)
      channel <- findArg(args, 1)
      logic <- createLogic()
    } yield {

      println("roomid: " + room)
      println("cannelid: " + channel)

      logic.execute(room, channel)
    }
  } match {
    case Right(_) =>
      //TODO:応答から実行状態を表示するといいかも。
      println("done.")
    case Left(message) =>

      //TODO: 足りないエラーを一覧で全部表示したい。
      println("error: " + message)
  }

  private def findArg(args: Array[String], index: Int): Either[String, String] =
    args.drop(index).headOption match {
      case Some(room) => Right(room)
      case _ => Left(s"""${index + 1}つ目の引数が指定されていません。""")
    }

  private def createLogic(): Either[String, Logic] = {
    val chatworkToken = Option(System.getProperty("chatwork.token"))
    val slackToken = Option(System.getProperty("slack.token"))

    (chatworkToken, slackToken) match {
      case (Some(c), Some(s)) =>
        Right(new Logic(new ChatWorkClientImpl(c), new SlackClientImpl(s)))
      case _ =>
        Left("システムプロパティに 'chatwork.token', 'slack.token' のいずれか、あるいは両方が含まれていません。")
    }
  }

}
