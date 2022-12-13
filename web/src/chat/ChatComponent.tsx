import {ChatMessage, ChatType} from './model'
import {List} from 'immutable'
import {useStoreMap} from 'effector-react'
import {$messagesStore} from './store'
import {$usersMapStore, UserId} from '../users'
import {splitSpeeches} from './textSpeech'
import {TextSpeechComponent} from './TextSpeechComponent'
import {Box, Grid, Paper, TextField} from '@mui/material'
import {GlobalIndent} from '../globalStyles'

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
    <Paper variant={'outlined'} sx={{
      padding: GlobalIndent,
      height: '100%',
    }}>
      <Grid container direction={'column'} justifyContent={'space-between'} sx={{height: '100%', flexWrap: 'nowrap'}}>
        <Grid item sx={{overflowY: 'auto', marginBottom: GlobalIndent}}>
          {speeches.map((s, idx) => <Box key={idx} sx={{paddingBottom: 1}}><TextSpeechComponent speech={s}/></Box>)}
        </Grid>
        <Grid item>
          <TextField fullWidth/>
        </Grid>
      </Grid>
    </Paper>
  )
}
