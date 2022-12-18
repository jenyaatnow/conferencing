import {ConferenceDetails} from '../ws'
import {connectUsersFx} from '../users'
import {receiveMessagesFx} from '../chat'

export const handleConferenceDetails = (message: ConferenceDetails) => {
  connectUsersFx(message.users.map(user => ({id: user.userId, username: user.username, online: user.online})))
  receiveMessagesFx(message.chatMessages)
}
