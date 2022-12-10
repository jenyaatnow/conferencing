import {List} from 'immutable'

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
  connectedUsers: List<string>
}

export interface UserConnected extends InMessage {
  userId: string
}

export interface UserDisconnected extends InMessage {
  userId: string
}
