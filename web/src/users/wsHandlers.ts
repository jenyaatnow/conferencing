import {ConferenceDetails, UserConnected, UserDisconnected} from '../ws'
import {connectUsersFx, disconnectUserFx} from './store'
import {List} from 'immutable'

export const handleConferenceDetails = (message: ConferenceDetails) => {
  connectUsersFx(message.users.map(user => ({id: user.userId, username: user.username, online: user.online})))
}

export const handleUserConnected = (message: UserConnected) => {
  connectUsersFx(List.of({id: message.userId, username: message.username, online: true}))
}

export const handleUserDisconnected = (message: UserDisconnected) => {
  disconnectUserFx(message.userId)
}
