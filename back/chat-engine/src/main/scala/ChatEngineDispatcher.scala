package com.bravewave.conferencing.chat

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout
import cats.implicits.{catsSyntaxOptionId, none}
import com.bravewave.conferencing.chat.ChatActor.protocol.ChatActorMessage
import com.bravewave.conferencing.chat.ChatEngineDispatcher.protocol._
import com.bravewave.conferencing.chatgrpc.gen.{ChatMessageRequest, SpawnChatRequest, SpawnChatResponse}
import com.bravewave.conferencing.conf.shared.{ChatId, ChatTypes}

import scala.util.{Failure, Success}

object ChatEngineDispatcher {

  def apply(): Behavior[ChatEngineDispatcherMessage] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case SpawnConfChat(in, replyTo) =>
        val chatId = s"${ChatTypes.conf}@${in.conferenceId}"
        spawnChat(ctx, chatId)
        replyTo ! SpawnChatResponse(chatId)
        Behaviors.same

      case message @ SendChatMessage(in, _) =>
        resolveChatId(in)
          .foreach { chatId =>
            implicit val timeout: Timeout = Config.actorLookupTimeout
            val serviceKey = ServiceKey[ChatActorMessage](chatId)
            ctx.ask(ctx.system.receptionist, Receptionist.Find(serviceKey)) {
              case Success(listing) =>
                listing.serviceInstances(serviceKey).iterator.nextOption() match {
                  case Some(chatActor) => chatActor ! message; Ignore
                  case None => spawnChat(ctx, chatId) ! message; Ignore
                }

              case Failure(exception) =>
                ctx.log.error(s"Chat actor [id='$chatId'] lookup error", exception); Ignore
            }
          }

        Behaviors.same

      case _ => Behaviors.same
    }
  }

  private def spawnChat(ctx: ActorContext[ChatEngineDispatcherMessage], chatId: String): ActorRef[ChatActorMessage] = {
    val serviceKey = ServiceKey[ChatActorMessage](chatId)
    val chatActor = ctx.spawn(ChatActor(), s"chat_$chatId")
    ctx.system.receptionist ! Receptionist.Register(serviceKey, chatActor)

    ctx.log.info(s"Spawned new chat '$chatId'")
    chatActor
  }

  private def resolveChatId(msg: ChatMessageRequest): Option[ChatId] = msg match {
    case ChatMessageRequest(_, conferenceId, "conf", _, _, _, _) =>
      s"${ChatTypes.conf}@$conferenceId".some

    case ChatMessageRequest(_, conferenceId, "dm", from, Some(to), _, _) =>
      List(from, to).sorted.mkString(s"${ChatTypes.dm}@$conferenceId:", ":", "").some

    case _ => none
  }

  object protocol {
    trait ChatEngineDispatcherMessage
    final case object Ignore extends ChatEngineDispatcherMessage
    final case class SpawnConfChat(in: SpawnChatRequest, replyTo: ActorRef[SpawnChatResponse]) extends ChatEngineDispatcherMessage
  }
}
