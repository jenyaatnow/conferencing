import {ChatMessage, ChatType} from './model'
import {List} from 'immutable'
import {useStoreMap} from 'effector-react'
import {$messagesStore} from './store'
import {$usersMapStore, UserId} from '../users'
import {splitSpeeches} from './textSpeech'
import {TextSpeechComponent} from './TextSpeechComponent'

interface ChatComponentProps {
  chatType: ChatType
  currentUserId: UserId
  dmUserId?: UserId
}

export const ChatComponent = (props: ChatComponentProps) => {
  const chatUserIds = useStoreMap($messagesStore, s => s.get(props.chatType) || List<ChatMessage>())
    .map(m => m.userId)
    .toSet()

  const users = useStoreMap($usersMapStore, s => s.filter(u => chatUserIds.includes(u.id)))
  const messages = useStoreMap($messagesStore, s => s.get(props.chatType) || List<ChatMessage>())
  const speeches = splitSpeeches(messages, users)

  return (
    <div>
      {speeches.map((s, idx) => <TextSpeechComponent key={idx} speech={s} />)}
    </div>
  )
}
