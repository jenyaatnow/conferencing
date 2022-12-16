import {UserId} from '../users'
import {ChatMessage, ChatType} from '../chat'

export const OutMessageTypes = {
  ChatMessageReceived: 'ChatMessageReceived',
}

export type OutMessageType =
  typeof OutMessageTypes.ChatMessageReceived


export interface OutMessage {
  type: OutMessageType
}

export interface ChatMessageReceived extends OutMessage {
  chatType: ChatType
  from: UserId
  to?: UserId
  text: string
}

export const buildChatMessageReceived = (chatMessage: ChatMessage) => {
  return {
    ...chatMessage,
    type: OutMessageTypes.ChatMessageReceived,
  }
}
