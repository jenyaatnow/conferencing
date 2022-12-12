import {createEffect, createStore} from 'effector'
import {List, Map} from 'immutable'
import {User, UserId} from './model'
import {identity} from '../utils'

export const connectUsersFx = createEffect(identity<List<User>>)
export const disconnectUserFx = createEffect(identity<UserId>)

export const $usersStore = createStore<List<User>>(List())
  .on(
    connectUsersFx.doneData,
    (state, payload) => {
      const connectedUserIds = payload.map(u => u.id)
      return state
        .filterNot(u => connectedUserIds.includes(u.id))
        .concat(payload)
    }
  )
  .on(
    disconnectUserFx.doneData,
    (state, payload) => {
      const user = state.find(u => u.id === payload)
      return user
        ? state.filterNot(u => u.id === payload).push({...user, online: false}).sortBy(user => user.id)
        : state
    }
  )

export const $usersMapStore = $usersStore.map(list => Map(list.map(user => [user.id, user])))
