import {TextSpeech} from './textSpeech'
import {Typography} from '@mui/material'
import {ChatMessageComponent} from './ChatMessageComponent'

interface TextSpeechComponentProps {
  speech: TextSpeech
}

export const TextSpeechComponent = (props: TextSpeechComponentProps) => {
  return (
    <div>
      <Typography sx={{fontWeight: 'bold'}}>{props.speech.user.username}{!props.speech.user.online && ' [offline]'}</Typography>
      {props.speech.messages.map((m, idx) => <ChatMessageComponent key={idx} message={m}/>)}
    </div>
  )
}
