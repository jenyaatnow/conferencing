import {List, Map} from 'immutable'
import {ChatMessage, ChatType} from './chat'
import moment from 'moment/moment'
import {User, UserId} from './users'

export const mockMessages = Map<ChatType, List<ChatMessage>>().set(
  'conf',
  List.of(
    {chatType: 'conf', userId: 'Hank', text: "Hi, mom, it's me", timestamp: moment()},
    {chatType: 'conf', userId: 'Mom', text: "Hi, son, glad to see you", timestamp: moment().add(10, 'minutes')},
    {chatType: 'conf', userId: 'Mom', text: "Haven't seen you for a long time. Why didn't you call me?", timestamp: moment().add(12, 'minutes')},
    {chatType: 'conf', userId: 'Mom', text: "How're you doing, sweet?", timestamp: moment().add(15, 'minutes')},
    {chatType: 'conf', userId: 'Hank', text: "All fine, mom, thanx", timestamp: moment().add(20, 'minutes')},
    {chatType: 'conf', userId: 'Hank', text: "What about you?", timestamp: moment().add(25, 'minutes')},
  )
)

export const hank = {id: "Hank", username: "Hank", online: true}
export const mom = {id: "Mom", username: "Mom", online: true}
export const jello = {id: "Jello", username: "Jello", online: true}
export const ruby = {id: "Ruby", username: "Ruby", online: true}
export const mockUsers = Map<UserId, User>()
  .set("Hank", hank)
  .set("Mom", mom)
  .set("Jello", jello)
  .set("Ruby", ruby)
