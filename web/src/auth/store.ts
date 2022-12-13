import {v4 as uuidv4} from 'uuid'
import {createEffect, createStore} from 'effector'
import {User, UserId} from '../users'
import {mockUsers} from '../mockData'

const unknownUser = () => {
  const id = uuidv4().substring(0, 8)
  return {id, username: `Unknown user [${id}]`, online: true}
}

export const loginFx = createEffect((userId: UserId) => mockUsers.get(userId) || unknownUser())
export const logoutFx = createEffect(() => unknownUser())

export const $currentUserStore = createStore<User>(unknownUser())
  .on(
    loginFx.doneData,
    (_, payload) => payload
  )
  .on(
    logoutFx.doneData,
    (_, payload) => payload
  )