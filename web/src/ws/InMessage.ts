import {List} from 'immutable'
import {UserId} from '../users'
import {ChatMessage} from '../chat'

export const InMessageTypes = {
  UserConnected: 'UserConnected',
  UserDisconnected: 'UserDisconnected',
  ConferenceDetails: 'ConferenceDetails',
  ChatMessages: 'ChatMessages',
}

export type InMessageType =
  typeof InMessageTypes.UserConnected
  | typeof InMessageTypes.UserDisconnected
  | typeof InMessageTypes.ConferenceDetails
  | typeof InMessageTypes.ChatMessages


export interface InMessage {
  type: InMessageType
}

export interface ConferenceDetails extends InMessage {
  users: List<{
    userId: UserId
    username: string
    online: boolean
  }>
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
