import {UserId} from './users'

export const ChatStrings = {
  Offline: 'offline',
  You: 'you',
  UnknownUser: (userId: UserId) => `Unknown user [${userId}]`
}
