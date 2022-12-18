import {v4 as uuidv4} from 'uuid'
import {createEffect, createStore} from 'effector'
import {User, UserId} from '../users'
import {ChatStrings} from '../strings'

const unknownUser = () => {
  const id = uuidv4().substring(0, 8)
  return {id, username: ChatStrings.UnknownUser(id), online: true}
}

const obtainUserDetails = (id: UserId) => {
  // todo find real username somewhere
  return {id, username: id, online: true}
}

export const loginFx = createEffect((userId: UserId) => obtainUserDetails(userId))
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