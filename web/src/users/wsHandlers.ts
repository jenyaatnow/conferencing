import {UserConnected, UserDisconnected} from '../ws'
import {connectUsersFx, disconnectUserFx} from './store'
import {List} from 'immutable'

export const handleUserConnected = (message: UserConnected) => {
  connectUsersFx(List.of({id: message.userId, username: message.username, online: true}))
}

export const handleUserDisconnected = (message: UserDisconnected) => {
  disconnectUserFx(message.userId)
}
