import {List} from 'immutable'
import {UserId} from '../users'

export const InMessageTypes = {
  UserConnected: 'UserConnected',
  UserDisconnected: 'UserDisconnected',
  ConferenceDetails: 'ConferenceDetails',
}

export type InMessageType =
  typeof InMessageTypes.UserConnected
  | typeof InMessageTypes.UserDisconnected
  | typeof InMessageTypes.ConferenceDetails


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
