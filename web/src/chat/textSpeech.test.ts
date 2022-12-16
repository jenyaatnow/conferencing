import {List} from 'immutable'
import moment from 'moment/moment'
import {splitSpeeches} from './textSpeech'
import {ChatTypes} from './model'
import {hank, mockUsers, mom} from '../mockData'

test('should group messages to speeches', () => {
  const m1 = {chatType: ChatTypes.Conf, from: hank.id, text: "Hi, mom, it's me", timestamp: moment()}
  const m2 = {chatType: ChatTypes.Conf, from: mom.id, text: "Hi, son, glad to see you", timestamp: moment().add(10, 'minutes')}
  const m3 = {chatType: ChatTypes.Conf, from: mom.id, text: "Haven't seen you for a long time", timestamp: moment().add(12, 'minutes')}
  const m4 = {chatType: ChatTypes.Conf, from: mom.id, text: "How're you doing, sweet?", timestamp: moment().add(15, 'minutes')}
  const m5 = {chatType: ChatTypes.Conf, from: hank.id, text: "All fine, mom, thanx", timestamp: moment().add(20, 'minutes')}
  const m6 = {chatType: ChatTypes.Conf, from: hank.id, text: "What about you?", timestamp: moment().add(30, 'minutes')}
  const messages = List.of(m1, m2, m3, m4, m5, m6)

  const expected = List.of(
    {user: hank, messages: List.of(m1)},
    {user: mom, messages: List.of(m2, m3, m4)},
    {user: hank, messages: List.of(m5, m6)},
  )

  const actual = splitSpeeches(messages, mockUsers)
  expect(actual).toStrictEqual(expected)
})
