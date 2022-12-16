import {createEffect, createStore} from 'effector'
import {ChatMessage, ChatMessageStoreEntry, ChatType} from './model'
import {List, Map} from 'immutable'

export const receiveMessagesFx = createEffect((messages: List<ChatMessage>) => {
  return messages.map(message => ({message, delivered: true}))
})

export const sendMessageFx = createEffect((message: ChatMessage) => {
  return {message, delivered: false}
})

// todo use userId as key for dm chats
export const $messagesStore = createStore<Map<ChatType, List<ChatMessageStoreEntry>>>(Map())
  .on(
    receiveMessagesFx.doneData,
    (state, payload) => payload.reduce((accState, newMessage) => {
      const chatId = newMessage.message.chatType
      const chatMessages = accState.get(chatId) || List()
      const messageIdx = chatMessages.findIndex(existedMessage => existedMessage.message.id === newMessage.message.id)
      const updatedChatMessages = messageIdx >= 0
        ? chatMessages.remove(messageIdx).insert(messageIdx, newMessage)
        : chatMessages.push(newMessage)

      return accState.set(chatId, updatedChatMessages)
    }, state)
  )
  .on(
    sendMessageFx.doneData,
    (state, payload) => {
      const chatId = payload.message.chatType
      const chatMessages = (state.get(chatId) || List()).push(payload)
      return state.set(chatId, chatMessages)
    }
  )
