import {v4 as uuidv4} from 'uuid'
import {List} from 'immutable'
import moment from 'moment/moment'
import {splitSpeeches} from './textSpeech'
import {ChatTypes} from './model'
import {hank, mockUsers, mom} from '../mockData'

test('should group messages to speeches', () => {
  const m1 = {message: {id: uuidv4(), chatType: ChatTypes.Conf, from: hank.id, text: "Hi, mom, it's me", timestamp: moment()}, delivered: true}
  const m2 = {message: {id: uuidv4(), chatType: ChatTypes.Conf, from: mom.id, text: "Hi, son, glad to see you", timestamp: moment().add(10, 'minutes')}, delivered: true}
  const m3 = {message: {id: uuidv4(), chatType: ChatTypes.Conf, from: mom.id, text: "Haven't seen you for a long time", timestamp: moment().add(12, 'minutes')}, delivered: true}
  const m4 = {message: {id: uuidv4(), chatType: ChatTypes.Conf, from: mom.id, text: "How're you doing, sweet?", timestamp: moment().add(15, 'minutes')}, delivered: true}
  const m5 = {message: {id: uuidv4(), chatType: ChatTypes.Conf, from: hank.id, text: "All fine, mom, thanx", timestamp: moment().add(20, 'minutes')}, delivered: true}
  const m6 = {message: {id: uuidv4(), chatType: ChatTypes.Conf, from: hank.id, text: "What about you?", timestamp: moment().add(30, 'minutes')}, delivered: true}
  const messages = List.of(m1, m2, m3, m4, m5, m6)

  const expected = List.of(
    {user: hank, messages: List.of(m1)},
    {user: mom, messages: List.of(m2, m3, m4)},
    {user: hank, messages: List.of(m5, m6)},
  )

  const actual = splitSpeeches(messages, mockUsers)
  expect(actual).toStrictEqual(expected)
})
