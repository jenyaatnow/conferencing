import {ChatMessages} from '../ws'
import {addMessagesFx} from './store'

export const handleChatMessages = (req: ChatMessages) => {
  addMessagesFx(req.messages)
}
