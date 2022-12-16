import {ChatMessage, ChatType} from './model'
import {List} from 'immutable'
import {useStore, useStoreMap} from 'effector-react'
import {$messagesStore} from './store'
import {$usersMapStore, UserId} from '../users'
import {splitSpeeches} from './textSpeech'
import {Box, Grid, Paper} from '@mui/material'
import {GlobalIndent} from '../globalStyles'
import {buildChatMessageReceived, send} from '../ws'
import {$currentUserStore} from '../auth'
import {TextInput} from './TextInput'
import TextSpeechComponent from './TextSpeechComponent'

interface ChatComponentProps {
  chatType: ChatType
  dmUserId?: UserId
}

// todo 1. obtain all previous messages on connection
//      2. delivery report
//      3. DMs
export const ChatComponent = (props: ChatComponentProps) => {
  const currentUser = useStore($currentUserStore)
  const users = useStore($usersMapStore)
  const messages = useStoreMap($messagesStore, s => s.get(props.chatType) || List<ChatMessage>())
  const speeches = splitSpeeches(messages, users)

  const handlePressEnter = (text: string) => {
    send(buildChatMessageReceived({
      chatType: props.chatType,
      from: currentUser.id,
      to: props.dmUserId,
      text: text
    }))
  }

  return (
    <Paper variant={'outlined'} sx={{
      padding: GlobalIndent,
      height: '100%',
    }}>
      <Grid container direction={'column'} justifyContent={'space-between'} sx={{height: '100%', flexWrap: 'nowrap'}}>
        <Grid item sx={{overflowY: 'auto', marginBottom: GlobalIndent}}>
          {speeches.map((s, idx) => <Box key={idx} sx={{paddingBottom: 1}}><TextSpeechComponent speech={s}/></Box>)}
        </Grid>
        <Grid item>
          <TextInput onPressEnter={handlePressEnter}/>
        </Grid>
      </Grid>
    </Paper>
  )
}
