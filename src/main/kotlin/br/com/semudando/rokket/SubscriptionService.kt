package br.com.semudando.rokket

import br.com.semudando.rokket.websocket.SubscribeMessage
import br.com.semudando.rokket.websocket.UnsubscribeMessage
import java.util.UUID

internal class SubscriptionService {
  private val channelsById = HashMap<String, ChannelData>()
  private val channelsByName = HashMap<String, ChannelData>()
  private val newestTimestampsSeen = HashMap<String, Long>()

  fun handleSubscription(
    channelId: String,
    channelName: String?,
    channelType: EventHandler.ChannelType,
  ): SubscribeMessage? {

    if (channelsById.contains(channelId)) {
      return null
    }
    val subscriptionId = UUID.randomUUID().toString()
    val channelData = ChannelData(channelId, channelName, channelType, subscriptionId)
    if (channelName != null) {
      channelsByName[channelName] = channelData
    }
    channelsById[channelId] = channelData

    return SubscribeMessage(id = subscriptionId, name = "stream-room-messages", params = listOf(channelId, false))
  }

  fun handleUnsubscription(channelId: String): UnsubscribeMessage? {

    val channel = channelsById[channelId]
    if (channel == null) {
      return null
    }

    channelsById.remove(channel.id)
    if (channel.name != null) {
      channelsByName.remove(channel.name)
    }

    return UnsubscribeMessage(id = channel.subscriptionId)
  }

  fun getChannelIdByName(roomName: String) = channelsByName[roomName]?.id

  fun getChannelNameById(roomId: String) = channelsById[roomId]?.name

  fun getNewestTimestampSeen(roomId: String): Long? = newestTimestampsSeen[roomId]

  fun updateNewestTimestampSeen(roomId: String, timestamp: Long) {
    newestTimestampsSeen[roomId] = timestamp
  }

  fun getRoomType(roomId: String) = channelsById[roomId]?.type

  fun reset() {
    channelsById.clear()
    channelsByName.clear()
    newestTimestampsSeen.clear()
  }
}

internal data class ChannelData(
  val id: String,
  val name: String?,
  val type: EventHandler.ChannelType,
  val subscriptionId: String,
)
