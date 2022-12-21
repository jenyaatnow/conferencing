import {
  ChatMessages,
  ConferenceDetails,
  ErrorWsMessage,
  InMessage,
  InMessageTypes,
  UserConnected,
  UserDisconnected
} from './InMessage'
import {handleUserConnected, handleUserDisconnected, UserId} from '../users'
import {handleChatMessages} from '../chat'
import {ConferenceId, handleConferenceDetails} from '../conference'
import {handleError} from '../error'
import {identity, userLocale} from '../utils'
import {createEffect, createStore} from 'effector'

interface WsConnectArgs {
  confId: ConferenceId
  userId: UserId
  username: string
}

export const wsConnectFx = createEffect((args: WsConnectArgs) => {
  return new WebSocket(`ws://0.0.0.0:8080/conference/${args.confId}?userId=${args.userId}&username=${args.username}&locale=${userLocale}`)
})

export const wsSendFx = createEffect(identity<any>)

const $ws = createStore<WebSocket | null>(null)
  .on(
    wsSendFx.doneData,
    (ws, payload) => {
      if (ws && ws.readyState === 1) {
        console.log('Send message', JSON.stringify(payload))
        ws.send(JSON.stringify(payload))
      }

      return ws
    }
  )
  .on(
    wsConnectFx.doneData,
    (_, ws) => {
      ws.onmessage = msg => {
        console.log('Got message', msg.data)

        const message = JSON.parse(msg.data) as InMessage
        switch (message.type) {
          case InMessageTypes.ConferenceDetails:
            handleConferenceDetails(message as ConferenceDetails)
            break

          case InMessageTypes.UserConnected:
            handleUserConnected(message as UserConnected)
            break

          case InMessageTypes.UserDisconnected:
            handleUserDisconnected(message as UserDisconnected)
            break

          case InMessageTypes.ChatMessages:
            handleChatMessages(message as ChatMessages)
            break

          case InMessageTypes.ErrorWsMessage:
            handleError(message as ErrorWsMessage)
            break

          default: break
        }
      }

      ws.onerror = error => {
        console.log('Got error', JSON.stringify(error))
      }

      return ws
    }
  )
