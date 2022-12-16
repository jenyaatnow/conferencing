import {createEffect, createStore} from 'effector'
import {ChatMessage, ChatType} from './model'
import {List, Map} from 'immutable'
import {identity} from '../utils'

export const addMessagesFx = createEffect(identity<List<ChatMessage>>)
export const sendMessageFx = createEffect(identity<ChatMessage>) // todo impl

// todo use userId as key for dm chats
export const $messagesStore = createStore<Map<ChatType, List<ChatMessage>>>(Map())
  .on(
    addMessagesFx.doneData,
    (state, payload) => payload.reduce((s, m) => {
      const chatMessages = (s.get(m.chatType) || List()).push(m)
      return s.set(m.chatType, chatMessages)
    }, state)
  )
