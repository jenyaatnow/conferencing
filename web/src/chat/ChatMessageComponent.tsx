import {ChatMessage} from './model'

interface ChatMessageComponentProps {
  message: ChatMessage
}

export const ChatMessageComponent = (props: ChatMessageComponentProps) => {
  return <div>{props.message.text}</div>
}
