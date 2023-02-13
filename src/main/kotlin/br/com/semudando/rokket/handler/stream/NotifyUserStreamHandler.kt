package br.com.semudando.rokket.handler.stream

import br.com.semudando.rokket.BotConfiguration
import br.com.semudando.rokket.EventHandler
import br.com.semudando.rokket.util.MessageHelper
import com.fasterxml.jackson.databind.JsonNode
import java.util.*

@Suppress("unused")
class NotifyUserStreamHandler(eventHandler: EventHandler, botConfiguration: BotConfiguration) :
  AbstractStreamHandler(eventHandler, botConfiguration) {
  override fun getHandledStream() = "stream-notify-user"

  override fun handleStreamMessage(data: JsonNode): List<List<Any>> {
    val eventNode = data.get("fields")?.get("eventName")

    @Suppress("MoveVariableDeclarationIntoWhen")
    val eventName = eventNode?.textValue()?.split("/")?.get(1)

    return when (eventName) {
      "subscriptions-changed" -> handleSubscriptionsChangedEvent(data)
//            "rooms-changed" -> handleRoomsChangedEvent(data)
      else -> emptyList()
    }
  }

  private fun handleSubscriptionsChangedEvent(data: JsonNode): List<List<Any>> {
    val args: JsonNode = data.get("fields")?.get("args") ?: return emptyList()

    return when (args.get(0).textValue()) {
      "inserted" -> handleSubscriptionInsertedMessage(args)
      "removed" -> handleSubscriptionRemovedMessage(args)
      else -> emptyList()
    }
  }

  private fun handleSubscriptionInsertedMessage(args: JsonNode): List<List<Any>> {
    return args
      .drop(1)
      .toList()
      .mapNotNull {
        val channelId = args.get(1).get("rid").textValue()
        val channelName = args.get(1).get("fname").textValue()
        val channelType = MessageHelper.instance.mapChannelType(args.get(1).get("t").textValue())

        br.com.semudando.rokket.Bot.subscriptionService.handleSubscription(channelId, channelName, channelType)
      }
      .map { listOf(it) }
  }

  private fun handleSubscriptionRemovedMessage(args: JsonNode): List<List<Any>> {
    return args
      .drop(1)
      .toList()
      .mapNotNull {
        val channelId = args.get(1).get("rid").textValue()

        br.com.semudando.rokket.Bot.subscriptionService.handleUnsubscription(channelId)
      }
      .map { listOf(it) }
  }
}
