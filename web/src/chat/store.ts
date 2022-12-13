import {createEffect, createStore} from 'effector'
import {ChatMessage, ChatType} from './model'
import {List, Map} from 'immutable'
import {identity} from '../utils'
import {mockMessages} from '../mockData'

export const addMessageFx = createEffect(identity<ChatMessage>)

// todo use userId as key for dm chats
export const $messagesStore = createStore<Map<ChatType, List<ChatMessage>>>(mockMessages)
  .on(
    addMessageFx.doneData,
    (state, payload) => {
      const chatMessages = (state.get(payload.chatType) || List()).push(payload)
      state.set(payload.chatType, chatMessages)
    }
  )
