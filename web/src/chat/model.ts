import {UserId} from '../users'
import {Moment} from 'moment'

export const ChatTypes = {
  Conf: 'conf',
  DM: 'dm',
}

export type ChatType = typeof ChatTypes.Conf | typeof ChatTypes.DM

export interface ChatMessage {
  chatType: ChatType
  from: UserId
  to?: UserId
  text: string
  timestamp?: Moment
}
