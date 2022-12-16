import {TextSpeech} from './textSpeech'
import {Grid, Typography} from '@mui/material'
import {ChatMessageComponent} from './ChatMessageComponent'
import {useStore} from 'effector-react'
import {$currentUserStore} from '../auth'
import {ChatStrings} from '../strings'
import {deepEq} from '../utils'
import React from 'react'

interface TextSpeechComponentProps {
  speech: TextSpeech
}

const TextSpeechComponent = (props: TextSpeechComponentProps) => {
  const currentUser = useStore($currentUserStore)

  let user = props.speech.user
  return (
    <div>
      <Grid container alignItems={'center'}>
        <Typography sx={{fontWeight: 'bold'}} component="span">{user.username}</Typography>
        {user.id === currentUser.id &&
          <Typography color={'grey'} component="span" sx={{paddingLeft: 1}}>{ChatStrings.You}</Typography>}
        {!user.online &&
          <Typography color={'grey'} component="span" sx={{paddingLeft: 1}}>{ChatStrings.Offline}</Typography>}
      </Grid>
      {props.speech.messages.map((m, idx) => <ChatMessageComponent key={idx} message={m}/>)}
    </div>
  )
}

export default React.memo(TextSpeechComponent, (prevProps, nextProps) => deepEq(prevProps.speech, nextProps.speech))
