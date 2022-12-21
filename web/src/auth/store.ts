import {v4 as uuidv4} from 'uuid'
import {createEffect, createStore} from 'effector'
import {AuthUser, UserOrigin} from '../users'
import {ChatStrings} from '../strings'

const generateId = () => uuidv4().substring(0, 8)

const alien = () => {
  const id = generateId()
  return {id, username: ChatStrings.UnknownUser(id), online: true, origin: UserOrigin.ALIEN}
}

export const logoutFx = createEffect(() => alien())

export const logInWithTempUserFx = createEffect((username: string) => {
  return {id: generateId(), username, online: true, origin: UserOrigin.TEMP}
})

export const $currentUserStore = createStore<AuthUser>(alien())
  .on(
    logInWithTempUserFx.doneData,
    (_, payload) => payload
  )
  .on(
    logoutFx.doneData,
    (_, payload) => payload
  )
