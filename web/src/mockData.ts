import {Map} from 'immutable'
import {User, UserId} from './users'

export const hank = {id: "Hank", username: "Hank", online: true}
export const mom = {id: "Mom", username: "Mom", online: true}
export const jello = {id: "Jello", username: "Jello", online: true}
export const ruby = {id: "Ruby", username: "Ruby", online: true}
export const mockUsers = Map<UserId, User>()
  .set("Hank", hank)
  .set("Mom", mom)
  .set("Jello", jello)
  .set("Ruby", ruby)
