import {ChatMessages} from '../ws'
import {receiveMessagesFx} from './store'

export const handleChatMessages = (req: ChatMessages) => {
  receiveMessagesFx(req.messages)
}
