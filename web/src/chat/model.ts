import {UserId} from '../users'
import {Moment} from 'moment'

export type ChatType = 'conf' | 'dm'

export interface ChatMessage {
  chatType: ChatType
  userId: UserId
  text: string
  timestamp: Moment
}
