import {UserId} from '../users'
import {Moment} from 'moment'

export const ChatTypes = {
  Conf: 'conf',
  DM: 'dm',
}

export type ChatType = typeof ChatTypes.Conf | typeof ChatTypes.DM

export interface ChatMessage {
  id: string
  chatType: ChatType
  from: UserId
  to?: UserId
  text: string
  timestamp?: Moment
}

export interface ChatMessageStoreEntry {
  message: ChatMessage,
  delivered: boolean
}
