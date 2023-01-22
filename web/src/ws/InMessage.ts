import {List} from 'immutable'
import {UserId} from '../users'
import {ChatMessage} from '../chat'
import {Moment} from 'moment'

export const InMessageTypes = {
  UserConnected: 'UserConnected',
  UserDisconnected: 'UserDisconnected',
  ConferenceDetails: 'ConferenceDetails',
  ChatMessages: 'ChatMessages',
  ErrorWsMessage: 'Error',
  WebRtcOffer: 'WebRtcOffer',
  WebRtcAnswer: 'WebRtcAnswer',
}

export type InMessageType =
  typeof InMessageTypes.UserConnected
  | typeof InMessageTypes.UserDisconnected
  | typeof InMessageTypes.ConferenceDetails
  | typeof InMessageTypes.ChatMessages
  | typeof InMessageTypes.ErrorWsMessage
  | typeof InMessageTypes.WebRtcOffer
  | typeof InMessageTypes.WebRtcAnswer


export interface InMessage {
  type: InMessageType
}

export interface ConferenceDetails extends InMessage {
  users: List<{
    userId: UserId
    username: string
    online: boolean
  }>
  chatMessages: List<ChatMessage>
}

export interface UserConnected extends InMessage {
  userId: UserId
  username: string
}

export interface UserDisconnected extends InMessage {
  userId: UserId
}

export interface ChatMessages extends InMessage {
  messages: List<ChatMessage>
}

export interface ErrorWsMessage extends InMessage {
  message: string
  timestamp: Moment
}

export interface WebRtcOffer extends InMessage {
  offer: RTCSessionDescriptionInit,
}

export interface WebRtcAnswer extends InMessage {
  answer: RTCSessionDescriptionInit
}
