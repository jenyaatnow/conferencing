import {ChatMessage} from '../chat'
import {InMessageTypes} from './InMessage'

export const OutMessageTypes = {
  ChatMessageReceived: 'ChatMessageReceived',
}

export type OutMessageType =
  typeof OutMessageTypes.ChatMessageReceived


export interface OutMessage {
  type: OutMessageType
}

export const buildChatMessageReceived = (chatMessage: ChatMessage) => ({
  ...chatMessage,
  type: OutMessageTypes.ChatMessageReceived,
})

export const buildWebRtcOffer = (offer: RTCSessionDescriptionInit) => ({
  offer,
  type: InMessageTypes.WebRtcOffer
})

export const buildWebRtcAnswer = (answer: RTCSessionDescriptionInit) => ({
  answer,
  type: InMessageTypes.WebRtcAnswer
})
