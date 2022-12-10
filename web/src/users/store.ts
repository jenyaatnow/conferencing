import {createEffect, createStore} from 'effector'
import {List} from 'immutable'
import {User} from './model'

function identity<T>(t: T): T {
  return t
}

export const addUsersFx = createEffect(identity<List<User>>)
export const removeUserFx = createEffect(identity<User>)

export const $usersStore = createStore<List<User>>(List())
  .on(
    addUsersFx.doneData,
    (state, data) => state.concat(data).sortBy(user => user.id)
  )
  .on(
    removeUserFx.doneData,
    (state, data) => state.filterNot(u => u.id === data.id)
  )
