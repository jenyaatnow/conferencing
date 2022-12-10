import {ConferenceDetails, InMessage, InMessageTypes, UserConnected, UserDisconnected} from './InMessage'
import {addUsersFx, removeUserFx} from '../users'
import {List} from 'immutable'

const confId = prompt("Conference ID")
const userId = prompt("User ID")
export const WS = new WebSocket(`ws://0.0.0.0:8080/conference/${confId}?userId=${userId}`)

WS.onmessage = msg => {
  console.log('Got message', msg.data)

  const message = JSON.parse(msg.data) as InMessage
  switch (message.type) {
    case InMessageTypes.ConferenceDetails:
      addUsersFx((message as ConferenceDetails).connectedUsers.map(userId => ({"id": userId})))
      break

    case InMessageTypes.UserConnected:
      addUsersFx(List.of({"id": (message as UserConnected).userId}))
      break

    case InMessageTypes.UserDisconnected:
      removeUserFx({"id": (message as UserDisconnected).userId})
      break

    default: break
  }
}

WS.onerror = error => {
  console.log('Got error', JSON.stringify(error))
}


export const send = (msg: any): void => {
  if (WS.readyState === 1) {
    WS.send(JSON.stringify(msg))
  }
}
