import {ChatMessages, ConferenceDetails, InMessage, InMessageTypes, UserConnected, UserDisconnected} from './InMessage'
import {handleUserConnected, handleUserDisconnected} from '../users'
import {loginFx} from '../auth'
import {handleChatMessages} from '../chat'
import {handleConferenceDetails} from '../conference'

const confId = prompt("Conference ID")
const userId = prompt("User ID")
loginFx(userId || "")

export const WS = new WebSocket(`ws://0.0.0.0:8080/conference/${confId}?userId=${userId}`)

WS.onmessage = msg => {
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

    default: break
  }
}

WS.onerror = error => {
  console.log('Got error', JSON.stringify(error))
}


export const send = (msg: any): void => {
  if (WS.readyState === 1) {
    console.log('Send message', JSON.stringify(msg))
    WS.send(JSON.stringify(msg))
  }
}
