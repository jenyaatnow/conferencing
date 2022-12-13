import {TextSpeech} from './textSpeech'
import {Grid, Typography} from '@mui/material'
import {ChatMessageComponent} from './ChatMessageComponent'
import {useStore} from 'effector-react'
import {$currentUserStore} from '../auth'
import {Chat} from '../strings'

interface TextSpeechComponentProps {
  speech: TextSpeech
}

export const TextSpeechComponent = (props: TextSpeechComponentProps) => {
  const currentUser = useStore($currentUserStore)

  let user = props.speech.user
  return (
    <div>
      <Grid container alignItems={'center'}>
        <Typography sx={{fontWeight: 'bold'}} component="span">{user.username}</Typography>
        {user.id === currentUser.id &&
          <Typography color={'grey'} component="span" sx={{paddingLeft: 1}}>{Chat.You}</Typography>}
        {!user.online &&
          <Typography color={'grey'} component="span" sx={{paddingLeft: 1}}>{Chat.Offline}</Typography>}
      </Grid>
      {props.speech.messages.map((m, idx) => <ChatMessageComponent key={idx} message={m}/>)}
    </div>
  )
}
