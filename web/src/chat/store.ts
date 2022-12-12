import {createEffect, createStore} from 'effector'
import {ChatType, ChatMessage} from './model'
import {List, Map} from 'immutable'
import {identity} from '../utils'
import moment from 'moment'

export const addMessageFx = createEffect(identity<ChatMessage>)

const mockMessages = Map<ChatType, List<ChatMessage>>().set(
  'conf',
  List.of(
    {chatType: 'conf', userId: 'Hank', text: "Hi, mom, it's me", timestamp: moment()},
    {chatType: 'conf', userId: 'Mom', text: "Hi, son, glad to see you", timestamp: moment().add(10, 'minutes')},
    {chatType: 'conf', userId: 'Mom', text: "Haven't seen you for a long time", timestamp: moment().add(12, 'minutes')},
    {chatType: 'conf', userId: 'Mom', text: "How're you doing, sweet?", timestamp: moment().add(15, 'minutes')},
    {chatType: 'conf', userId: 'Hank', text: "All fine, mom, thanx", timestamp: moment().add(20, 'minutes')},
    {chatType: 'conf', userId: 'Hank', text: "What about you?", timestamp: moment().add(25, 'minutes')},
  )
)

// todo use userId as key for dm chats
export const $messagesStore = createStore<Map<ChatType, List<ChatMessage>>>(mockMessages)
  .on(
    addMessageFx.doneData,
    (state, payload) => {
      const chatMessages = (state.get(payload.chatType) || List()).push(payload)
      state.set(payload.chatType, chatMessages)
    }
  )
