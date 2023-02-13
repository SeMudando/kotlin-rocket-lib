package br.com.semudando.rokket

interface EventHandler {
  fun handleRoomMessage(channel: Channel, user: User, message: Message): List<OutgoingMessage>

  fun handleOwnMessage(channel: Channel, user: User, message: Message): List<OutgoingMessage>

  fun botInitialized()

  data class Channel(val id: String, val name: String?, val type: ChannelType)

  data class User(val id: String, val username: String)

  data class Message(val message: String, val botMessage: Boolean)

  enum class ChannelType {
    CHANNEL,
    DIRECT,
    OTHER
  }
}
