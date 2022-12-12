import {List, Map} from 'immutable'
import moment from 'moment/moment'
import {splitSpeeches} from './textSpeech'
import {ChatType} from './model'
import {User, UserId} from '../users'

const hank = {id: "1", username: "Hank", online: true}
const mom = {id: "2", username: "Mom", online: true}
const users = Map<UserId, User>()
  .set("1", hank)
  .set("2", mom)

test('should group messages to speeches', () => {
  const m1 = {chatType: ('conf' as ChatType), userId: '1', text: "Hi, mom, it's me", timestamp: moment()}
  const m2 = {chatType: ('conf' as ChatType), userId: '2', text: "Hi, son, glad to see you", timestamp: moment().add(10, 'minutes')}
  const m3 = {chatType: ('conf' as ChatType), userId: '2', text: "Haven't seen you for a long time", timestamp: moment().add(12, 'minutes')}
  const m4 = {chatType: ('conf' as ChatType), userId: '2', text: "How're you doing, sweet?", timestamp: moment().add(15, 'minutes')}
  const m5 = {chatType: ('conf' as ChatType), userId: '1', text: "All fine, mom, thanx", timestamp: moment().add(20, 'minutes')}
  const m6 = {chatType: ('conf' as ChatType), userId: '1', text: "What about you?", timestamp: moment().add(30, 'minutes')}
  const messages = List.of(m1, m2, m3, m4, m5, m6)

  const expected = List.of(
    {user: hank, messages: List.of(m1)},
    {user: mom, messages: List.of(m2, m3, m4)},
    {user: hank, messages: List.of(m5, m6)},
  )

  const actual = splitSpeeches(messages, users)
  expect(actual).toStrictEqual(expected)
})
